package com.apex.bss.cjsc.asclient.action;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.apex.bss.cjsc.asclient.service.AsClientService;
import com.apex.bss.cjsc.asclient.service.BusService;
import com.apex.bss.cjsc.common.Common;
import com.apex.bss.cjsc.dao.OpenPositionDao;
import com.apex.bss.cjsc.redis.RedisUtil;
import com.apex.bss.cjsc.taskn.TaskInfo;
import com.apex.bss.cjsc.taskn.TaskLog;
import com.apex.bss.cjsc.taskn.autoapply.AutoapplyTask_SD;
import com.apex.bss.cjsc.taskn.fundin.FundinTask_SD;
import com.apex.bss.cjsc.taskn.purchase.PurchaseTask_SD;
import com.apex.bss.cjsc.taskn.redemption.ThreadGetTask_SD;
import com.apex.bss.cjsc.taskn.transferfund.TransferFund_SD;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * Created by jinsh on 2017/2/10.
 */
@Controller
@RequestMapping("/processPlatform")
public class SynchronizeProductAction {
    AsClientService as_ClientService =new AsClientService();
    RedisUtil redis=new RedisUtil();
    @Autowired
    private OpenPositionDao openPositionDao;

    @Autowired
    private BusService busService;
    private ApplicationContext ctx;
    @RequestMapping(value = "/synProduct")
    @ResponseBody
    public JSONObject synProduct(HttpServletRequest request, HttpServletResponse response, Model model){
        JSONObject ret = new JSONObject();
        JSONObject req_data = getParams(request);
        String func = req_data.getString("id");
        String error_msg = "组合ID："+func+"同步产品失败";
        int code=-1;
        try{
            System.out.println(func);
            Map map_BD=new HashMap();
            map_BD.put("O_CODE","");
            map_BD.put("O_NOTE","");
            map_BD.put("O_RESULT",new ArrayList<Map<String,Object>>());
            map_BD.put("I_FAID",func);
            openPositionDao.synchronizeProduct(map_BD);
            System.out.println(map_BD.toString());
            if(null != map_BD && Integer.parseInt(map_BD.get("O_CODE").toString())>0){
                List list= (List) map_BD.get("O_RESULT");
                Map map= (Map) list.get(0);
                String cpdm= (String) map.get("cpdm");
                String []combine=cpdm.split("\\;");
                List listSum = new ArrayList();
                for(int i=0;i<combine.length;i++){
                    String []combine_data=combine[i].split("\\|");
                    if(null != combine_data && combine_data.length >=3){
                        JSONObject json = new JSONObject();
                        json.put("fund_code",combine_data[0]);
                        json.put("fund_company",combine_data[1]);
                        json.put("deal_flag",combine_data[2]);
                        listSum.add(i,json);
                    }
                }
                System.out.println(listSum);
                Map paramMap = new HashMap();
                paramMap.put("hs_func_no","HS855077");//业务功能号
                paramMap.put("op_branch_no", Common.op_branch_no);//操作分支机构
                paramMap.put("operator_no",Common.operator_no);//操作员编号
                paramMap.put("user_type", Common.user_type);//用户类别
                paramMap.put("op_password",Common.op_password);//操作员密码
                paramMap.put("op_station",10);//站点地址
                paramMap.put("combine_name",map.get("mc"));//名称
                paramMap.put("combine_type",map.get("zhlx"));//组合类型(1,智能理财)
                paramMap.put("combine_serial",map.get("bm"));//产品池编号
                paramMap.put("en_channel_type",map.get("yxwtqd"));//允许委托渠道
                paramMap.put("en_entrust_way",map.get("yxwtfs"));//允许委托方式
                paramMap.put("combine_status",map.get("zt"));//组合状态
                paramMap.put("remark","1");//备注
                paramMap.put("combine_data",listSum);//成份产品
                paramMap.put("update_flag",req_data.getString("update_flag"));//成份产品
                Map map_TB = as_ClientService.getResultListMap(140346 ,paramMap);//调用产品同步接口
                if(null != map_TB && (int)map_TB.get("errNo")==0) {
                    List li = (List) map_TB.get("list");
                    ret.put("data",li);
                    ret.put("code",1);
                    ret.put("note","success");
                    return ret;
                }else{
                    code=(int)map_TB.get("errNo");
                    error_msg= (String) map_TB.get("errMsg");
                }
            }else{
                error_msg ="存储过程查询产品同步信息出错";
            }
        }catch(Exception e){
            error_msg ="调用产品同步接口出错";
            e.printStackTrace();
            return ret;
        }
        ret.put("code",code);
        ret.put("note",error_msg);
        return ret;
    }

    @RequestMapping(value = "/cancel")
    @ResponseBody
    public JSON cancel(HttpServletRequest request, HttpServletResponse response, Model model){
        JSONObject req_data = getParams(request);
        req_data.put("entrust_way","4");
        req_data.put("ofchannel_type","b");
        return busService.cancel(req_data);
    }

    @RequestMapping(value = "/auto")
    @ResponseBody
    public JSON auto(HttpServletRequest request, HttpServletResponse response, Model model){
        JSONObject req_data = getParams(request);
        TaskLog taskLog = TaskLog.getTaskLog();
        String error_msg = "定时任务启动失败";
        try{
            String flag=req_data.getString("flag");
            String op_station=req_data.getString("op_station");
            switch (flag){
                case "1":
                    if(null == redis.getKey("redemisrun") || "".equals(redis.getKey("redemisrun"))){
                        if(null == redis.getKey("masterID") || "".equals(redis.getKey("masterID"))){
                            taskLog.insMasterLog(openPositionDao,"1","2","定时任务开始");
                        }
                        new ThreadGetTask_SD(openPositionDao,op_station).start();
                        req_data.put("code",1);
                        req_data.put("note","定时任务1启动成功");
                    }else{
                        req_data.put("code",-1);
                        req_data.put("note","定时任务1正在执行");
                        return req_data;
                    }
                    break;
                case "2":
                    if(null == redis.getKey("fundinisrun") || "".equals(redis.getKey("fundinisrun"))){
                        if(null == redis.getKey("masterID") || "".equals(redis.getKey("masterID"))){
                            taskLog.insMasterLog(openPositionDao,"1","2","定时任务开始");
                        }
                        new FundinTask_SD(openPositionDao,op_station).start();
                        req_data.put("code",1);
                        req_data.put("note","定时任务2启动成功");
                    }else{
                        req_data.put("code",-1);
                        req_data.put("note","定时任务2正在执行");
                        return req_data;
                    }
                    break;
                case "3":
                    if(null == redis.getKey("autoisrun") || "".equals(redis.getKey("autoisrun"))){
                        if(null == redis.getKey("masterID") || "".equals(redis.getKey("masterID"))){
                            taskLog.insMasterLog(openPositionDao,"1","2","定时任务开始");
                        }
                        new AutoapplyTask_SD(openPositionDao,op_station).start();
                        req_data.put("code",1);
                        req_data.put("note","定时任务3启动成功");
                        if( TaskInfo.autoapplyIsDone&&TaskInfo.autoapplyIsDone){
                            taskLog.updMasterLog(openPositionDao,"2","1","定时任务结束");
                        }
                    }else{
                        req_data.put("code",-1);
                        req_data.put("note","定时任务3正在执行");
                        return req_data;
                    }
                    break;
                case "4":
                    if(null == redis.getKey("purchaseisrun") || "".equals(redis.getKey("purchaseisrun"))){
                        if(null == redis.getKey("masterID") || "".equals(redis.getKey("masterID"))){
                            taskLog.insMasterLog(openPositionDao,"1","2","定时任务开始");
                        }
                        new PurchaseTask_SD(openPositionDao,op_station).start();
                        req_data.put("code",1);
                        req_data.put("note","定时任务4启动成功");
                        if( TaskInfo.autoapplyIsDone&&TaskInfo.autoapplyIsDone){
                            taskLog.updMasterLog(openPositionDao,"2","1","定时任务结束");
                        }
                    }else{
                        req_data.put("code",-1);
                        req_data.put("note","定时任务4正在执行");
                        return req_data;
                    }
                    break;
                case "5":
                    new TransferFund_SD(openPositionDao,op_station).start();
                    req_data.put("code",1);
                    req_data.put("note","定时任务余额转货基启动成功");
                    break;
                default:
                    break;
            }
            return req_data;
        }catch(Exception e){
            error_msg ="定时任务启动出错";
        }
        req_data.put("code",-1);
        req_data.put("note",error_msg);
        return req_data;
    }

    @RequestMapping(value = "/saveConfig")
    @ResponseBody
    public JSON saveConfig(HttpServletRequest request, HttpServletResponse response, Model model){
        JSONObject req_data = getParams(request);
        req_data.put("entrust_way","4");
        req_data.put("ofchannel_type","b");
        return busService.modify(req_data);
    }


    @RequestMapping(value = "/purchase")
    @ResponseBody
    public JSONObject purchase(HttpServletRequest request, HttpServletResponse response, Model model){
        JSONObject req_data = getParams(request);
        String combine_data = req_data.getString("combine_data");
        List combine_list = new ArrayList();
        String[] data = combine_data.split("\\;");
        for (int i = 0; i < data.length; i++) {
            String[] combine = data[i].split("\\,");
            if (combine != null && combine.length > 3) {
                JSONObject js = new JSONObject();
                js.put("fund_code", combine[0]);
                js.put("fund_company", combine[1]);
                js.put("balance", combine[2]);
                js.put("product_id", combine[3]);
                js.put("business_flag", "22");
                combine_list.add(i,js);
            }
        }
        req_data.put("combine_data",combine_list.toString());
        req_data.put("entrust_way","4");
        req_data.put("ofchannel_type","b");
        return busService.purchase(req_data);
    }



    @RequestMapping(value = "/redeemof")
    @ResponseBody
    public JSON redeemof(HttpServletRequest request, HttpServletResponse response, Model model){
        JSONObject req_data = getParams(request);
        String combine_data = req_data.getString("combine_data");
        List combine_list = new ArrayList();
        if(combine_data!=null&&combine_data!=""){
            String[] data = combine_data.split("\\;");
            for (int i = 0; i < data.length; i++) {
                String[] combine = data[i].split("\\,");
                if (combine != null && combine.length >=3) {
                    JSONObject js = new JSONObject();
                    js.put("fund_code", combine[0]);
                    js.put("fund_company", combine[1]);
                    js.put("amount", combine[2]);
                    if(Double.parseDouble(combine[2])>0.00){
                        combine_list.add(js);
                    }
                }
            }
        }
        req_data.put("combine_data",combine_list.toString());
        req_data.put("entrust_way","4");
        req_data.put("ofchannel_type","b");
        return busService.redeemof(req_data);
    }



    @RequestMapping(value = "/redeemall")
    @ResponseBody
    public JSON redeemall(HttpServletRequest request, HttpServletResponse response, Model model){
        JSONObject req_data = getParams(request);
        String combine_data = req_data.getString("combine_data");
        List combine_list = new ArrayList();
        if(combine_data!=null&&combine_data!=""){
            String[] data = combine_data.split("\\;");
            for (int i = 0; i < data.length; i++) {
                String[] combine = data[i].split("\\,");
                if (combine != null && combine.length >=3) {
                    JSONObject js = new JSONObject();
                    js.put("fund_code", combine[0]);
                    js.put("fund_company", combine[1]);
                    js.put("amount", combine[2]);
                    combine_list.add(i,js);
                }
            }
        }
        req_data.put("combine_data",combine_list.toString());
        req_data.put("entrust_way","4");
        req_data.put("ofchannel_type","b");
        return busService.redeemall(req_data);
    }


    @RequestMapping(value = "/append")
    @ResponseBody
    public JSON append(HttpServletRequest request, HttpServletResponse response, Model model){
        JSONObject req_data = getParams(request);
        String combine_data = req_data.getString("combine_data");
        List combine_list = new ArrayList();
        String[] data = combine_data.split("\\;");
        for (int i = 0; i < data.length; i++) {
            String[] combine = data[i].split("\\,");
            if (combine != null && combine.length >=3) {
                JSONObject js = new JSONObject();
                js.put("fund_code", combine[0]);
                js.put("fund_company", combine[1]);
                js.put("balance", combine[2]);
                js.put("business_flag", "22");
                combine_list.add(i,js);
            }
        }
        req_data.put("oper_type",req_data.getString("operType"));
        req_data.put("combine_data",combine_list.toString());
        req_data.put("entrust_way","4");
        req_data.put("ofchannel_type","b");
        return busService.append(req_data);
    }

    @RequestMapping(value = "/queryMoney")
    @ResponseBody
    public JSON queryMoney(HttpServletRequest request, HttpServletResponse response, Model model){
        JSONObject jsonRet = new JSONObject();
        JSONObject req_data = getParams(request);
        String error_msg = "查询账户资金失败";
        int code=-1;
        try{
            System.out.println(req_data);
            Map pramMap = new HashMap();
            pramMap.put("hs_func_no","HS855912");//业务功能号
            pramMap.put("op_branch_no",Common.op_branch_no);//操作分支机构
            pramMap.put("operator_no",Common.operator_no);//操作员编号
            pramMap.put("user_type", Common.user_type);//用户类别
            pramMap.put("op_password",Common.op_password);//操作员密码
            pramMap.put("op_station",req_data.getString("op_station"));//站点地址
            pramMap.put("client_id","");//客户编号
            pramMap.put("fund_account","");//资产账户
            pramMap.put("request_num","");//请求行数
            pramMap.put("combine_serial","");//产品池编号
            pramMap.put("combine_stock_no",req_data.getString("combine_stock_no"));//持仓编号
            pramMap.put("position_str","");//定位串
            Map map_YE = as_ClientService.getResultListMap(140346,pramMap);//调用查询组合余额接口
            if(null != map_YE && (int)map_YE.get("errNo")==0){
                List list= (List) map_YE.get("list");
                Map m= (Map) list.get(0);
                jsonRet.put("data",m.get("resultList"));
                jsonRet.put("code",1);
                jsonRet.put("note","success");
                return jsonRet;
            }else{
                code= (int) map_YE.get("errNo");
                error_msg = (String) map_YE.get("errMsg");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        jsonRet.put("code",code);
        jsonRet.put("note","调用查询账户金额接口错误:"+error_msg );
        return jsonRet;
    }

    private JSONObject getParams(HttpServletRequest request){
        JSONObject req_data = new JSONObject();
        Enumeration paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = (String) paramNames.nextElement();

            String[] paramValues = request.getParameterValues(paramName);
            if (paramValues.length == 1) {
                String paramValue = paramValues[0];
                if (paramValue.length() != 0) {
                    req_data.put(paramName, paramValue);
                }
            }
        }
        return req_data;
    }

    public void setOpenPositionDao(OpenPositionDao openPositionDao) {
        this.openPositionDao = openPositionDao;
    }

}
