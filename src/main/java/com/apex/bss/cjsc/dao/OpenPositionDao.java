package com.apex.bss.cjsc.dao;



import java.util.List;
import java.util.Map;

/**
 * Created by Lenovo on 2017/1/11.
 */

public interface OpenPositionDao {
    public Map openNew(Map map);//账户开户
    public Map modifyConfig(Map map);//修改当前配置方案
    public Map queryPositon(Map map);//查询组合当前持仓份额
    public Map synchronizeProduct(Map map);//产品同步
    public Map orderProcessing(Map map);//处理本地订单
    public Map judgmentDay(Map map);//判断交易日
    public Map selectOp_station(Map map);//获取站点地址
    public List<Map> queryPurchase();//产品调整对应后台申购
    public List<Map> redemptionMoney();//划拨组合赎回完成金额
    public List<Map> fundIn(int number);//自动定投资金划入
    public List<Map> applyPurchase(int number);//自动定投申购
    public Map allocationStatus(Map map);//记录组合资金划拨状态
    public Map allotMoney(Map map);//资金划拨结果
    public Map isTradeTime(Map map);//判断交易时间
    public Map masterLog(Map map);//总调度日志处理
    public Map subLog(Map map);//单独任务日志处理
    public Map isOpen(Map map);//判断当前客户是否开通对应组合的业务 首次申购专用
    public Map queryAccount(Map map);//查询账户基本信息
    public Map fundlog(Map map);//余额转货基日志
    public Map purchaseErr(Map map);//申购失败返回信息
    public Map bearPurchase(Map map);//牛熊轮动购买回写账户信息
    public Map productName(Map map);//牛熊轮动查询转换名称
    public Map filterProduct(Map map);//牛熊轮动查询客户持仓明细筛选
    public Map isPurchaseTime(Map map);//申购时间判断
    public Map updateClient(Map map);//更新客户消息执行状态
}
