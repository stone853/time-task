package com.apex.bss.cjsc.asclient.action.bus;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.apex.bss.cjsc.asclient.service.AsClientService;
import com.apex.bss.cjsc.asclient.service.IfsService;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Lenovo on 2017/3/2.
 */
public class Bus10509 extends Bus{
    protected static final Logger log = Logger.getLogger(Bus10509.class);
    @Override
    public JSONObject invoke(JSONObject req_data){
        IfsService ifsService=new IfsService();
        JSONObject jsonRet = new JSONObject();
        int code=-1;
        String error_msg = "查询客户可用资金失败";
        try{
            if(req_data.getString("cust_no")==null||req_data.getString("cust_no")==""||req_data.getString("cust_no").trim().length()<=0){
                jsonRet.put("code", -1);
                jsonRet.put("note", "客户号不能为空!");
                return jsonRet;
            }
            if(req_data.getString("cust_account")==null||req_data.getString("cust_account")==""||req_data.getString("cust_account").trim().length()<=0){
                jsonRet.put("code", -1);
                jsonRet.put("note", "资金账号不能为空!");
                return jsonRet;
            }

            if(req_data.getString("op_station")==null||req_data.getString("op_station")==""||req_data.getString("op_station").trim().length()<=0){
                jsonRet.put("code", -1);
                jsonRet.put("note", "站点地址不能为空!");
                return jsonRet;
            }
            String op_station=req_data.getString("op_station")+"_DD00";

            AsClientService as_ClientService =new AsClientService();
            Map p_Map=new HashMap();
            p_Map.put("hs_func_no","HS822021");//业务功能号
            p_Map.put("client_id", req_data.getString("cust_no"));//客户编号
            p_Map.put("fund_account",req_data.getString("cust_account"));//资产账户
            Map map_BZJ = as_ClientService.getResultListMap(140345,p_Map);//调用查询保证金接口
            if(null != map_BZJ && (int)map_BZJ.get("errNo")==0){
                List list_BZJ= (List) map_BZJ.get("list");
                Map map_bzj = (Map) list_BZJ.get(0);
                String  result_List= (String) map_bzj.get("resultList");
                List bzj_list= JSON.parseArray(result_List);
                List li=new ArrayList();
                JSONObject json = new JSONObject();
                JSONObject jso=new JSONObject();
                Double enable_balance=0.00;
                DecimalFormat df = new DecimalFormat("0.00");
                BigDecimal bigDecimal=new BigDecimal(enable_balance);
                if(bzj_list.size()>0&&bzj_list!=null){
                    jso= (JSONObject) bzj_list.get(0);
                    enable_balance=jso.getDouble("enable_balance");
                    bigDecimal=new BigDecimal(enable_balance);
                }
                if(req_data.getString("combine_stock_no")!=null&&req_data.getString("combine_stock_no")!=""){
                    Map map_YE =ifsService.queryMoney(op_station,req_data.getString("combine_stock_no"));
                    if(null != map_YE && (int)map_YE.get("errNo")==0) {
                        List list = (List) map_YE.get("list");
                        Map map = (Map) list.get(0);
                        String resultList = (String) map.get("resultList");
                        List re_list = JSON.parseArray(resultList);
                        if (re_list.size() > 0 && re_list != null) {
                            JSONObject json_ye = (JSONObject) re_list.get(0);
                            enable_balance = enable_balance + json_ye.getDouble("enable_balance");
                            bigDecimal = new BigDecimal(enable_balance);
                        }
                    }else{
                        code= (int) map_YE.get("errNo");
                        error_msg = (String) map_YE.get("errMsg");
                    }
                }
                    json.put("enable_balance",df.format(bigDecimal));
                    li.add(json);
                    jsonRet.put("data",li);
                    jsonRet.put("code",1);
                    jsonRet.put("note","查询客户可用资金成功");
                    return jsonRet;
            }else{
                code= (int) map_BZJ.get("errNo");
                error_msg = (String) map_BZJ.get("errMsg");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        jsonRet.put("code",-1);
        jsonRet.put("note","错误代码:"+code+";查询客户可用资金失败:"+error_msg );
        return jsonRet;
    }
}
