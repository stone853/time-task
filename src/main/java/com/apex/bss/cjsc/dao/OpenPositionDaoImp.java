package com.apex.bss.cjsc.dao;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;
import java.util.Map;

/**
 * Created by Lenovo on 2017/2/23.
 */
public class OpenPositionDaoImp implements OpenPositionDao{
    private OpenPositionDao openPositionDao;
    public void init(){
        ApplicationContext context=new ClassPathXmlApplicationContext("classpath:/spring/applicationContext.xml");
        openPositionDao=(OpenPositionDao)context.getBean("openPositionDao");
    }

    @Override
    public Map modifyConfig(Map map) {
        return null;
    }

    @Override
    public Map openNew(Map map) {
        return null;
    }

    @Override
    public Map queryPositon(Map map) {
        return null;
    }

    @Override
    public Map synchronizeProduct(Map map) {
        return null;
    }

    @Override
    public Map orderProcessing(Map map) {
        return null;
    }

    @Override
    public Map judgmentDay(Map map) {
        openPositionDao.judgmentDay(map);
        return map;
    }

    @Override
    public Map selectOp_station(Map map) {
        return null;
    }

    @Override
    public List<Map> queryPurchase() {
        return null;
    }

    @Override
    public List<Map> redemptionMoney(){
        return null;
    }

    @Override
    public List<Map> fundIn(int number){
        return null;
    }

    @Override
    public List<Map> applyPurchase(int number){
        return null;
    }

    @Override
    public Map allocationStatus(Map map){
        return null;
    }

    @Override
    public Map allotMoney(Map map){
        return null;
    }

    @Override
    //判断交易时间
    public Map isTradeTime(Map map){
        return null;
    }

    @Override
    //总调度日志处理
    public Map masterLog(Map map){
        return null;
    }

    @Override
    //单独任务日志处理
    public Map subLog(Map map){
        return null;
    }

    @Override
    //判断当前客户是否开通对应组合的业务 首次申购专用
    public Map isOpen(Map map){
        return null;
    }

    @Override
    //查询账户基本信息
    public Map queryAccount(Map map){
        return null;
    }

    @Override
    //查询账户基本信息
    public Map fundlog(Map map){
        return null;
    }

    @Override
    public Map purchaseErr(Map map) {
        return null;
    }

    @Override
    public Map bearPurchase(Map map) {
        return null;
    }

    @Override
    public Map productName(Map map) {
        return null;
    }

    @Override
    public Map filterProduct(Map map) {
        return null;
    }

    @Override
    public Map isPurchaseTime(Map map) {
        return null;
    }

    @Override
    public Map updateClient(Map map) {
        return null;
    }

}
