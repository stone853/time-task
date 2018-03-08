package com.apex.bss.cjsc.asclient.action.bus;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.apex.bss.cjsc.asclient.service.IfsService;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Lenovo on 2017/1/13.
 * 查询组合委托明细
 */
public class Bus10506 extends Bus{
    protected static final Logger log = Logger.getLogger(Bus10506.class);
    @Override
    public JSONObject invoke(JSONObject req_data){
        IfsService ifsService=new IfsService();
        JSONObject jsonRet = new JSONObject();
        int code = -1;
        String error_msg = "查询组合委托明细失败";
        try{
            if(req_data.getString("combine_entrust_no")==null||req_data.getString("combine_entrust_no")==""||req_data.getString("combine_entrust_no").trim().length()<=0){
                jsonRet.put("code", -1);
                jsonRet.put("note", "组合委托编号不能为空!");
                return jsonRet;
            }
            if(req_data.getString("op_station")==null||req_data.getString("op_station")==""||req_data.getString("op_station").trim().length()<=0){
                jsonRet.put("code", -1);
                jsonRet.put("note", "站点地址不能为空!");
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
            if(req_data.getString("occur_date")==null||req_data.getString("occur_date")==""||req_data.getString("occur_date").trim().length()<=0){
                jsonRet.put("code", -1);
                jsonRet.put("note", "发生时间不能为空!");
                return jsonRet;
            }
            String op_station=req_data.getString("op_station")+"_DD00";
            String combine_entrust_no=req_data.getString("combine_entrust_no");

            String []combine_entrust=combine_entrust_no.split("\\,");
            List listSum = new ArrayList();
            for(int i=0;i<combine_entrust.length;i++){
                String []combine=combine_entrust[i].split("\\;");
                if(null != combine && combine.length >=3 && !combine[0].equals("") && null!=combine[0]){
                    Map map_WT= new HashMap();
                    if(Integer.parseInt(combine[2])==0){
                        map_WT=ifsService.queryAmount(op_station,combine[0]);
                    }else{
                        map_WT=ifsService.queryHis_amount(op_station,req_data.getString("cust_no"),req_data.getString("cust_account"),combine[0],req_data.getString("occur_date"));
                    }
                    if(null != map_WT && (int)map_WT.get("errNo")==0){
                        List li= (List) map_WT.get("list");
                        Map m_QA= (Map) li.get(0);
                        String  resultList= (String) m_QA.get("resultList");
                        List li_re= JSON.parseArray(resultList);
                        if(li_re!=null&&li_re.size()>0){
                            for(int j=0;j<li_re.size();j++){
                                JSONObject jo = new JSONObject();
                                jo = (JSONObject) li_re.get(j);
                                JSONObject js = new JSONObject();
                                js.put("product_name",jo.getString("fund_name"));
                                js.put("occur_balance",jo.getString("occur_balance"));
                                js.put("occur_amount",jo.getString("occur_amount"));
                                js.put("oper_type",combine[1]);
                                js.put("entrust_status",jo.getString("entrust_status"));
                                listSum.add(js);
                            }
                        }
                    }else{
                        code=(int)map_WT.get("errNo");
                        error_msg= (String) map_WT.get("errMsg");
                        jsonRet.put("code",-1);
                        jsonRet.put("note","错误代码:"+code+"查询组合委托明细失败:"+error_msg );
                        return jsonRet;
                    }
                }
            }
            jsonRet.put("data",listSum);
            jsonRet.put("code",1);
            jsonRet.put("note","查询组合委托明细成功");
            return jsonRet;
        }catch (Exception e){
            e.printStackTrace();
        }
        jsonRet.put("code",-1);
        jsonRet.put("note","错误代码:"+code+"查询组合委托明细失败:"+error_msg );
        return jsonRet;
    }
}
