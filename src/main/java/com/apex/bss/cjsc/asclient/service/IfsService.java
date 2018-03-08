package com.apex.bss.cjsc.asclient.service;

import com.alibaba.fastjson.JSONObject;
import com.apex.bss.cjsc.common.Common;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Lenovo on 2017/2/25.
 */
@Controller
@RequestMapping("/processPlatform")
public class IfsService {

    AsClientService as_ClientService =new AsClientService();

    //IFS组合申购接口
    public Map fundPurchase(String op_station,String branch_no,String client_id,String fund_account, String combine_stock_no,
                            String channel_type,String entrust_way,String combine_data){
        Map map=new HashMap();
        map.put("hs_func_no","HS855072");//业务功能号
        map.put("op_branch_no", Common.op_branch_no);//操作分支机构
        map.put("operator_no",Common.operator_no);//操作员编号
        map.put("user_type", Common.user_type);//用户类别
        map.put("op_password",Common.op_password);//操作员密码
        map.put("op_station",op_station);//站点地址
        map.put("branch_no",branch_no);//分支机构
        map.put("client_id",client_id);//客户编号
        map.put("fund_account",fund_account);//资产账户
        map.put("combine_stock_no",combine_stock_no);//组合持仓编号
        map.put("ofchannel_type",channel_type);//场外基金渠道类型
        map.put("entrust_way",entrust_way);//委托方式
        map.put("combine_data",combine_data);//组合委托成份清
        Map map_sg = as_ClientService.getResultListMap(140346,map);//调用组合基金申购接口
        return map_sg;
    }

    //IFS资金划拨接口
    public Map capitalAllocation(String op_station, String branch_no, String client_id, String fund_account, String combine_stock_no,
                                 Double occur_balance, String Money_type, String trans_direction){
        Map map=new HashMap();
        map.put("hs_func_no","HS855075");//业务功能号
        map.put("op_branch_no",Common.op_branch_no);//操作分支机构
        map.put("operator_no",Common.operator_no);//操作员编号
        map.put("user_type", Common.user_type);//用户类别
        map.put("op_password",Common.op_password);//操作员密码
        map.put("op_station",op_station);//站点地址
        map.put("branch_no",branch_no);//分支机构
        map.put("client_id", client_id);//客户编号
        map.put("fund_account",fund_account);//资产账户
        map.put("combine_stock_no",combine_stock_no);//持仓编号
        map.put("occur_balance", BigDecimal.valueOf(occur_balance).toPlainString());//发生金额
        map.put("money_type",Money_type);//币种
        map.put("trans_direction",trans_direction);//调拨方向
        Map map_hb = as_ClientService.getResultListMap(140346,map);//调用资金划拨接口
        return map_hb;
    }



    //IFS组合基金赎回
    public Map fundRedemption(String op_station,String branch_no,String client_id,String fund_account,String ofchannel_type,
                              String entrust_way,String combine_stock_no,Double balance,String combine_data){
        Map paramMap = new HashMap();
        paramMap.put("hs_func_no", "HS855073");//业务功能号
        paramMap.put("op_branch_no", Common.op_branch_no);//操作分支机构
        paramMap.put("operator_no", Common.operator_no);//操作员编号
        paramMap.put("user_type", Common.user_type);//用户类别
        paramMap.put("op_password", Common.op_password);//操作员密码
        paramMap.put("op_station", op_station);//站点地址
        paramMap.put("branch_no", branch_no);//分支机构
        paramMap.put("client_id", client_id);//客户编号
        paramMap.put("fund_account", fund_account);//资产账户
        paramMap.put("ofchannel_type", ofchannel_type);//场外基金渠道类型
        paramMap.put("entrust_way", entrust_way);//委托方式
        paramMap.put("combine_stock_no",combine_stock_no);//持仓编号
        paramMap.put("balance",BigDecimal.valueOf(balance).toPlainString());//
        paramMap.put("combine_data", combine_data);//组合委托成份清单
        Map map_SH = as_ClientService.getResultListMap(140346, paramMap);//调用组合基金赎回接口
        return map_SH;
    }


    //IFS组合平仓
    public  Map fundLiquidation(String op_station,String branch_no,String client_id,String fund_account,String combine_stock_no){
        Map paramMap = new HashMap();
        paramMap.put("hs_func_no", "HS855071");//业务功能号
        paramMap.put("op_branch_no", Common.op_branch_no);//操作分支机构
        paramMap.put("operator_no", Common.operator_no);//操作员编号
        paramMap.put("user_type", Common.user_type);//用户类别
        paramMap.put("op_password", Common.op_password);//操作员密码
        paramMap.put("op_station", op_station);//站点地址
        paramMap.put("branch_no", branch_no);//分支机构
        paramMap.put("client_id", client_id);//客户编号
        paramMap.put("fund_account", fund_account);//资产账户
        paramMap.put("combine_stock_no",combine_stock_no);//持仓编号
        Map map_PC = as_ClientService.getResultListMap(140346, paramMap);//调用组合基金赎回接口
        return map_PC;
    }

    //IFS组合开仓接口
    public Map fundOrderSend(String op_station,String branch_no,String client_id,String fund_account,String combine_serial){
        Map paramMap = new HashMap();
        paramMap.put("hs_func_no","HS855070");//业务功能号
        paramMap.put("op_branch_no",Common.op_branch_no);//操作分支机构
        paramMap.put("operator_no", Common.operator_no);//操作员编号
        paramMap.put("user_type", Common.user_type);//用户类别
        paramMap.put("op_password", Common.op_password);//操作员密码
        paramMap.put("op_station",op_station);//站点地址
        paramMap.put("branch_no",branch_no);//分支机构
        paramMap.put("client_id",client_id);//客户编号
        paramMap.put("fund_account",fund_account);//资产账户
        paramMap.put("combine_serial",combine_serial);//产品池编号
        Map map_KC = as_ClientService.getResultListMap(140346, paramMap);//调用开仓接口
        return map_KC;
    }


    //IFS查询账户余额
    public Map queryMoney(String op_station,String combine_stock_no){
        Map pramMap = new HashMap();
        pramMap.put("hs_func_no","HS855912");//业务功能号
        pramMap.put("op_branch_no", Common.op_branch_no);//操作分支机构
        pramMap.put("operator_no",Common.operator_no);//操作员编号
        pramMap.put("user_type", Common.user_type);//用户类别
        pramMap.put("op_password",Common.op_password);//操作员密码
        pramMap.put("op_station",op_station);//站点地址
        pramMap.put("client_id","");//客户编号
        pramMap.put("fund_account","");//资产账户
        pramMap.put("request_num","");//请求行数
        pramMap.put("combine_serial","");//产品池编号
        pramMap.put("combine_stock_no",combine_stock_no);//持仓编号
        pramMap.put("position_str","");//定位串
        Map map_YE = as_ClientService.getResultListMap(140346,pramMap);//调用查询组合余额接口
        return map_YE;
    }


    //IFS撤单操作
    public Map cancel(String op_station,String branch_no,String client_id,String fund_account,String acpt_id,String ofchannel_type,String entrust_way){
        Map paramMap=new HashMap();
        paramMap.put("hs_func_no","HS855074");//业务功能号
        paramMap.put("op_branch_no",Common.op_branch_no);//操作分支机构
        paramMap.put("operator_no",Common.operator_no);//操作员编号
        paramMap.put("user_type", Common.user_type);//用户类别
        paramMap.put("op_password",Common.op_password);//操作员密码
        paramMap.put("op_station",op_station);//站点地址
        paramMap.put("branch_no",branch_no);//分支机构
        paramMap.put("client_id",client_id);//客户编号
        paramMap.put("fund_account",fund_account);//资产账户
        paramMap.put("combine_entrust_no_orginal",acpt_id);//原委托编号
        paramMap.put("ofchannel_type",ofchannel_type);//渠道类型
        paramMap.put("entrust_way",entrust_way);//委托方式
        Map map_CD = as_ClientService.getResultListMap(140346,paramMap);//组合撤单
        return map_CD;
    }

    //IFS组合成份委托查询
    public Map queryAmount(String op_station,String combine_entrust_no){
        Map paramMap=new HashMap();
        paramMap.put("hs_func_no","HS855913");//业务功能号
        paramMap.put("op_branch_no",Common.op_branch_no);//操作分支机构
        paramMap.put("operator_no",Common.operator_no);//操作员编号
        paramMap.put("user_type", Common.user_type);//用户类别
        paramMap.put("op_password",Common.op_password);//操作员密码
        paramMap.put("op_station",op_station);//站点地址
        paramMap.put("branch_no","");//分支机构
        paramMap.put("client_id","");//客户编号
        paramMap.put("fund_account","");//资产账户
        paramMap.put("combine_serial","");//产品池编号
        paramMap.put("combine_stock_no","");//对应到组合持仓表
        paramMap.put("prod_code","");//产品代码
        paramMap.put("fund_company","");//基金公司
        paramMap.put("init_date","");//交易日期
        paramMap.put("serial_no","");//流水序号
        paramMap.put("entrust_status","");//委托状态
        paramMap.put("combine_entrust_no",combine_entrust_no);//组合委托编号
        paramMap.put("position_str","");//定位串
        Map map_QA = as_ClientService.getResultListMap(140346,paramMap);//组合成份委托查询
        return map_QA;
    }

    //IFS组合历史成份委托查询
    public Map queryHis_amount(String op_station,String client_id,String fund_account,String combine_entrust_no,String occur_date) throws ParseException {
        Calendar c = Calendar.getInstance();
        String time=occur_date.substring(0,4)+occur_date.substring(5,7)+occur_date.substring(8,10);
        SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
        Date date = sdf.parse(time);
        c.setTime(date);
        c.add(Calendar.DATE,1);
        Date d = c.getTime();
        String day = sdf.format(d);
        System.out.println("后一天："+day);
        c.setTime(d);
        c.add(Calendar.MONTH, -1);
        Date m = c.getTime();
        String mon = sdf.format(m);
        System.out.println("过去一个月："+mon);
        Map paramMap=new HashMap();
        paramMap.put("hs_func_no","HS855903");//业务功能号
        paramMap.put("op_branch_no", Common.op_branch_no);//操作分支机构
        paramMap.put("operator_no",Common.operator_no);//操作员编号
        paramMap.put("user_type", Common.user_type);//用户类别
        paramMap.put("op_password",Common.op_password);//操作员密码
        paramMap.put("op_station",op_station);//站点地址
        paramMap.put("op_entrust_way","");//委托方式
        paramMap.put("branch_no","");//分支机构
        paramMap.put("client_id",client_id);//客户编号
        paramMap.put("fund_account",fund_account);//资产账户
        paramMap.put("combine_stock_no","");//对应到组合持仓表
        paramMap.put("allotno","");//申请编号
        paramMap.put("entrust_status","");//委托状态
        paramMap.put("combine_entrust_no",combine_entrust_no);//组合委托编号
        paramMap.put("position_str","");//定位串
        paramMap.put("start_date",mon);//开始日期
        paramMap.put("end_date",day);//到期日期
        paramMap.put("request_num","");//请求行数
        Map map_QA = as_ClientService.getResultListMap(140346,paramMap);//组合历史成份委托查询
        return map_QA;
    }


    //IFS余额转货基
    public Map queryAccount(String op_station,String combine_serial,String ofchannel_type,String entrust_way){
        Map paramMap=new HashMap();
        paramMap.put("hs_func_no","HS855078");//业务功能号
        paramMap.put("op_branch_no",Common.op_branch_no);//操作分支机构
        paramMap.put("operator_no",Common.operator_no);//操作员编号
        paramMap.put("user_type", Common.user_type);//用户类别
        paramMap.put("op_password",Common.op_password);//操作员密码
        paramMap.put("op_station",op_station);//站点地址
        paramMap.put("combine_serial",combine_serial);//产品池编号
        paramMap.put("ofchannel_type",ofchannel_type);//渠道类型
        paramMap.put("entrust_way",entrust_way);//委托方式
        Map map_YE = as_ClientService.getResultListMap(140346,paramMap);//余额转货基
        return map_YE;
    }



    //异步写入PRO_PIF_TZZH_ZHFA 组合方案
    public JSONObject combineScheme(String I_CZLX,String I_KHLX,String I_KHH,String I_ZJZH,String I_ZHBH,
                                   String I_YWLXBM,String I_PBBM,String I_CPID, String I_OP_STATION) {
        JSONObject json_fa = new JSONObject();
        json_fa.put("I_CZLX", I_CZLX);//操作类型
        json_fa.put("I_KHLX", I_KHLX);//客户类型
        json_fa.put("I_KHH", I_KHH);//客户号
        json_fa.put("I_ZJZH", I_ZJZH);//资金账号
        json_fa.put("I_ZHBH", I_ZHBH);//组合编号
        json_fa.put("I_YWLXBM", I_YWLXBM);//业务类型编码
        json_fa.put("I_PBBM", I_PBBM);//配比编码
        json_fa.put("I_CPID", I_CPID);//产品ID连接串,用分号隔开
        json_fa.put("I_OP_STATION", I_OP_STATION);//站点地址
        return json_fa;
    }

    //异步写入PRO_PIF_TZZH_DTFA 定投方案
    public JSONObject investmentScheme(String I_KHLX,String I_KHH,String I_ZJZH,String I_ZHBH,String I_YWLXBM,
                              Integer I_DTRQ,Double I_DTJE,String I_KSRQ,Integer I_DTQX,Double I_CSTRJE,
                              Double I_MBJE, String I_OP_STATION) {
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String dateNowStr = sdf.format(d);
        JSONObject json_dt = new JSONObject();
        json_dt.put("I_KHLX", I_KHLX);//客户类型
        json_dt.put("I_KHH", I_KHH);//客户号
        json_dt.put("I_ZJZH", I_ZJZH);//资金账号
        json_dt.put("I_ZHBH", I_ZHBH);//组合编号
        json_dt.put("I_YWLXBM", I_YWLXBM);//业务类型编码
        json_dt.put("I_DTRQ", I_DTRQ);//定投日期
        json_dt.put("I_DTJE", I_DTJE);//每期定投金额
        json_dt.put("I_KSRQ", dateNowStr);//开始日期
        json_dt.put("I_DTQX", I_DTQX);//定投期数 ,年
        json_dt.put("I_CSTRJE", I_CSTRJE);//初始投入金额
        json_dt.put("I_MBJE", I_MBJE);//目标金额
        json_dt.put("I_OP_STATION", I_OP_STATION);//站点地址
        return json_dt;
    }

    //异步写入PRO_PIF_ZHJY_JYCZ 交易操作
    public JSONObject tradingOperation(String I_CZLX,String I_KHLX,String I_KHH,String I_ZJZH,String I_ZHBH,
                              Integer I_DDLX,String I_CPDM,String I_CZLSH,Double I_HBJE, String I_OP_STATION,
                              String I_ZHSLDH,String I_ZHWTBH,String I_ZHDDZT){
        JSONObject json_jy = new JSONObject();
        json_jy.put("I_CZLX", I_CZLX);//操作类型
        json_jy.put("I_KHLX", I_KHLX);//客户类型
        json_jy.put("I_KHH", I_KHH);//客户号
        json_jy.put("I_ZJZH", I_ZJZH);//资金账号
        json_jy.put("I_ZHBH", I_ZHBH);//组合编号
        json_jy.put("I_DDLX", I_DDLX);//1|申购;2|赎回;3|撤单;0|产品调整未赎回
        json_jy.put("I_CPDM",I_CPDM);//产品代码|金额/份额的分号连接串
        json_jy.put("I_CZLSH",I_CZLSH);//操作流水号
        json_jy.put("I_HBJE", I_HBJE);//资金划拨金额
        json_jy.put("I_OP_STATION", I_OP_STATION);//站点地址
        json_jy.put("I_ZHSLDH", I_ZHSLDH);//组合IFS受理单号
        json_jy.put("I_ZHWTBH", I_ZHWTBH);//组合委托编号
        json_jy.put("I_ZHDDZT", I_ZHDDZT);//组合订单状态
        return json_jy;
    }

    //异步写入PRO_PIF_JYCZ_CZGL 操作管理
    public JSONObject operationGuidance(String I_CZLX,String I_CZLSH,String I_ZHBH,String I_OP_STATION,String I_CZZT,
                              Double I_HBJE,String I_CPDM,String I_DDLX){
        JSONObject json_gl = new JSONObject();
        json_gl.put("I_CZLX",I_CZLX);//操作类型 撤单传0
        json_gl.put("I_CZLSH",I_CZLSH);//撤单时传入操作流水号
        json_gl.put("I_ZHBH",I_ZHBH);//组合编号
        json_gl.put("I_OP_STATION",I_OP_STATION);//站点地址
        json_gl.put("I_CZZT",I_CZZT);//操作状态
        json_gl.put("I_HBJE",I_HBJE);//划拨金额
        json_gl.put("I_CPDM",I_CPDM);//申购、赎回产品成分，撤单时传空
        json_gl.put("I_DDLX",I_DDLX);//1|申购;2|赎回;3|撤单
        return json_gl;
    }

    //异步写入首次申购 PRO_PIF_JYCZ_SCSG
    public JSONObject firstPurchase(String I_KHLX,String I_KHH,String I_ZJZH,String I_ZHBH,String I_YWLXBM,
                                    Integer I_DTRQ, Double I_DTJE,Integer I_DTQX,Double I_CSTRJE,
                                    Double I_MBJE,String I_PBBM, String I_CPID,String I_CPDM,String I_ZHSLDH,
                                    String I_ZHWTBH,String I_ZHDDZT,Double I_HBJE,String I_OP_STATION){
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String dateNowStr = sdf.format(d);
        JSONObject json_purchase = new JSONObject();
        json_purchase.put("I_KHLX",I_KHLX);//客户类型
        json_purchase.put("I_KHH",I_KHH);//客户号
        json_purchase.put("I_ZJZH",I_ZJZH);//资金账号
        json_purchase.put("I_ZHBH",I_ZHBH);//组合编号
        json_purchase.put("I_YWLXBM",I_YWLXBM);//业务类型编码
        json_purchase.put("I_DTRQ",I_DTRQ);//定投日期
        json_purchase.put("I_DTJE",I_DTJE);//每期定投金额
        json_purchase.put("I_KSRQ",dateNowStr);//开始日期
        json_purchase.put("I_DTQX",I_DTQX);//定投期数 ,年
        json_purchase.put("I_CSTRJE",I_CSTRJE);//初始投入金额
        json_purchase.put("I_MBJE",I_MBJE);//目标金额
        json_purchase.put("I_PBBM",I_PBBM);//配比编码
        json_purchase.put("I_CPID",I_CPID);//产品ID连接串,用分号隔开
        json_purchase.put("I_CPDM",I_CPDM);//产品代码|金额/份额的分号连接串
        json_purchase.put("I_ZHSLDH",I_ZHSLDH);//组合IFS受理单号
        json_purchase.put("I_ZHWTBH",I_ZHWTBH);//组合委托编号
        json_purchase.put("I_ZHDDZT",I_ZHDDZT);//组合订单状态 -4|已提交申请;-3|未受理;-2|受理中;-1|受理废单
        json_purchase.put("I_HBJE",I_HBJE);//资金划拨金额 首次申购金额
        json_purchase.put("I_OP_STATION",I_OP_STATION);//站点地址
        return json_purchase;
    }

}
