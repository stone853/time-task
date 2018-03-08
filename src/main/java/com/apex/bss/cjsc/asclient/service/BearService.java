package com.apex.bss.cjsc.asclient.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.apex.bss.cjsc.dao.OpenPositionDao;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Saphurot on 2017/5/3.
 */
public class BearService {
    protected static final Logger log = Logger.getLogger(BearService.class);
    BearIfsService bearIfsService = new BearIfsService();
    @Autowired
    private OpenPositionDao openPositionDao;

    //牛熊轮动首次申购
    public JSONObject purchaseBear(JSONObject req_data) {
        JSONObject jsonRet = new JSONObject();
        String error_msg = "首次申购失败";
        int code = -1;
        try {
            Map map_time = new HashMap();
            map_time.put("O_CODE", "");
            map_time.put("O_NOTE", "");
            map_time.put("I_CZLX", 1);
            openPositionDao.isPurchaseTime(map_time);//调用本地过程判断是否申购时间
            if (null != map_time && Integer.parseInt(map_time.get("O_CODE").toString()) == 1) {
                //调用IFS牛熊基金认申购
                Map map_sg = bearIfsService.bearFundPurchase(req_data.getString("op_branch_no"),req_data.getString("op_entrust_way"),req_data.getString("op_station"),req_data.getString("branch_no"),
                        req_data.getString("cust_no"),req_data.getString("cust_account"),req_data.getString("password"),req_data.getString("password_type"),req_data.getString("user_token"),
                        req_data.getString("secum_account"),req_data.getString("fund_code"),req_data.getString("fund_company"),req_data.getDouble("entrust_balance"),req_data.getString("developer"),
                        req_data.getString("elig_confirm_type"),req_data.getString("risk_sub_id"));
                if (null != map_sg && (int) map_sg.get("errNo") == 0) {
                    Map map_BD = new HashMap();
                    map_BD.put("O_CODE", "");
                    map_BD.put("O_NOTE", "");
                    map_BD.put("I_KHLX", req_data.getString("cust_type"));
                    map_BD.put("I_KHH", req_data.getString("cust_no"));
                    map_BD.put("I_ZJZH", req_data.getString("cust_account"));
                    map_BD.put("I_CLID", req_data.getString("stgy_id"));
                    openPositionDao.bearPurchase(map_BD);//回写账户信息到本地数据库
                    List list_sg = (List) map_sg.get("list");
                    jsonRet.put("data", list_sg);
                    jsonRet.put("code", 1);
                    jsonRet.put("note", "首次申购成功");
                    return jsonRet;
                } else {
                    code = (int) map_sg.get("errNo");
                    error_msg = (String) map_sg.get("errMsg");
                }
            } else {
                code = (int) map_time.get("O_CODE");
                error_msg = map_time.get("O_NOTE").toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        jsonRet.put("code", -1);
        jsonRet.put("note", "错误代码:" + code + ";首次申购失败:" + error_msg);
        return jsonRet;
    }

    //牛熊轮动追加申购
    public JSONObject appendBear(JSONObject req_data) {
        JSONObject jsonRet = new JSONObject();
        String error_msg = "";
        int code = -1;
        List listSum = new ArrayList();
        try {
            Map map_time = new HashMap();
            map_time.put("O_CODE", "");
            map_time.put("O_NOTE", "");
            map_time.put("I_CZLX", 1);
            openPositionDao.isPurchaseTime(map_time);//调用本地过程判断是否申购时间
            if (null != map_time && Integer.parseInt(map_time.get("O_CODE").toString()) == 1) {
                //在途转换判断
                Map map_zh = bearIfsService.queryEntrustedFunds(req_data.getString("op_branch_no"),req_data.getString("op_entrust_way"),req_data.getString("op_station"),req_data.getString("branch_no"),
                        req_data.getString("cust_no"),req_data.getString("cust_account"),req_data.getString("password"),req_data.getString("password_type"),req_data.getString("user_token"),
                        "","","",req_data.getString("secum_account"),req_data.getString("fund_code"),req_data.getString("fund_company"),"","",
                        "","","","","","","","","1000","");
                if (null != map_zh && (int) map_zh.get("errNo") == 0) {
                    List list_zh = (List) map_zh.get("list");
                    Map m = (Map) list_zh.get(0);
                    String resultList = (String) m.get("resultList");
                    List re_list = JSON.parseArray(resultList);
                    String flag = null;
                    int flag1 = 0;
                    //查询数据库
                    Map map_ch = new HashMap();
                    map_ch.put("O_CODE", "");
                    map_ch.put("O_NOTE", "");
                    map_ch.put("O_RESULT", "");
                    map_ch.put("I_CLID", req_data.getString("stgy_id"));
                    openPositionDao.filterProduct(map_ch);
                    List list_ch = (List) map_ch.get("O_RESULT");
                    for (int i = 0; i < list_ch.size(); i++) {
                        Map m_ch = (Map) list_ch.get(i);
                        for (int j = 0; j < re_list.size(); j++) {
                            JSONObject jso = (JSONObject) re_list.get(j);
                            flag = jso.getString("business_flag");
                            if (flag == "22" && m_ch.get("PROD_CODE").equals(jso.get("fund_code")) && m_ch.get("PROD_NAME").equals(jso.get("fund_name"))) {
                                flag1 = 1;
                                break;
                            }
                        }
                    }
                    //没有转换
                    if (flag1!=1) {
                        Map map_sg = bearIfsService.bearFundPurchase(req_data.getString("op_branch_no"),req_data.getString("op_entrust_way"),req_data.getString("op_station"),req_data.getString("branch_no"),
                                req_data.getString("cust_no"),req_data.getString("cust_account"),req_data.getString("password"),req_data.getString("password_type"),req_data.getString("user_token"),
                                req_data.getString("secum_account"),req_data.getString("fund_code"),req_data.getString("fund_company"),req_data.getDouble("entrust_balance"),req_data.getString("developer"),
                                req_data.getString("elig_confirm_type"),req_data.getString("risk_sub_id"));
                        if (null != map_sg && (int) map_sg.get("errNo") == 0) {
                            List list_sg = (List) map_sg.get("list");
                            Map m_sg = (Map) list_zh.get(0);
                            String resultList_sg = (String) m_sg.get("resultList");
                            List list = JSON.parseArray(resultList_sg);
                            jsonRet.put("data", list_sg);
                            jsonRet.put("code", 1);
                            jsonRet.put("note", "追加申购成功");
                            return jsonRet;
                        } else {
                            code = (int) map_sg.get("errNo");
                            error_msg = (String) map_sg.get("errMsg");
                        }
                    }else{
                        code = -1;
                        error_msg = "正在进行在途转换";
                    }
                } else {
                    code = (int) map_zh.get("errNo");
                    error_msg = (String) map_zh.get("errMsg");
                }

            } else {
                code = (int) map_time.get("O_CODE");
                error_msg = map_time.get("O_NOTE").toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        jsonRet.put("code", -1);
        jsonRet.put("note", "错误代码:" + code + ";追加申购失败:" + error_msg);
        return jsonRet;
    }

    //牛熊轮动赎回
    public JSONObject redeemBear(JSONObject req_data) {
        JSONObject jsonRet = new JSONObject();
        String error_msg = "";
        int code = -1;
        try {
            Map map_time = new HashMap();
            map_time.put("O_CODE", "");
            map_time.put("O_NOTE", "");
            map_time.put("I_CZLX", 2);
            openPositionDao.isPurchaseTime(map_time);//调用本地过程判断是否赎回时间
            if (null != map_time && Integer.parseInt(map_time.get("O_CODE").toString()) == 1) {
                //在途转换判断
                Map map_zh = bearIfsService.queryEntrustedFunds(req_data.getString("op_branch_no"),req_data.getString("op_entrust_way"),req_data.getString("op_station"),req_data.getString("branch_no"),
                        req_data.getString("cust_no"),req_data.getString("cust_account"),req_data.getString("password"),req_data.getString("password_type"),req_data.getString("user_token"),
                        "","","",req_data.getString("secum_account"),req_data.getString("fund_code"),req_data.getString("fund_company"),"","",
                        "","","","","","","","","1000","");
                if (null != map_zh && (int) map_zh.get("errNo") == 0) {
                    List list_zh = (List) map_zh.get("list");
                    Map m = (Map) list_zh.get(0);
                    String resultList = (String) m.get("resultList");
                    List re_list = JSON.parseArray(resultList);
                    String flag = null;
                    int flag1 = 0;
                    //查询数据库
                    Map map_ch = new HashMap();
                    map_ch.put("O_CODE", "");
                    map_ch.put("O_NOTE", "");
                    map_ch.put("O_RESULT", "");
                    map_ch.put("I_CLID", req_data.getString("stgy_id"));
                    openPositionDao.filterProduct(map_ch);
                    List list_ch = (List) map_ch.get("O_RESULT");
                    for (int i = 0; i < list_ch.size(); i++) {
                        Map m_ch = (Map) list_ch.get(i);
                        for (int j = 0; j < re_list.size(); j++) {
                            JSONObject jso = (JSONObject) re_list.get(j);
                            flag = jso.getString("business_flag");
                            if (flag == "22" && m_ch.get("PROD_CODE").equals(jso.get("fund_code")) && m_ch.get("PROD_NAME").equals(jso.get("fund_name"))) {
                                flag1=1;
                                break;
                            }
                        }
                    }
                    //没有转换
                    if (flag1!=1){
                        //调用牛熊轮动基金赎回接口
                        Map map_sh = bearIfsService.bearFundRedemption(req_data.getString("op_branch_no"),req_data.getString("op_entrust_way"),req_data.getString("op_station"),req_data.getString("branch_no"),
                                req_data.getString("cust_no"),req_data.getString("cust_account"),req_data.getString("password"),req_data.getString("password_type"),req_data.getString("user_token"),
                                req_data.getString("secum_account"),req_data.getString("fund_code"),req_data.getString("fund_company"),req_data.getDouble("amount"),req_data.getString("allotno"),
                                req_data.getString("exceedflag"),req_data.getString("developer"));
                        if (null != map_sh && (int) map_sh.get("errNo") == 0) {
                            List list_sh = (List) map_sh.get("list");

                            jsonRet.put("data", list_sh);
                            jsonRet.put("code", 1);
                            jsonRet.put("note", "赎回申购成功");
                            return jsonRet;
                        } else {
                            code = (int) map_sh.get("errNo");
                            error_msg = (String) map_sh.get("errMsg");
                        }
                    }else {
                        code = -1;
                        error_msg = "正在进行在途转换";
                    }
                }else{
                    code = (int) map_zh.get("errNo");
                    error_msg = (String) map_zh.get("errMsg");
                }
            } else {
                code = (int) map_time.get("O_CODE");
                error_msg = map_time.get("O_NOTE").toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        jsonRet.put("code", -1);
        jsonRet.put("note", "错误代码:" + code + ";赎回失败:" + error_msg);
        return jsonRet;
    }

    //牛熊轮动转换
    public JSONObject transform(JSONObject req_data){
        JSONObject jsonRet = new JSONObject();
        String error_msg = "";
        int code = -1;
        try{
            Map map_time = new HashMap();
            map_time.put("O_CODE", "");
            map_time.put("O_NOTE", "");
            map_time.put("I_CZLX", 3);
            openPositionDao.isPurchaseTime(map_time);//调用本地过程判断是否转换时间
            if(null != map_time && Integer.parseInt(map_time.get("O_CODE").toString()) == 1){
                //在途转换判断
                Map map_zh = bearIfsService.queryEntrustedFunds(req_data.getString("op_branch_no"),req_data.getString("op_entrust_way"),req_data.getString("op_station"),req_data.getString("branch_no"),
                        req_data.getString("cust_no"),req_data.getString("cust_account"),req_data.getString("password"),req_data.getString("password_type"),req_data.getString("user_token"),
                        "","","",req_data.getString("secum_account"),req_data.getString("fund_code"),req_data.getString("fund_company"),"","",
                        "","","","","","","","","1000","");
                if (null != map_zh && (int) map_zh.get("errNo") == 0) {
                    List list_zh = (List) map_zh.get("list");
                    Map m = (Map) list_zh.get(0);
                    String resultList = (String) m.get("resultList");
                    List re_list = JSON.parseArray(resultList);
                    int flag = 0;
                    //查询数据库
                    Map map_ch = new HashMap();
                    map_ch.put("O_CODE", "");
                    map_ch.put("O_NOTE", "");
                    map_ch.put("O_RESULT", "");
                    map_ch.put("I_CLID", req_data.getString("stgy_id"));
                    openPositionDao.filterProduct(map_ch);
                    List list_ch = (List) map_ch.get("O_RESULT");
                    for (int i = 0; i < list_ch.size(); i++) {
                        Map m_ch = (Map) list_ch.get(i);
                        for (int j = 0; j < re_list.size(); j++) {
                            JSONObject jso = (JSONObject) re_list.get(j);
                            if (m_ch.get("PROD_CODE").equals(jso.get("fund_code")) && m_ch.get("PROD_NAME").equals(jso.get("fund_name"))) {
                                flag=1;
                                break;
                            }
                        }
                    }
                    if(flag!=1) {
                        String[] trans_out_prod = req_data.getString("trans_out_prod").split("\\;");
                        StringBuffer buffer = new StringBuffer();
                        int flag_zh = 0;
                        for (int i = 0; i < trans_out_prod.length; i++) {
                            String cpdm = trans_out_prod[i].split("\\|")[0];
                            Double zhfe = Double.valueOf(trans_out_prod[i].split("\\|")[0]);
                            //IFS转换接口
                            Map  map_Zh = bearIfsService.transform(req_data.getString("op_branch_no"), req_data.getString("op_entrust_way"), req_data.getString("op_station"), req_data.getString("branch_no"),
                                    req_data.getString("cust_no"), req_data.getString("cust_account"), req_data.getString("password"), req_data.getString("password_type"), req_data.getString("user_token"),
                                    req_data.getString("secum_account"), cpdm, req_data.getString("fund_company"), req_data.getString("switch_prod_code"), req_data.getString("allotno"),
                                    req_data.getString("exceedflag"), zhfe, req_data.getString("risk_sub_id"));
                            if (null != map_Zh && (int) map_Zh.get("errNo") == 0) {
                                List list_cx = (List) map_Zh.get("list");
                                Map m_cx = (Map) list_cx.get(0);
                                String resultList_Zh = (String) m_cx.get("resultList");
                                List Zh_list = JSON.parseArray(resultList_Zh);
                                buffer.append(Zh_list.get(1)+";");
                            }else{
                                flag_zh = 1;
                                code = (int) map_Zh.get("errNo");
                                error_msg = (String) map_Zh.get("errMsg");
                                break;
                            }
                        }
                        if(flag_zh != 1){
                            //回写数据库
                            Map map_uC = new HashMap();
                            map_uC.put("O_CODE","");
                            map_uC.put("O_NOTE","");
                            map_uC.put("I_KHH",req_data.getString("cust_no"));
                            map_uC.put("I_ZJZH",req_data.getString("cust_account"));
                            openPositionDao.updateClient(map_uC);
                            String lsh = String.valueOf(buffer.substring(buffer.lastIndexOf(";")+1,buffer.length()));
                            jsonRet.put("data", lsh);
                            jsonRet.put("code", 1);
                            jsonRet.put("note", "转换操作成功");
                            return jsonRet;
                        }
                    }
                }else{
                    code = (int) map_zh.get("errNo");
                    error_msg = (String) map_zh.get("errMsg");
                }
            }else{
                code = (int) map_time.get("O_CODE");
                error_msg = map_time.get("O_NOTE").toString();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        jsonRet.put("code", -1);
        jsonRet.put("note", "错误代码:" + code + ";转换失败:" + error_msg);
        return jsonRet;
    }

    //牛熊轮动查询转换操作成分
    public JSONObject queryTransformationBear(JSONObject req_data) {
        JSONObject jsonRet = new JSONObject();
        String error_msg = "";
        int code = -1;
        Double sum = 0.00;
        List listSum = new ArrayList();
        try {
            String zccp = req_data.getString("non_rec_prod");
            //调用牛熊轮动基金持仓查询
            Map map_cx = bearIfsService.queryFundPosition("","","",req_data.getString("cust_no"), req_data.getString("cust_account"),"",
                    "","","","","","","1","","","1000","");
            if (null != map_cx && (int) map_cx.get("errNo") == 0) {
                List list_cx = (List) map_cx.get("list");
                Map m_cx = (Map) list_cx.get(0);
                String resultList = (String) m_cx.get("resultList");
                List re_list = JSON.parseArray(resultList);
                String[] non_rec_prod = zccp.split("\\;");
                //获取不推荐转出产品的市值总和sum（即推荐转入产品金额）以及获取不推荐转出产品信息
                for (int j = 0; j < non_rec_prod.length; j++) {
                    if (re_list != null && re_list.size() > 0) {
                        for (int i = 0; i < re_list.size(); i++) {
                            JSONObject jso = (JSONObject) re_list.get(i);
                            StringBuffer buffer = new StringBuffer();
                            buffer.append(jso.getString("fund_code") + "|" + jso.getString("fund_company"));
                            String code_company = String.valueOf(buffer);
                            if (non_rec_prod[j].equals(code_company)) {
                                //市值总和 转入产品金额
                                sum += jso.getDouble("market_value");
                                //转出产品信息
                                JSONObject json = new JSONObject();
                                json.put("tran_type", "2");
                                json.put("prod_code", jso.getString("fund_code"));
                                json.put("fund_company", jso.getString("fund_company"));
                                json.put("prod_name", jso.getString("fund_name"));
                                json.put("tran_amount", jso.getString("current_share"));
                                listSum.add(json);
                            }
                        }
                    }
                }
                //获取转入产品信息
                Map map_zr = new HashMap();
                map_zr.put("O_CODE", "");
                map_zr.put("O_NOTE", "");
                map_zr.put("O_RESULT", "");
                map_zr.put("I_CPDMJJGSDM", req_data.getString("rec_prod"));
                map_zr.put("I_ZEJE", sum);
                openPositionDao.productName(map_zr);
                List list_zr = (List) map_zr.get("O_RESULT");
                Map m_zr = (Map) list_zr.get(0);
                JSONObject json_zr = new JSONObject();
                json_zr.put("tran_type", m_zr.get("TRAN_TYPE"));
                json_zr.put("prod_code", m_zr.get("PROD_CODE"));
                json_zr.put("fund_company", m_zr.get("FUND_COMPANY"));
                json_zr.put("prod_name", m_zr.get("PROD_NAME"));
                json_zr.put("tran_amount", m_zr.get("TRAN_AMOUNT"));
                listSum.add(json_zr);

                jsonRet.put("data", listSum);
                jsonRet.put("code", 1);
                jsonRet.put("note", "查询转换操作成分成功");
                return jsonRet;
            } else {
                code = (int) map_cx.get("errNo");
                error_msg = (String) map_cx.get("errMsg");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        jsonRet.put("code", -1);
        jsonRet.put("note", "错误代码:" + code + ";查询转换操作成分失败:" + error_msg);
        return jsonRet;
    }

    //牛熊轮动查询客户持仓明细
    public JSONObject queryCustomer(JSONObject req_data) {
        JSONObject jsonRet = new JSONObject();
        String error_msg = "";
        int code = -1;
        List listSum = new ArrayList();
        try {
            //调用牛熊轮动基金持仓查询
            Map map_cx = bearIfsService.queryFundPosition("","","",req_data.getString("cust_no"), req_data.getString("cust_account"),"",
                    "","","","","","","1","","","1000","");
            if (null != map_cx && (int) map_cx.get("errNo") == 0) {
                List list_cx = (List) map_cx.get("list");
                Map m_cx = (Map) list_cx.get(0);
                String resultList = (String) m_cx.get("resultList");
                List re_list = JSON.parseArray(resultList);
                //查询数据库
                Map map_ch = new HashMap();
                map_ch.put("O_CODE", "");
                map_ch.put("O_NOTE", "");
                map_ch.put("O_RESULT", "");
                map_ch.put("I_CLID", req_data.getString("stgy_id"));
                openPositionDao.filterProduct(map_ch);
                List list_ch = (List) map_ch.get("O_RESULT");
                //持仓查询和数据库查询对比
                for (int i = 0; i < list_ch.size(); i++) {
                    Map m_ch = (Map) list_ch.get(i);
                    for (int j = 0; j < re_list.size(); j++) {
                        JSONObject jso = (JSONObject) re_list.get(j);
                        if (m_ch.get("PROD_CODE").equals(jso.get("fund_code")) && m_ch.get("PROD_NAME").equals(jso.get("fund_name"))) {
                            JSONObject json = new JSONObject();
                            json.put("prod_code", jso.getString("fund_code"));
                            json.put("fund_company", jso.getString("fund_company"));
                            json.put("prod_name", jso.getString("fund_name"));
                            json.put("avail_share", jso.getString("enable_shares"));
                            json.put("avail_market_value", jso.getString("market_value"));
                            json.put("is_redeem", "1");
                            listSum.add(json);
                        }
                    }
                }
                jsonRet.put("data", listSum);
                jsonRet.put("code", 1);
                jsonRet.put("note", "查询客户持仓明细成功");
                return jsonRet;

            } else {
                code = (int) map_cx.get("errNo");
                error_msg = (String) map_cx.get("errMsg");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        jsonRet.put("code", -1);
        jsonRet.put("note", "错误代码:" + code + ";查询客户持仓明细失败:" + error_msg);
        return jsonRet;
    }
}
