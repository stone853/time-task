package com.apex.bss.cjsc.asclient.action.bus;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.apex.bss.cjsc.asclient.service.IfsService;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Lenovo on 2017/2/27.
 */
public class Bus10508 extends Bus{
    protected static final Logger log = Logger.getLogger(Bus10508.class);
    @Override
    public JSONObject invoke(JSONObject req_data){
        IfsService ifsService=new IfsService();
        JSONObject jsonRet = new JSONObject();
        String error_msg = "查询客户组合资产失败";
        int code=-1;
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
            String op_station=req_data.getString("op_station")+"_DD00";

            Map map_YE =ifsService.queryMoney(op_station,req_data.getString("combine_stock_no"));
            if(null != map_YE && (int)map_YE.get("errNo")==0){
                List list= (List) map_YE.get("list");
                Map map = (Map) list.get(0);
                String  resultList= (String) map.get("resultList");
                List re_list= JSON.parseArray(resultList);
                List li=new ArrayList();
                if(re_list.size()>0&&re_list!=null){
                    JSONObject jso= (JSONObject) re_list.get(0);
                    JSONObject json = new JSONObject();
                    json.put("enable_balance", jso.getString("enable_balance"));//可用资金
                    json.put("frozen_balance",jso.getString("frozen_balance"));//冻结资金
                    json.put("total_asset",jso.getString("total_asset"));//总资产
                    json.put("total_market_value",jso.getString("total_market_value"));//总市值
                    Double cash=jso.getDouble("enable_balance")+jso.getDouble("frozen_balance");
                    DecimalFormat df = new DecimalFormat("0.00");
                    BigDecimal bigDecimal=new BigDecimal(cash);
                    json.put("cash",df.format(bigDecimal));//现金
                    li.add(json);
                }
                jsonRet.put("data",li);
                jsonRet.put("code",1);
                jsonRet.put("note","查询客户组合资产成功");
                return jsonRet;
            }else{
                code= (int) map_YE.get("errNo");
                error_msg = (String) map_YE.get("errMsg");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        jsonRet.put("code",-1);
        jsonRet.put("note","错误代码:"+code+";查询客户组合资产失败:"+error_msg );
        return jsonRet;
    }
}
