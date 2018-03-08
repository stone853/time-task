package com.apex.bss.cjsc.asclient.service;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Saphurot on 2017/5/3.
 */
@Controller
@RequestMapping("/processPlatform")
public class BearIfsService {
    AsClientService as_ClientService =new AsClientService();

    //IFS牛熊轮动基金申购 337412
    public Map bearFundPurchase(String op_branch_no, String op_entrust_way, String op_station, String branch_no, String client_id, String fund_account, String password,
                                String password_type, String user_token, String secum_account, String prod_code, String prodta_no, Double entrust_balance, String developer,
                                String elig_confirm_type, String risk_sub_id){
        Map map=new HashMap();
        map.put("hs_func_no","337412");//业务功能号
        map.put("op_branch_no",op_branch_no);//操作分支机构
        map.put("op_entrust_way",op_entrust_way);//委托方式
        map.put("op_station",op_station);//站点地址
        map.put("branch_no",branch_no);//分支机构
        map.put("client_id",client_id);//客户编号
        map.put("fund_account",fund_account);//资产账户
        map.put("password",password);//密码
        map.put("password_type",password_type);//密码类别
        map.put("user_token",user_token);//用户口令
        map.put("secum_account",secum_account);//证券理财账号
        map.put("prod_code",prod_code);//产品代码
        map.put("prodta_no",prodta_no);//产品TA编号
        map.put("entrust_balance", BigDecimal.valueOf(entrust_balance).toPlainString());//委托金额
        map.put("developer",developer);//开发人员
        map.put("elig_confirm_type",elig_confirm_type);//适当性信息确认类型
        map.put("risk_sub_id",risk_sub_id);//风险揭示书协议号
        Map map_sg = as_ClientService.getResultListMap(140346,map);//调用牛熊基金申购接口
        return map_sg;

    }

    //IFS牛熊轮动基金赎回 337413
    public Map bearFundRedemption(String op_branch_no, String op_entrust_way, String op_station, String branch_no, String client_id, String fund_account, String password,
                                  String password_type, String user_token, String secum_account, String prod_code, String prodta_no, Double amount, String allot_no,
                                  String exceedflag, String developer){
        Map map=new HashMap();
        map.put("hs_func_no","337413");//业务功能号
        map.put("op_branch_no",op_branch_no);//操作分支机构
        map.put("op_entrust_way",op_entrust_way);//委托方式
        map.put("op_station",op_station);//站点地址
        map.put("branch_no",branch_no);//分支机构
        map.put("client_id",client_id);//客户编号
        map.put("fund_account",fund_account);//资产账户
        map.put("password",password);//密码
        map.put("password_type",password_type);//密码类别
        map.put("user_token",user_token);//用户口令
        map.put("secum_account",secum_account);//证券理财账号
        map.put("prod_code",prod_code);//产品代码
        map.put("prodta_no",prodta_no);//产品TA编号
        map.put("amount",BigDecimal.valueOf(amount).toPlainString());//持仓量
        map.put("allot_no",allot_no);//申请编号
        map.put("exceedflag",exceedflag);//巨额赎回处理办法
        map.put("developer",developer);//开发人员
        Map map_sh = as_ClientService.getResultListMap(140346,map);//调用牛熊基金赎回接口
        return map_sh;
    }

    //IFS牛熊轮动基金转换 337415
    public Map transform(String op_branch_no, String op_entrust_way, String op_station, String branch_no, String client_id, String fund_account, String password,
                         String password_type, String user_token, String secum_account, String prod_code, String prodta_no, String switch_prod_code,
                         String allot_no, String exceedflag, Double trans_amount, String risk_sub_id){
        Map map = new HashMap();
        map.put("hs_func_no","337415");//业务功能号
        map.put("op_branch_no",op_branch_no);//操作分支机构
        map.put("op_entrust_way",op_entrust_way);//委托方式
        map.put("op_station",op_station);//站点地址
        map.put("branch_no",branch_no);//分支机构
        map.put("client_id",client_id);//客户编号
        map.put("fund_account",fund_account);//资产账户
        map.put("password",password);//密码
        map.put("password_type",password_type);//密码类别
        map.put("user_token",user_token);//用户口令
        map.put("secum_account",secum_account);//证券理财账号
        map.put("prod_code",prod_code);//产品代码
        map.put("prodta_no",prodta_no);//产品TA编号
        map.put("switch_prod_code",switch_prod_code);//转换代码
        map.put("allot_no",allot_no);//申请编号
        map.put("exceedflag",exceedflag);//巨额赎回处理办法
        map.put("trans_amount",BigDecimal.valueOf(trans_amount).toPlainString());//转换份额
        map.put("risk_sub_id",risk_sub_id);//风险揭示书协议号
        Map map_zh = as_ClientService.getResultListMap(140346,map);//调用牛熊基金转换接口
        return map_zh;
    }

    //IFS牛熊轮动基金持仓查询 HS855092
    public Map queryFundPosition(String op_entrust_way, String op_station, String branch_no, String client_id, String fund_account, String password,
                                 String password_type, String user_token, String secum_account, String prod_code, String prodta_no, String allot_no, String query_mode,
                                 String trans_account, String en_prodcode_kind, String request_num, String position_str){
        Map map = new HashMap();
        map.put("hs_func_no","HS855092");//业务功能号
        map.put("op_entrust_way",op_entrust_way);//委托方式
        map.put("op_station",op_station);//站点地址
        map.put("branch_no",branch_no);//分支机构
        map.put("client_id",client_id);//客户编号
        map.put("fund_account",fund_account);//资产账户
        map.put("password",password);//密码
        map.put("password_type",password_type);//密码类别
        map.put("user_token",user_token);//用户口令
        map.put("secum_account",secum_account);//证券理财账号
        map.put("prod_code",prod_code);//产品代码
        map.put("prodta_no",prodta_no);//产品TA编号
        map.put("allot_no",allot_no);//申请编号
        map.put("query_mode",query_mode);//查询模式
        map.put("trans_account",trans_account);//交易账号
        map.put("en_prodcode_kind",en_prodcode_kind);//允许产品代码种类
        map.put("request_num",request_num);//请求行数
        map.put("position_str",position_str);//定位串
        Map map_ch = as_ClientService.getResultListMap(140346,map);//调用牛熊基金持仓查询接口
        return map_ch;
    }

    //IFS牛熊轮动基金委托查询 HS855093
    public Map queryEntrustedFunds(String op_branch_no, String op_entrust_way, String op_station, String branch_no, String client_id, String fund_account, String password,
                                   String password_type, String user_token, String entrust_date, String actp_id, String allot_no, String secum_account, String prod_code,
                                   String prodta_no, String en_entrust_status, String query_type, String query_mode, String prodquery_mode, String co_serial_no,
                                   String en_business_flag, String en_invpay_status, String en_pay_kind, String sort_direction, String en_prodcode_kind,
                                   String request_num, String position_str){
        Map map = new HashMap();
        map.put("hs_func_no","HS855093");//业务功能号
        map.put("op_branch_no",op_branch_no);//操作分支机构
        map.put("op_entrust_way",op_entrust_way);//委托方式
        map.put("op_station",op_station);//站点地址
        map.put("branch_no",branch_no);//分支机构
        map.put("client_id",client_id);//客户编号
        map.put("fund_account",fund_account);//资产账户
        map.put("password",password);//密码
        map.put("password_type",password_type);//密码类别
        map.put("user_token",user_token);//用户口令
        map.put("entrust_date",entrust_date);//委托日期
        map.put("actp_id",actp_id);//受理编号
        map.put("allot_no",allot_no);//申请编号
        map.put("secum_account",secum_account);//证券理财账号
        map.put("prod_code",prod_code);//产品代码
        map.put("prodta_no",prodta_no);//产品TA编号
        map.put("en_entrust_status",en_entrust_status);//允许委托状态
        map.put("query_type",query_type);//查询类别
        map.put("query_mode",query_mode);//查询模式
        map.put("prodquery_mode",prodquery_mode);//产品查询模式
        map.put("co_serial_no",co_serial_no);//协作流水号
        map.put("en_business_flag",en_business_flag);//包含的业务标志
        map.put("en_invpay_status",en_invpay_status);//允许资金支付状态
        map.put("en_pay_kind",en_pay_kind);//允许支付方式
        map.put("sort_direction",sort_direction);//返回排序方式
        map.put("en_prodcode_kind",en_prodcode_kind);//允许产品代码种类
        map.put("request_num",request_num);//请求行数
        map.put("position_str",position_str);//定位串
        Map map_ch = as_ClientService.getResultListMap(140346,map);//调用牛熊基金委托查询接口
        return map_ch;
    }
}
