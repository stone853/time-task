package com.apex.bss.cjsc.asclient.service;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.apex.bss.cjsc.base.ConfigUtil;
import com.apex.bss.cjsc.common.Common;
import com.apex.bss.cjsc.dao.OpenPositionDao;
import com.apex.bss.cjsc.redis.RedisUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * Created by jinsh on 2017/3/29.
 */
public class BusService {
    protected static final Logger log = Logger.getLogger(BusService.class);
    IfsService ifsService=new IfsService();
    @Autowired
    private OpenPositionDao openPositionDao;
    //首次申购
    public JSONObject purchase(JSONObject req_data){
        long start = System.currentTimeMillis();
        RedisUtil redis=new RedisUtil();
        JSONObject jsonRet = new JSONObject();

        String error_msg = "首次申购失败";
        String ZHBH = "";
        int code=-1;
        try{
            if(req_data.getString("cust_type")==null||req_data.getString("cust_type")==""||req_data.getString("cust_type").trim().length()<=0){
                jsonRet.put("code", -1);
                jsonRet.put("note", "客户类型不能为空!");
                return jsonRet;
            }
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
            if(req_data.getString("combine_code")==null||req_data.getString("combine_code")==""||req_data.getString("combine_code").trim().length()<=0){
                jsonRet.put("code", -1);
                jsonRet.put("note", "组合编码不能为空!");
                return jsonRet;
            }
            if(req_data.getString("target_amount")==null||req_data.getString("target_amount")==""||req_data.getString("target_amount").trim().length()<=0){
                jsonRet.put("code", -1);
                jsonRet.put("note", "目标金额不能为空!");
                return jsonRet;
            }
            if(req_data.getString("init_amount")==null||req_data.getString("init_amount")==""||req_data.getString("init_amount").trim().length()<=0){
                jsonRet.put("code", -1);
                jsonRet.put("note", "首次申购金额不能为空!");
                return jsonRet;
            }
            if(req_data.getString("combine_data")==null||req_data.getString("combine_data")==""||req_data.getString("combine_data").trim().length()<=0||
                    req_data.getString("combine_data")=="[]"){
                jsonRet.put("code", -1);
                jsonRet.put("note", "组合申购产品成份不能为空!");
                return jsonRet;
            }
            if(req_data.getString("aip_amount")==null||req_data.getString("aip_amount")==""||req_data.getString("aip_amount").trim().length()<=0){
                jsonRet.put("code", -1);
                jsonRet.put("note", "定投金额不能为空!");
                return jsonRet;
            }
            if(req_data.getString("aip_date")==null||req_data.getString("aip_date")==""||req_data.getString("aip_date").trim().length()<=0){
                jsonRet.put("code", -1);
                jsonRet.put("note", "定投日不能为空!");
                return jsonRet;
            }
            if(req_data.getString("invest_period")==null||req_data.getString("invest_period")==""||req_data.getString("invest_period").trim().length()<=0){
                jsonRet.put("code", -1);
                jsonRet.put("note", "投资年限不能为空!");
                return jsonRet;
            }
            if(req_data.getString("op_station")==null||req_data.getString("op_station")==""||req_data.getString("op_station").trim().length()<=0){
                jsonRet.put("code", -1);
                jsonRet.put("note", "站点地址不能为空!");
                return jsonRet;
            }
            Map map_time=new HashMap();
            map_time.put("O_CODE","");
            map_time.put("O_NOTE","");
            log.debug("交易时间入参:"+map_time.toString());
            long d1 = System.currentTimeMillis();
            openPositionDao.isTradeTime(map_time);//调用本地过程判断是否是交易时间
            long d2 = System.currentTimeMillis();
            System.out.println("调用本地过程判断是否是交易时间执行时间"+(d2-d1));
            log.debug("交易时间出参:"+map_time.toString());
            if(null != map_time && Integer.parseInt(map_time.get("O_CODE").toString())==1){
                Map map_power=new HashMap();
                map_power.put("O_CODE","");
                map_power.put("O_NOTE","");
                map_power.put("I_KHH", req_data.getString("cust_no"));
                map_power.put("I_ZJZH",req_data.getString("cust_account"));
                map_power.put("I_YWLXBM",req_data.getString("combine_code"));
                log.debug("查询开仓入参:"+map_power.toString());
                long d3 = System.currentTimeMillis();
                openPositionDao.isOpen(map_power);//调用本地过程判断是否已开仓
                long d4 = System.currentTimeMillis();
                System.out.println("调用本地过程判断是否已开仓执行时间"+(d4-d3));
                log.debug("查询开仓出参:"+map_power.toString());
                if(null != map_power && Integer.parseInt(map_power.get("O_CODE").toString())==1){
                    String combine_data= req_data.getString("combine_data");
                    List combine_list=new ArrayList();
                    try {
                        combine_list = JSON.parseArray(combine_data);
                    }catch(Exception e){
                        e.printStackTrace();
                        error_msg ="组合申购产品成份格式不正确";
                        jsonRet.put("code",-1);
                        jsonRet.put("note","错误代码:"+code+";首次申购失败:"+error_msg );
                        return jsonRet;
                    }
                        StringBuffer buffer = new StringBuffer();
                        StringBuffer buff = new StringBuffer();
                        for (int i = 0; i < combine_list.size(); i++) {
                            JSONObject js = new JSONObject();
                            js = (JSONObject) combine_list.get(i);
                            buffer.append(js.getString("fund_code") + "|" + js.getString("fund_company") + "|" + js.getString("balance") + ";");
                            buff.append(js.getString("product_id") + ";");
                        }
                        buffer.deleteCharAt(buffer.length() - 1);
                        buff.deleteCharAt(buff.length() - 1);
                        String cpdm = String.valueOf(buffer);
                        String cpid = String.valueOf(buff);
                        //调用IFS开仓接口
                    long s1 = System.currentTimeMillis();
                        Map map_KC = ifsService.fundOrderSend(req_data.getString("op_station"), "", req_data.getString("cust_no"),
                                req_data.getString("cust_account"), req_data.getString("combine_code"));
                    long s2 = System.currentTimeMillis();
                    System.out.println("调用IFS开仓接口"+(s2-s1));
                        if (null != map_KC && (int) map_KC.get("errNo") == 0) {
                            List list = (List) map_KC.get("list");
                            Map m = (Map) list.get(0);
                            //调用IFS资金划拨接口
                            long d5 = System.currentTimeMillis();
                            Map map_HK = ifsService.capitalAllocation(req_data.getString("op_station"), "", req_data.getString("cust_no"),
                                    req_data.getString("cust_account"), m.get("combine_stock_no").toString(), req_data.getDouble("init_amount"), "0", "0");
                            long d6 = System.currentTimeMillis();
                            System.out.println("调用IFS资金划拨接口执行时间"+(d6-d5));
                            if (null != map_HK && (int) map_HK.get("errNo") == 0) {//划款成功
                                List li = (List) map_HK.get("list");
                                Map m_li = (Map) li.get(0);
                                Map map_BD = new HashMap();
                                map_BD.put("O_CODE", "");
                                map_BD.put("O_NOTE", "");
                                map_BD.put("I_KHLX", req_data.getString("cust_type"));
                                map_BD.put("I_CZLX", 1);
                                map_BD.put("I_KHH", req_data.getString("cust_no"));
                                map_BD.put("I_ZJZH", req_data.getString("cust_account"));
                                map_BD.put("I_ZHBH", m.get("combine_stock_no"));
                                map_BD.put("I_YWLX", req_data.getString("combine_code"));
                                map_BD.put("I_KTQD", "app");
                                log.debug("存入账户信息入参"+map_BD.toString());
                                long d7 = System.currentTimeMillis();
                                openPositionDao.openNew(map_BD);//调用本地过程存入账户信息
                                long d8 = System.currentTimeMillis();
                                System.out.println("调用本地过程存入账户信息"+(d8-d7));
                                log.debug("存入账户信息出参"+map_BD.toString());
                                if (null != map_BD && Integer.parseInt(map_BD.get("O_CODE").toString()) == 1) {
                                    long d9 = System.currentTimeMillis();
                                    Map map_sg = ifsService.fundPurchase(req_data.getString("op_station"), "", req_data.getString("cust_no"),
                                            req_data.getString("cust_account"), m.get("combine_stock_no").toString(),req_data.getString("ofchannel_type"),
                                            req_data.getString("entrust_way"), req_data.getString("combine_data").toString());
                                    long d10 = System.currentTimeMillis();
                                    System.out.println("IFS组合申购接口执行时间"+(d10-d9));
                                    if (null != map_sg && (int) map_sg.get("errNo") == 0) {
                                        List list_sg = (List) map_sg.get("list");

                                        Map map_zt = (Map) list_sg.get(0);
                                        jsonRet.put("data", list_sg);
                                        jsonRet.put("code", 1);
                                        jsonRet.put("note", "首次申购成功");
                                        //异步写入插入redis
                                        long d11 = System.currentTimeMillis();
                                        JSONObject json_ph = ifsService.firstPurchase(req_data.getString("cust_type"), req_data.getString("cust_no"), req_data.getString("cust_account"),
                                                m.get("combine_stock_no").toString(), req_data.getString("combine_code"), req_data.getInteger("aip_date"), req_data.getDouble("aip_amount"),
                                                req_data.getInteger("invest_period"), req_data.getDouble("init_amount"), req_data.getDouble("target_amount"), req_data.getString("ratio_code"),
                                                cpid, cpdm, map_zt.get("combine_entrust_no").toString(), map_zt.get("combine_entrust_no").toString(), map_zt.get("entrust_status").toString(),
                                                req_data.getDouble("init_amount"), req_data.getString("op_station"));
                                        long d12 = System.currentTimeMillis();
                                        System.out.println("异步写入首次申购 PRO_PIF_JYCZ_SCSG执行时间"+(d12-d11));
                                        redis.lpush(Common.PRO_PIF_JYCZ_SCSG, json_ph.toJSONString());
                                        long end = System.currentTimeMillis();
                                        System.out.println("调用首次申购接口成功时间为:"+(end - start));
                                        return jsonRet;
                                    } else {
                                        ZHBH = m.get("combine_stock_no").toString();
                                        Map map_Lsh = new HashMap();
                                        map_Lsh.put("O_CODE", "");
                                        map_Lsh.put("O_NOTE", "");
                                        map_Lsh.put("I_ZHBH",ZHBH);
                                        log.debug("申购失败返回信息入参:"+map_Lsh.toString());
                                        long d13 = System.currentTimeMillis();
                                        openPositionDao.purchaseErr(map_Lsh);
                                        long d14 = System.currentTimeMillis();
                                        System.out.println("申购失败返回信息执行时间"+(d14-d13));
                                        log.debug("申购失败返回信息出参:"+map_Lsh.toString());
                                        //获取游标
                                        List l1 = (List) map_Lsh.get("O_RESULT");
                                        //获取流水号
                                        Map m1 = (Map)l1.get(0);
                                        String LSH = m1.get("LSH").toString();
                                        List li_PZ  = new ArrayList();
                                        Map<String,Object> map2 = new HashMap<>();
                                        map2.put("entrust_status","");
                                        map2.put("combine_stock_no","");
                                        map2.put("combine_entrust_no",LSH);
                                        li_PZ.add(map2);
                                        jsonRet.put("data",li_PZ);
                                        //获取状态信息
                                         code =(int) map_sg.get("errNo");
                                        //获取错误信息
                                        error_msg = map_sg.get("errMsg").toString();
                                        jsonRet.put("code",code);
                                        jsonRet.put("note","错误代码:"+code+";首次申购失败:"+error_msg );
                                        //异步写入插入redis
                                        JSONObject json_ph = ifsService.firstPurchase(req_data.getString("cust_type"), req_data.getString("cust_no"), req_data.getString("cust_account"),
                                                m.get("combine_stock_no").toString(), req_data.getString("combine_code"), req_data.getInteger("aip_date"), req_data.getDouble("aip_amount"),
                                                req_data.getInteger("invest_period"), req_data.getDouble("init_amount"), req_data.getDouble("target_amount"), req_data.getString("ratio_code"),
                                                cpid, cpdm, "", "", "9", req_data.getDouble("init_amount"), req_data.getString("op_station"));
                                        redis.lpush(Common.PRO_PIF_JYCZ_SCSG, json_ph.toJSONString());
                                        long end = System.currentTimeMillis();
                                        System.out.println("调用首次申购接口失败时间为:"+(end - start));
                                        return jsonRet;
                                    }
                                } else {
                                    error_msg = "存入账户信息出错";
                                }
                            } else {
                                code = (int) map_HK.get("errNo");
                                error_msg = (String) map_HK.get("errMsg");
                                //调用IFS平仓接口
                                long d15 = System.currentTimeMillis();
                                Map map_PC = ifsService.fundLiquidation(req_data.getString("op_station"), "", req_data.getString("cust_no"), req_data.getString("cust_account"),
                                        m.get("combine_stock_no").toString());
                                long d16 = System.currentTimeMillis();
                                System.out.println("调用IFS平仓接口执行时间"+(d16-d15));
                                if (null != map_PC && (int) map_PC.get("errNo") == 0) {
                                    List list_PC = (List) map_PC.get("list");
                                }
                            }
                        } else {
                            code = (int) map_KC.get("errNo");
                            error_msg = (String) map_KC.get("errMsg");
                        }
                }else{
                    error_msg = map_power.get("O_NOTE").toString();
                }
            }else{
                code = (int) map_time.get("O_CODE");
                error_msg = map_time.get("O_NOTE").toString();
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        jsonRet.put("code",-1);
        jsonRet.put("note","错误代码:"+code+";首次申购失败:"+error_msg );
        long end = System.currentTimeMillis();
        System.out.println("调用首次申购接口失败ifs错误时间为:"+(end - start));
        return jsonRet;
    }


    //追加申购
    public JSONObject append(JSONObject req_data){
        JSONObject jsonRet = new JSONObject();
        RedisUtil redis=new RedisUtil();
        String error_msg="";
        int code=-1;
        int flag = 0;
        try{
            if(req_data.getString("oper_type")==null||req_data.getString("oper_type")==""||req_data.getString("oper_type").trim().length()<=0){
                jsonRet.put("code", -1);
                jsonRet.put("note", "操作类型不能为空!");
                return jsonRet;
            }
            if(req_data.getString("cust_type")==null||req_data.getString("cust_type")==""||req_data.getString("cust_type").trim().length()<=0){
                jsonRet.put("code", -1);
                jsonRet.put("note", "客户类型不能为空!");
                return jsonRet;
            }
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
            if(req_data.getString("combine_stock_no")==null||req_data.getString("combine_stock_no")==""||req_data.getString("combine_stock_no").trim().length()<=0){
                jsonRet.put("code", -1);
                jsonRet.put("note", "组合持仓编号不能为空!");
                return jsonRet;
            }
            if(req_data.getString("add_amount")==null||req_data.getString("add_amount")==""||req_data.getString("add_amount").trim().length()<=0){
                jsonRet.put("code", -1);
                jsonRet.put("note", "追加申购金额不能为空!");
                return jsonRet;
            }
            if(req_data.getString("combine_data")==null||req_data.getString("combine_data")==""||req_data.getString("combine_data").trim().length()<=0||
                    req_data.getString("combine_data")=="[]"){
                jsonRet.put("code", -1);
                jsonRet.put("note", "组合追加申购产品成份不能为空!");
                return jsonRet;
            }
            if(req_data.getString("op_station")==null||req_data.getString("op_station")==""||req_data.getString("op_station").trim().length()<=0){
                jsonRet.put("code", -1);
                jsonRet.put("note", "站点地址不能为空!");
                return jsonRet;
            }
            String combine_data= req_data.getString("combine_data");
            log.info(combine_data);
            JSONArray combine_list=new JSONArray();
            try {
                combine_list=JSON.parseArray(combine_data);
                log.info(combine_list);
            }catch(Exception e){
                e.printStackTrace();
                error_msg ="组合申购产品成份格式不正确";
                jsonRet.put("code",-1);
                jsonRet.put("note","错误代码:"+code+";追加申购失败:"+error_msg );
                return jsonRet;
            }
                StringBuffer buffer = new StringBuffer();
                for(int i=0;i<combine_list.size();i++){
                    JSONObject js = (JSONObject) combine_list.get(i);
                    buffer.append(js.getString("fund_code")+"|"+js.getString("fund_company")+"|"+js.getString("balance")+";");
                }
                buffer.deleteCharAt(buffer.length()-1);
                String cpdm= String.valueOf(buffer);
                Map map_time=new HashMap();
                map_time.put("O_CODE","");
                map_time.put("O_NOTE","");
                log.debug("交易时间入参:"+map_time.toString());
                openPositionDao.isTradeTime(map_time);//调用本地过程判断是否是交易时间
                log.debug("交易时间出参:"+map_time.toString());

                error_msg = "追加申购失败";
                if(null != map_time && Integer.parseInt(map_time.get("O_CODE").toString())==1){
                    Map map_YE =ifsService.queryMoney(req_data.getString("op_station"),req_data.getString("combine_stock_no"));
                    if(null != map_YE && (int)map_YE.get("errNo")==0){
                        List list= (List) map_YE.get("list");
                        Map map = (Map) list.get(0);
                        String  resultList= (String) map.get("resultList");
                        List re_list= JSON.parseArray(resultList);
                        if(re_list == null || re_list.size() == 0) {
                            error_msg="未查询到用户资金信息";
                            jsonRet.put("code",-1);
                            jsonRet.put("note","错误代码:"+code+";追加申购失败:"+error_msg );
                            return jsonRet;
                        }
                        JSONObject jso= (JSONObject) re_list.get(0);
                        if(jso.getDouble("enable_balance")<req_data.getDouble("add_amount")){//可用资金小于划拨金额 调用划拨接口
                            Map map_HK =ifsService.capitalAllocation(req_data.getString("op_station"),"",req_data.getString("cust_no"),req_data.getString("cust_account"),
                                    req_data.getString("combine_stock_no"),req_data.getDouble("add_amount"),"0","0");
                            if(null!=map_HK &&(int)map_HK.get("errNo")==0){
                                 flag = 1;
                            }else{
                                code= (int) map_HK.get("errNo");
                                error_msg = (String) map_HK.get("errMsg");
                            }
                        }
                        Map map_sg =ifsService.fundPurchase(req_data.getString("op_station"),"",req_data.getString("cust_no"),req_data.getString("cust_account"),
                                req_data.getString("combine_stock_no"),req_data.getString("ofchannel_type"),req_data.getString("entrust_way"),req_data.getString("combine_data"));
                        if(null != map_sg && (int)map_sg.get("errNo")==0){
                            List li= (List) map_sg.get("list");
                            Map map_zt= (Map) li.get(0);
                            jsonRet.put("data",li);
                            jsonRet.put("code",1);
                            jsonRet.put("note","追加申购成功");

                            JSONObject json_jy=ifsService.tradingOperation(req_data.getString("oper_type"),req_data.getString("cust_type"),req_data.getString("cust_no"),
                                    req_data.getString("cust_account"),req_data.getString("combine_stock_no"),1,cpdm,"",req_data.getDouble("add_amount"),
                                    req_data.getString("op_station"),map_zt.get("combine_entrust_no").toString(), map_zt.get("combine_entrust_no").toString(),
                                    map_zt.get("entrust_status").toString());
                            redis.lpush(Common.PRO_PIF_ZHJY_JYCZ,json_jy.toJSONString());
                            return jsonRet;
                        }else{
                            code= (int) map_sg.get("errNo");
                            error_msg = (String) map_sg.get("errMsg");
                            if(flag==1){
                                error_msg = "申购资金已划转至目标理财封闭账户，可自行赎回";
                            }

                            JSONObject json_jy=ifsService.tradingOperation(req_data.getString("oper_type"),req_data.getString("cust_type"),req_data.getString("cust_no"),
                                    req_data.getString("cust_account"),req_data.getString("combine_stock_no"),1,cpdm,"",req_data.getDouble("add_amount"),
                                    req_data.getString("op_station"),"","","9");
                            redis.lpush(Common.PRO_PIF_ZHJY_JYCZ,json_jy.toJSONString());
                        }
                    }else{
                        code= (int) map_YE.get("errNo");
                        error_msg = (String) map_YE.get("errMsg");
                    }
                }else{
                     error_msg = map_time.get("O_NOTE").toString();
                }
        }catch(Exception e){
            e.printStackTrace();
        }
        jsonRet.put("code",-1);
        jsonRet.put("note","错误代码:"+code+";追加申购失败:"+error_msg );
        return jsonRet;
    }
    //部分赎回
    public JSONObject redeemof(JSONObject req_data){
        JSONObject jsonRet = new JSONObject();
        RedisUtil redis=new RedisUtil();
        String error_msg="";
        String cpdm="";
        int code=-1;
        try{
            if(req_data.getString("cust_type")==null||req_data.getString("cust_type")==""||req_data.getString("cust_type").trim().length()<=0){
                jsonRet.put("code", -1);
                jsonRet.put("note", "客户类型不能为空!");
                return jsonRet;
            }
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
            if(req_data.getString("combine_stock_no")==null||req_data.getString("combine_stock_no")==""||req_data.getString("combine_stock_no").trim().length()<=0){
                jsonRet.put("code", -1);
                jsonRet.put("note", "组合持仓编号不能为空!");
                return jsonRet;
            }
            if(req_data.getString("redeem_amount")==null||req_data.getString("redeem_amount")==""||req_data.getString("redeem_amount").trim().length()<=0){
                jsonRet.put("code", -1);
                jsonRet.put("note", "赎回金额不能为空!");
                return jsonRet;
            }
            if(req_data.getString("capital_balance")==null||req_data.getString("capital_balance")==""||req_data.getString("capital_balance").trim().length()<=0){
                jsonRet.put("code", -1);
                jsonRet.put("note", "可用资金余额不能为空!");
                return jsonRet;
            }
            if(req_data.getString("op_station")==null||req_data.getString("op_station")==""||req_data.getString("op_station").trim().length()<=0){
                jsonRet.put("code", -1);
                jsonRet.put("note", "站点地址不能为空!");
                return jsonRet;
            }
            Map map_time=new HashMap();
            map_time.put("O_CODE","");
            map_time.put("O_NOTE","");
            log.debug("交易时间入参:"+map_time.toString());
            openPositionDao.isTradeTime(map_time);
            log.debug("交易时间出参:"+map_time.toString());
            if(null != map_time && Integer.parseInt(map_time.get("O_CODE").toString())==1){
                error_msg = "部分赎回失败";
                if(req_data.getDouble("capital_balance")>=req_data.getDouble("redeem_amount")){//账户余额>=赎回金额
                    Map map_HK =ifsService.capitalAllocation(req_data.getString("op_station"), "", req_data.getString("cust_no"),
                            req_data.getString("cust_account"),req_data.getString("combine_stock_no"),
                            req_data.getDouble("redeem_amount"),"0","1");//调用资金划拨接口
                    if(null != map_HK && (int)map_HK.get("errNo")==0) {//划款成功
                        List li = (List) map_HK.get("list");
                        jsonRet.put("data",li);
                        jsonRet.put("code",1);
                        jsonRet.put("note","部分赎回成功");

                        JSONObject json_gl=ifsService.operationGuidance(req_data.getString("type"),"",req_data.getString("combine_stock_no"),
                                req_data.getString("op_station"), "8",req_data.getDouble("redeem_amount"),"","2");
                        redis.lpush(Common.PRO_PIF_JYCZ_CZGL,json_gl.toJSONString());
                        return jsonRet;
                    }else{
                        JSONObject json_gl=ifsService.operationGuidance(req_data.getString("type"),"",req_data.getString("combine_stock_no"),
                                req_data.getString("op_station"), "9",req_data.getDouble("redeem_amount"),"","2");
                        redis.lpush(Common.PRO_PIF_JYCZ_CZGL,json_gl.toJSONString());
                        code=(int)map_HK.get("errNo");
                        error_msg= (String) map_HK.get("errMsg");
                    }
                }else {
                    if(req_data.getString("combine_data")==null||req_data.getString("combine_data")==""||req_data.getString("combine_data").trim().length()<=0||
                            req_data.getString("combine_data").equals("[]")){
                        jsonRet.put("code", -1);
                        jsonRet.put("note", "当前无法计算赎回方案，可用金额不足，暂时无法赎回!");
                        return jsonRet;
                    }
                    String combine_data= req_data.getString("combine_data");
                    List combine_list=new ArrayList();
                    try {
                        combine_list=JSON.parseArray(combine_data);
                    }catch(Exception e){
                        e.printStackTrace();
                        error_msg ="组合成份格式不正确";
                    }
                    StringBuffer buffer = new StringBuffer();
                    for(int i=0;i<combine_list.size();i++){
                        JSONObject js = new JSONObject();
                        js = (JSONObject) combine_list.get(i);
                        buffer.append(js.getString("fund_code")+"|"+js.getString("fund_company")+"|"+js.getString("amount")+";");
                    }
                    if(buffer.length()>0){
                        buffer.deleteCharAt(buffer.length()-1);
                        cpdm= String.valueOf(buffer);
                    }
                    Map map_SH = ifsService.fundRedemption(req_data.getString("op_station"), "",
                            req_data.getString("cust_no"), req_data.getString("cust_account"),req_data.getString("ofchannel_type"),
                            req_data.getString("entrust_way"), req_data.getString("combine_stock_no"),req_data.getDouble("capital_balance"),req_data.getString("combine_data"));//调用组合基金赎回接口
                    if(null != map_SH && (int)map_SH.get("errNo")==0){
                        List list_SH= (List) map_SH.get("list");
                        Map m_sh= (Map) list_SH.get(0);
                        jsonRet.put("data",list_SH);
                        jsonRet.put("code",1);
                        jsonRet.put("note","部分赎回成功");

                        JSONObject json_jy=ifsService.tradingOperation(req_data.getString("type"),req_data.getString("cust_type"),req_data.getString("cust_no"),
                                req_data.getString("cust_account"),req_data.getString("combine_stock_no"),2,cpdm,"",req_data.getDouble("capital_balance"),
                                req_data.getString("op_station"),m_sh.get("combine_entrust_no").toString(),m_sh.get("combine_entrust_no").toString(),
                                m_sh.get("entrust_status").toString());
                        redis.lpush(Common.PRO_PIF_ZHJY_JYCZ,json_jy.toJSONString());
                        return jsonRet;
                    }else{
                        JSONObject json_jy=ifsService.tradingOperation(req_data.getString("type"),req_data.getString("cust_type"),req_data.getString("cust_no"),
                                req_data.getString("cust_account"),req_data.getString("combine_stock_no"),2,cpdm,"",req_data.getDouble("capital_balance"),
                                req_data.getString("op_station"),"","","9");
                        redis.lpush(Common.PRO_PIF_ZHJY_JYCZ,json_jy.toJSONString());
                        code=(int)map_SH.get("errNo");
                        error_msg= (String) map_SH.get("errMsg");
                    }
                }
            }else{
                error_msg = map_time.get("O_NOTE").toString();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        jsonRet.put("code",-1);
        jsonRet.put("note","错误代码:"+code+";部分赎回失败:"+error_msg );
        return jsonRet;
    }

    //全部赎回
    public JSONObject redeemall(JSONObject req_data){
        JSONObject jsonRet = new JSONObject();
        ConfigUtil.loadConfig();
        RedisUtil redis=new RedisUtil();
        String error_msg = "全部赎回失败";
        int code=-1;
        try{
            if(req_data.getString("cust_type")==null||req_data.getString("cust_type")==""||req_data.getString("cust_type").trim().length()<=0){
                jsonRet.put("code", -1);
                jsonRet.put("note", "客户类型不能为空!");
                return jsonRet;
            }
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
            if(req_data.getString("combine_stock_no")==null||req_data.getString("combine_stock_no")==""||req_data.getString("combine_stock_no").trim().length()<=0){
                jsonRet.put("code", -1);
                jsonRet.put("note", "组合持仓编号不能为空!");
                return jsonRet;
            }
            if(req_data.getString("capital_balance")==null||req_data.getString("capital_balance")==""||req_data.getString("capital_balance").trim().length()<=0){
                jsonRet.put("code", -1);
                jsonRet.put("note", "可用资金余额不能为空!");
                return jsonRet;
            }
            if(req_data.getString("op_station")==null||req_data.getString("op_station")==""||req_data.getString("op_station").trim().length()<=0){
                jsonRet.put("code", -1);
                jsonRet.put("note", "站点地址不能为空!");
                return jsonRet;
            }

            if(req_data.getString("combine_data")==null||req_data.getString("combine_data")==""||req_data.getString("combine_data").equals("[]")){
                Map map_HK =ifsService.capitalAllocation(req_data.getString("op_station"), "", req_data.getString("cust_no"),
                        req_data.getString("cust_account"), req_data.getString("combine_stock_no"),
                        req_data.getDouble("capital_balance"), "0", "1");//调用资金划拨接口
                if(null != map_HK && (int)map_HK.get("errNo")==0){//划款成功
                    List li_HK= (List) map_HK.get("list");
                    Map m_sh= (Map) li_HK.get(0);
                    jsonRet.put("data",li_HK);
                    jsonRet.put("code",1);
                    jsonRet.put("note","全部赎回成功");

                    JSONObject json_gl=ifsService.operationGuidance(req_data.getString("type"),"",req_data.getString("combine_stock_no"),
                            req_data.getString("op_station"), "8",req_data.getDouble("capital_balance"),"","2");
                    redis.lpush(Common.PRO_PIF_JYCZ_CZGL,json_gl.toJSONString());
                    return jsonRet;
                }else{
                    code=(int)map_HK.get("errNo");
                    error_msg= (String)map_HK.get("errMsg");

                    JSONObject json_gl=ifsService.operationGuidance(req_data.getString("type"),"",req_data.getString("combine_stock_no"),
                            req_data.getString("op_station"), "9",req_data.getDouble("capital_balance"),"","2");
                    redis.lpush(Common.PRO_PIF_JYCZ_CZGL,json_gl.toJSONString());
                }
            }else{
                String combine_data= req_data.getString("combine_data");
                List combine_list=new ArrayList();
                try {
                    combine_list=JSON.parseArray(combine_data);
                }catch(Exception e){
                    e.printStackTrace();
                    error_msg ="组合成份格式不正确";
                }
                StringBuffer buffer = new StringBuffer();
                for(int i=0;i<combine_list.size();i++){
                    JSONObject js = new JSONObject();
                    js = (JSONObject) combine_list.get(i);
                    buffer.append(js.getString("fund_code")+"|"+js.getString("fund_company")+"|"+js.getString("amount")+";");
                }
                buffer.deleteCharAt(buffer.length()-1);
                String cpdm= String.valueOf(buffer);
                Map map_SH = ifsService.fundRedemption(req_data.getString("op_station"), "",
                        req_data.getString("cust_no"), req_data.getString("cust_account"),req_data.getString("ofchannel_type"),
                        req_data.getString("entrust_way"), req_data.getString("combine_stock_no"),req_data.getDouble("capital_balance"),req_data.getString("combine_data"));//调用组合基金赎回接口
                if(null != map_SH && (int)map_SH.get("errNo")==0){
                    List list_SH= (List) map_SH.get("list");
                    Map m_sh= (Map) list_SH.get(0);
                    jsonRet.put("data",list_SH);
                    jsonRet.put("code",1);
                    jsonRet.put("note","全部赎回成功");

                    JSONObject json_jy = new JSONObject();
                    json_jy.put("I_CZLX",req_data.getString("type"));//操作类型
                    json_jy.put("I_KHLX",req_data.getString("cust_type"));//客户类型
                    json_jy.put("I_KHH",req_data.getString("cust_no"));//客户号
                    json_jy.put("I_ZJZH",req_data.getString("cust_account"));//资金账号
                    json_jy.put("I_ZHBH",req_data.getString("combine_stock_no"));//组合编号
                    json_jy.put("I_DDLX",2);//1|申购;2|赎回;3|撤单
                    json_jy.put("I_CPDM",cpdm);//产品ID|金额/份额的分号连接串
                    json_jy.put("I_ZHSLDH",m_sh.get("combine_entrust_no"));//组合IFS受理单号
                    json_jy.put("I_ZHWTBH",m_sh.get("combine_entrust_no"));//组合委托编号
                    json_jy.put("I_ZHDDZT",m_sh.get("entrust_status"));//组合订单状态  -4|已提交申请;-3|未受理;-2|受理中;-1|受理废单
                    json_jy.put("I_CZLSH","");//操作流水号
                    json_jy.put("I_HBJE",req_data.getDouble("capital_balance"));//资金划拨金额
                    json_jy.put("I_OP_STATION",req_data.getString("op_station"));//站点地址
                    redis.lpush(Common.PRO_PIF_ZHJY_JYCZ,json_jy.toJSONString());
                    return jsonRet;
                }else{
                    JSONObject json_jy = new JSONObject();
                    json_jy.put("I_CZLX",req_data.getString("type"));//操作类型
                    json_jy.put("I_KHLX",req_data.getString("cust_type"));//客户类型
                    json_jy.put("I_KHH",req_data.getString("cust_no"));//客户号
                    json_jy.put("I_ZJZH",req_data.getString("cust_account"));//资金账号
                    json_jy.put("I_ZHBH",req_data.getString("combine_stock_no"));//组合编号
                    json_jy.put("I_DDLX",2);//1|申购;2|赎回;3|撤单
                    json_jy.put("I_CPDM",cpdm);//产品ID|金额/份额的分号连接串
                    json_jy.put("I_ZHSLDH","");//组合IFS受理单号
                    json_jy.put("I_ZHWTBH","");//组合委托编号
                    json_jy.put("I_ZHDDZT","9");//组合订单状态  -4|已提交申请;-3|未受理;-2|受理中;-1|受理废单
                    json_jy.put("I_CZLSH","");//操作流水号
                    json_jy.put("I_HBJE",req_data.getDouble("capital_balance"));//资金划拨金额
                    json_jy.put("I_OP_STATION",req_data.getString("op_station"));//站点地址
                    redis.lpush(Common.PRO_PIF_ZHJY_JYCZ,json_jy.toJSONString());
                    code=(int)map_SH.get("errNo");
                    error_msg= (String) map_SH.get("errMsg");
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        jsonRet.put("code",-1);
        jsonRet.put("note","错误代码:"+code+";全部赎回失败错误:"+error_msg );
        return jsonRet;
    }
    //撤销操作
    public JSONObject cancel(JSONObject req_data){
        JSONObject jsonRet = new JSONObject();
        ConfigUtil.loadConfig();
        RedisUtil redis=new RedisUtil();
        String error_msg = "撤单失败";
        int code=-1;
        try{
            if(req_data.getString("oper_type")==null||req_data.getString("oper_type")==""||req_data.getString("oper_type").trim().length()<=0){
                jsonRet.put("code", -1);
                jsonRet.put("note", "操作类型不能为空!");
                return jsonRet;
            }
            if(req_data.getString("oper_serial_no")==null||req_data.getString("oper_serial_no")==""||req_data.getString("oper_serial_no").trim().length()<=0){
                jsonRet.put("code", -1);
                jsonRet.put("note", "操作流水号不能为空!");
                return jsonRet;
            }
            if(req_data.getString("acpt_id")==null||req_data.getString("acpt_id")==""||req_data.getString("acpt_id").trim().length()<=0){
                jsonRet.put("code", -1);
                jsonRet.put("note", "受理单编号不能为空!");
                return jsonRet;
            }
            if(req_data.getString("cust_type")==null||req_data.getString("cust_type")==""||req_data.getString("cust_type").trim().length()<=0){
                jsonRet.put("code", -1);
                jsonRet.put("note", "客户类型不能为空!");
                return jsonRet;
            }
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
            if(req_data.getString("combine_stock_no")==null||req_data.getString("combine_stock_no")==""||req_data.getString("combine_stock_no").trim().length()<=0){
                jsonRet.put("code", -1);
                jsonRet.put("note", "组合持仓编号不能为空!");
                return jsonRet;
            }
            if(req_data.getString("op_station")==null||req_data.getString("op_station")==""||req_data.getString("op_station").trim().length()<=0){
                jsonRet.put("code", -1);
                jsonRet.put("note", "站点地址不能为空!");
                return jsonRet;
            }
            String oper_type=req_data.getString("oper_type");

            Map map_CD =ifsService.cancel(req_data.getString("op_station"),"",req_data.getString("cust_no"),req_data.getString("cust_account"),
                    req_data.getString("acpt_id"),req_data.getString("ofchannel_type"),req_data.getString("entrust_way"));
            if(null != map_CD && (int)map_CD.get("errNo") == 0){
                List list= (List) map_CD.get("list");
                Map m_zt= (Map) list.get(0);
                Map map_PZ = new HashMap();
                map_PZ.put("O_CODE", "");
                map_PZ.put("O_NOTE","");
                map_PZ.put("I_CZLX",oper_type);
                map_PZ.put("I_CZLSH",req_data.getString("oper_serial_no"));
                map_PZ.put("I_ZHBH",req_data.getString("combine_stock_no"));
                map_PZ.put("I_CZZT",m_zt.get("entrust_status"));
                map_PZ.put("I_OP_STATION",req_data.getString("op_station"));
                log.debug("处理本地订单入参:"+map_PZ.toString());
                openPositionDao.orderProcessing(map_PZ);//处理本地订单
                log.debug("处理本地订单出参:"+map_PZ.toString());

                if(null != map_PZ && Integer.parseInt(map_PZ.get("O_CODE").toString())==1 ){
                    if(oper_type.equals("1")||oper_type.equals("2")||oper_type.equals("7")||oper_type.equals("8")){
                        Double occure_balance=0.00;
                        if(oper_type.equals("1")){
                            Map map_QM=ifsService.queryMoney(req_data.getString("op_station"), req_data.getString("combine_stock_no"));
                            if(null != map_QM && (int)map_QM.get("errNo") == 0){//查询账户余额
                                List li_QM= (List) map_QM.get("list");
                                Map m_QM= (Map) li_QM.get(0);
                                String  resultList= (String) m_QM.get("resultList");
                                List li_re= JSON.parseArray(resultList);
                                if(li_re == null || li_re.size() == 0) {
                                    error_msg="未查询到用户资金信息";
                                    jsonRet.put("code",-1);
                                    jsonRet.put("note","错误代码:"+code+";撤单失败:"+error_msg );
                                    return jsonRet;
                                }
                                JSONObject js_re =(JSONObject) li_re.get(0);
                                occure_balance=js_re.getDouble("enable_balance");
                            }else{
                                code=(int)map_QM.get("errNo");
                                error_msg= (String) map_QM.get("errMsg");
                            }
                        }else{
                            Map map_QA=ifsService.queryAmount(req_data.getString("op_station"),req_data.getString("acpt_id"));
                            if(null != map_QA && (int)map_QA.get("errNo") == 0){//查询组合委托成功
                                List li_QA= (List) map_QA.get("list");
                                Map m_QA= (Map) li_QA.get(0);
                                String  resultList= (String) m_QA.get("resultList");
                                List li_re= JSON.parseArray(resultList);
                                if(li_re == null || li_re.size() == 0) {
                                    error_msg="未查询到用户成份委托信息";
                                    jsonRet.put("code",-1);
                                    jsonRet.put("note","错误代码:"+code+";撤单失败:"+error_msg );
                                    return jsonRet;
                                }
                                for(int i=0;i<li_re.size();i++){
                                    JSONObject js_re = new JSONObject();
                                    js_re= (JSONObject) li_re.get(i);
                                    if(js_re.getString("entrust_status").equals("6")){
                                        occure_balance+=js_re.getDouble("occur_balance");
                                    }
                                }
                             }else{
                                code=(int)map_QA.get("errNo");
                                error_msg= (String) map_QA.get("errMsg");
                             }
                        }
                        Map map_HK =ifsService.capitalAllocation(req_data.getString("op_station"),"",req_data.getString("cust_no"),
                                    req_data.getString("cust_account"),req_data.getString("combine_stock_no"),occure_balance,"0","1");
                        JSONObject json_jy=ifsService.tradingOperation(oper_type,req_data.getString("cust_type"),req_data.getString("cust_no"),
                                    req_data.getString("cust_account"),req_data.getString("combine_stock_no"), 3,"",req_data.getString("oper_serial_no"),
                                    occure_balance,req_data.getString("op_station"), m_zt.get("combine_entrust_no").toString(),
                                    m_zt.get("combine_entrust_no").toString(),m_zt.get("entrust_status").toString());
                        redis.lpush(Common.PRO_PIF_ZHJY_JYCZ,json_jy.toJSONString());
                        if(null != map_HK && (int)map_HK.get("errNo") == 0){//划款成功
                            if(oper_type.equals("1")&&m_zt.get("entrust_status").toString().equals("8")){
                                    //调用IFS平仓接口
                                Map map_PC = ifsService.fundLiquidation(req_data.getString("op_station"), "", req_data.getString("cust_no"), req_data.getString("cust_account"),
                                            req_data.getString("combine_stock_no"));
                                if (null != map_PC && (int) map_PC.get("errNo") == 0) {
                                    List list_PC = (List) map_PC.get("list");
                                    Map map_BD = new HashMap();
                                    map_BD.put("O_CODE", "");
                                    map_BD.put("O_NOTE", "");
                                    map_BD.put("I_KHLX", req_data.getString("cust_type"));
                                    map_BD.put("I_CZLX",2);
                                    map_BD.put("I_KHH", req_data.getString("cust_no"));
                                    map_BD.put("I_ZJZH", req_data.getString("cust_account"));
                                    map_BD.put("I_ZHBH", req_data.getString("combine_stock_no"));
                                    map_BD.put("I_YWLX","");
                                    map_BD.put("I_KTQD","app");
                                    log.debug("存入账户信息入参:"+map_BD.toString());
                                    openPositionDao.openNew(map_BD);//调用本地过程存入账户信息
                                    log.debug("存入账户信息出参:"+map_BD.toString());
                                    if (null != map_BD && Integer.parseInt(map_BD.get("O_CODE").toString()) == 1) {
                                        List li= (List) map_BD.get("list");
                                        jsonRet.put("data",li);
                                        jsonRet.put("code",1);
                                        jsonRet.put("note","撤单成功");
                                        return jsonRet;
                                    }else{
                                        error_msg= (String) map_BD.get("errMsg");
                                    }
                                }else{
                                    code=(int)map_PC.get("errNo");
                                    error_msg= (String) map_PC.get("errMsg");
                                }
                            }else{
                                List li= (List) map_HK.get("list");
                                jsonRet.put("data",li);
                                jsonRet.put("code",1);
                                jsonRet.put("note","撤单成功");
                                return jsonRet;
                            }
                        }else{
                            code=(int)map_HK.get("errNo");
                            error_msg= (String) map_HK.get("errMsg");
                        }
                    }else{
                        JSONObject json_jy=ifsService.tradingOperation(oper_type,req_data.getString("cust_type"),req_data.getString("cust_no"),
                                req_data.getString("cust_account"),req_data.getString("combine_stock_no"), 3,"",req_data.getString("oper_serial_no"),
                                0.00,req_data.getString("op_station"),m_zt.get("combine_entrust_no").toString(),m_zt.get("combine_entrust_no").toString(),
                                m_zt.get("entrust_status").toString());
                        redis.lpush(Common.PRO_PIF_ZHJY_JYCZ, json_jy.toJSONString());
                        jsonRet.put("code",1);
                        jsonRet.put("note","撤单成功");
                        return jsonRet;
                    }
                }else{
                    error_msg =map_PZ.get("O_NOTE").toString();
                }

            }else{
                code=(int)map_CD.get("errNo");
                error_msg= (String) map_CD.get("errMsg");
                JSONObject json_jy=ifsService.tradingOperation(oper_type,req_data.getString("cust_type"),req_data.getString("cust_no"),
                        req_data.getString("cust_account"),req_data.getString("combine_stock_no"), 3,"",req_data.getString("oper_serial_no"),
                        0.00,req_data.getString("op_station"),"","","9");
                redis.lpush(Common.PRO_PIF_ZHJY_JYCZ, json_jy.toJSONString());
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        jsonRet.put("code",-1);
        jsonRet.put("note","错误代码"+code+";调用撤单失败接口错误:"+error_msg );
        return jsonRet;
    }

    //组合配置方案
    public JSONObject modify(JSONObject req_data){
        JSONObject jsonRet = new JSONObject();
        ConfigUtil.loadConfig();
        RedisUtil redis=new RedisUtil();
        String error_msg="";
        int code=-1;
        try{
            if(req_data.getString("combine_stock_no")==null||req_data.getString("combine_stock_no")==""||req_data.getString("combine_stock_no").trim().length()<=0){
                jsonRet.put("code", -1);
                jsonRet.put("note", "组合持仓编号不能为空!");
                return jsonRet;
            }
            if(req_data.getString("product_id_pre")==null||req_data.getString("product_id_pre")==""||req_data.getString("product_id_pre").trim().length()<=0){
                jsonRet.put("code", -1);
                jsonRet.put("note", "变更前的产品ID不能为空!");
                return jsonRet;
            }
            if(req_data.getString("product_id")==null||req_data.getString("product_id")==""||req_data.getString("product_id").trim().length()<=0){
                jsonRet.put("code", -1);
                jsonRet.put("note", "变更后的产品ID不能为空!");
                return jsonRet;
            }
            if(req_data.getString("op_station")==null||req_data.getString("op_station")==""||req_data.getString("op_station").trim().length()<=0){
                jsonRet.put("code", -1);
                jsonRet.put("note", "站点地址不能为空!");
                return jsonRet;
            }
            Map map_time=new HashMap();
            map_time.put("O_CODE","");
            map_time.put("O_NOTE","");
            log.debug("交易时间入参:"+map_time.toString());
            openPositionDao.isTradeTime(map_time);//调用本地过程判断是否是交易时间
            log.debug("交易时间出参:"+map_time.toString());
            if(null != map_time && Integer.parseInt(map_time.get("O_CODE").toString())==1){
                error_msg = "Bus10503接口出错";
                String product_id_pre=  req_data.getString("product_id_pre");//变更前的产品ID分号连接串
                String product_id= req_data.getString("product_id");//变更后的产品ID分号连接串
                String [] id_pre=req_data.getString("product_id_pre").split("\\;");
                String [] id=req_data.getString("product_id").split("\\;");
                Arrays.sort(id_pre);
                Arrays.sort(id);
                if(!Arrays.equals(id_pre,id)){
                    Map map_YE = ifsService.queryMoney(req_data.getString("op_station"),req_data.getString("combine_stock_no"));//调用查询组合余额接口
                    if(null != map_YE && (int)map_YE.get("errNo")==0){
                        List list_ye= (List) map_YE.get("list");
                        Map map_ye = (Map) list_ye.get(0);
                        String  resultList= (String) map_ye.get("resultList");
                        List re_list= JSON.parseArray(resultList);
                        if(re_list == null || re_list.size() == 0) {
                            error_msg="未查询到用户资金信息";
                            jsonRet.put("code",-1);
                            jsonRet.put("note","错误代码:"+code+";保存组合配置方案失败:"+error_msg );
                            return jsonRet;
                        }
                        JSONObject jso= (JSONObject) re_list.get(0);
                            Map map_CC = new HashMap();
                            map_CC.put("O_CODE", "");
                            map_CC.put("O_NOTE", "");
                            map_CC.put("O_RESULT", new ArrayList<Map<String, Object>>());
                            map_CC.put("I_ZHBH", req_data.getString("combine_stock_no"));
                            map_CC.put("I_CPID", req_data.getString("product_id"));
                            log.debug("当前持仓份额入参:"+map_CC.toString());
                            openPositionDao.queryPositon(map_CC);//查询组合当前持仓份额
                            log.debug("当前持仓份额出参:"+map_CC.toString());
                            if(null != map_CC && Integer.parseInt(map_CC.get("O_CODE").toString()) == 1){
                                List li_cc= (List) map_CC.get("O_RESULT");
                                List listSum=new ArrayList();
                                if(li_cc.size()>0){
                                    Map m_cc= (Map) li_cc.get(0);
                                    StringBuffer buffer = new StringBuffer();
                                    for (int i=0;i<li_cc.size();i++){
                                        Map m= (Map) li_cc.get(i);
                                        JSONObject json = new JSONObject();
                                        json.put("fund_code",m.get("cpdm"));
                                        json.put("fund_company",m.get("jjgs"));
                                        json.put("amount",m.get("kyfe"));
                                        buffer.append(m.get("cpdm")+"|"+m.get("jjgs")+"|"+m.get("kyfe")+";");
                                        listSum.add(i,json);
                                    }
                                    buffer.deleteCharAt(buffer.length()-1);
                                    String cpdm= String.valueOf(buffer);
                                    Map map_SH = ifsService.fundRedemption(req_data.getString("op_station"),"",
                                            m_cc.get("khh").toString(),m_cc.get("zjzh").toString(), req_data.getString("ofchannel_type"),
                                            req_data.getString("entrust_way"),req_data.getString("combine_stock_no"),jso.getDouble("enable_balance"),
                                            listSum.toString());//调用组合基金赎回接口
                                    if (null != map_SH && (int) map_SH.get("errNo")==0){
                                        Map map_PZ = new HashMap();
                                        map_PZ.put("O_CODE", "");
                                        map_PZ.put("O_NOTE", "");
                                        map_PZ.put("I_CZLX",2);
                                        map_PZ.put("I_KHLX","");
                                        map_PZ.put("I_KHH","");
                                        map_PZ.put("I_ZJZH","");
                                        map_PZ.put("I_ZHBH", req_data.get("combine_stock_no").toString());
                                        map_PZ.put("I_YWLXBM","");
                                        map_PZ.put("I_PBBM","");
                                        map_PZ.put("I_CPID",req_data.getString("product_id"));
                                        map_PZ.put("I_OP_STATION",req_data.getString("op_station"));
                                        log.debug("修改当前配置方案入参:"+map_PZ.toString());
                                        openPositionDao.modifyConfig(map_PZ);//修改当前配置方案
                                        log.debug("修改当前配置方案出参:"+map_PZ.toString());
                                        if(null != map_PZ && Integer.parseInt(map_PZ.get("O_CODE").toString()) == 1){
                                            List li_SH = (List) map_SH.get("list");
                                            Map m_sh= (Map) li_SH.get(0);
                                            jsonRet.put("data", li_SH);
                                            jsonRet.put("code", 1);
                                            jsonRet.put("note", "保存组合配置方案成功");

                                            JSONObject json_jy = ifsService.tradingOperation("11",m_cc.get("GRJG").toString(),m_cc.get("khh").toString(),
                                                    m_cc.get("zjzh").toString(),m_cc.get("zhbh").toString(),2,cpdm,"",jso.getDouble("enable_balance"),
                                                    req_data.getString("op_station"),m_sh.get("combine_entrust_no").toString(),m_sh.get("combine_entrust_no").toString(),
                                                    m_sh.get("entrust_status").toString());
                                            redis.lpush(Common.PRO_PIF_ZHJY_JYCZ,json_jy.toJSONString());
                                            return jsonRet;
                                        }else{
                                            error_msg=map_PZ.get("O_NOTE").toString();
                                            jsonRet.put("code",-1);
                                            jsonRet.put("note", "错误代码:"+code+";保存组合配置方案失败:" + error_msg);
                                            return jsonRet;
                                        }

                                    }else {
                                        JSONObject json_jy = ifsService.tradingOperation("11",m_cc.get("GRJG").toString(),m_cc.get("khh").toString(),
                                                m_cc.get("zjzh").toString(),m_cc.get("zhbh").toString(),2,cpdm,"",jso.getDouble("enable_balance"),
                                                req_data.getString("op_station"),"","","9");
                                        redis.lpush(Common.PRO_PIF_ZHJY_JYCZ,json_jy.toJSONString());

                                        code=(int)map_SH.get("errNo");
                                        error_msg= (String) map_SH.get("errMsg");
                                    }
                                }else{
                                    Map map_PZ = new HashMap();
                                    map_PZ.put("O_CODE", "");
                                    map_PZ.put("O_NOTE", "");
                                    map_PZ.put("I_CZLX",2);
                                    map_PZ.put("I_KHLX","");
                                    map_PZ.put("I_KHH","");
                                    map_PZ.put("I_ZJZH","");
                                    map_PZ.put("I_ZHBH", req_data.get("combine_stock_no").toString());
                                    map_PZ.put("I_YWLXBM","");
                                    map_PZ.put("I_PBBM","");
                                    map_PZ.put("I_CPID",req_data.getString("product_id"));
                                    log.debug("修改当前配置方案入参:"+map_PZ.toString());
                                    openPositionDao.modifyConfig(map_PZ);//修改当前配置方案
                                    log.debug("修改当前配置方案出参:"+map_PZ.toString());
                                    if(null != map_PZ && Integer.parseInt(map_PZ.get("O_CODE").toString()) == 1){
                                        String ZHBH = req_data.get("combine_stock_no").toString();
                                        Map map_Lsh = new HashMap();
                                        map_Lsh.put("O_CODE", "");
                                        map_Lsh.put("O_NOTE", "");
                                        map_Lsh.put("I_ZHBH",ZHBH);
                                        log.debug("配置方案失败入参:"+map_Lsh.toString());
                                        openPositionDao.purchaseErr(map_Lsh);
                                        log.debug("配置方案失败出参:"+map_Lsh.toString());
                                        //获取游标
                                        List l1 = (List) map_Lsh.get("O_RESULT");
                                        //获取流水号
                                        Map m1 = (Map)l1.get(0);
                                        String LSH = m1.get("LSH").toString();
                                        List li_PZ  = new ArrayList();
                                        Map<String,Object> map2 = new HashMap<>();
                                        map2.put("entrust_status","");
                                        map2.put("combine_stock_no","");
                                        map2.put("combine_entrust_no",LSH);
                                        li_PZ.add(map2);
                                        jsonRet.put("data",li_PZ);
                                        jsonRet.put("code", 1);
                                        jsonRet.put("note", "保存组合配置方案成功");
                                        JSONObject json_jy = ifsService.tradingOperation("11",m1.get("KHLX").toString(),m1.get("khh").toString(),
                                                m1.get("zjzh").toString(),m1.get("zhbh").toString(),0,"","",0.00,
                                                req_data.getString("op_station"),"","","8");
                                        redis.lpush(Common.PRO_PIF_ZHJY_JYCZ,json_jy.toJSONString());
                                        System.out.println(jsonRet);
                                        return jsonRet;
                                    }else{
                                        String ZHBH = req_data.get("combine_stock_no").toString();
                                        Map map_Lsh = new HashMap();
                                        map_Lsh.put("O_CODE", "");
                                        map_Lsh.put("O_NOTE", "");
                                        map_Lsh.put("I_ZHBH",ZHBH);
                                        log.debug("配置方案失败入参:"+map_Lsh.toString());
                                        openPositionDao.purchaseErr(map_Lsh);
                                        log.debug("配置方案失败出参:"+map_Lsh.toString());
                                        //获取游标
                                        List l1 = (List) map_Lsh.get("O_RESULT");
                                        Map m1 = (Map)l1.get(0);
                                        JSONObject json_jy = ifsService.tradingOperation("11",m1.get("KHLX").toString(),m1.get("khh").toString(),
                                                m1.get("zjzh").toString(),m1.get("zhbh").toString(),0,"","",0.00,
                                                req_data.getString("op_station"),"","","9");
                                        redis.lpush(Common.PRO_PIF_ZHJY_JYCZ,json_jy.toJSONString());
                                        error_msg=map_PZ.get("O_NOTE").toString();
                                    }
                                }
                            }else {
                                error_msg = "存储过程查询持仓份额出错";
                            }
                    }else {
                        code=(int)map_YE.get("errNo");
                        error_msg= (String) map_YE.get("errMsg");
                    }
                }else {
                    error_msg = "预设产品未发生变化";
                }
            }else{
                error_msg = map_time.get("O_NOTE").toString();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        jsonRet.put("code",-1);
        jsonRet.put("note", "错误代码:"+code+";保存组合配置方案失败:" + error_msg);
        return jsonRet;
    }


    public void setOpenPositionDao(OpenPositionDao openPositionDao) {
        this.openPositionDao = openPositionDao;
    }

}
