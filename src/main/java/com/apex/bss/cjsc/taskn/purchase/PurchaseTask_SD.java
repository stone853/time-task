package com.apex.bss.cjsc.taskn.purchase;

import com.alibaba.fastjson.JSONObject;
import com.apex.bss.cjsc.dao.OpenPositionDao;
import com.apex.bss.cjsc.redis.RedisUtil;
import com.apex.bss.cjsc.taskn.TaskInfo;
import com.apex.bss.cjsc.taskn.TaskLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * Created by jinsh on 2017/3/14.
 * 定时查询数据库中的任务，将其插入redis队列中
 */
public class PurchaseTask_SD extends Thread{
    protected static final Logger log= LoggerFactory.getLogger(PurchaseTask_SD.class);
    private OpenPositionDao openPositionDao;
    private String opStation;
    private RedisUtil rs;

    public PurchaseTask_SD(OpenPositionDao openPositionDao, String opStation){
        this.openPositionDao = openPositionDao;
        this.opStation = opStation;
        this.rs = new RedisUtil();
    }

    /**
     * 开始任务
     */
    public void run() {
        try{
            Map<String,Object> map_date = new HashMap();
            map_date.put("I_RQ","");
            map_date.put("O_ISVALID","");
            log.debug("交易时间入参:"+map_date.toString());
            openPositionDao.judgmentDay(map_date);
            log.debug("交易时间出参:"+map_date.toString());
            int date = Integer.parseInt(map_date.get("O_ISVALID").toString());
            if(map_date != null && date == 1){
                this.execQry();
            }else{
                log.info("今天不是交易日");
            }

        }catch (Exception e){
            e.printStackTrace();
            log.error("定时任务【4】异常...");
        }
    }

    /**
     * 开始执行轮询
     */
    private void execQry(){
        try {
            log.info("查询数据库开始... step 1");
            /**记录日志**/
            Long startTime = System.currentTimeMillis();
            TaskLog taskLog = TaskLog.getTaskLog();
            taskLog.insPurchaseLog(openPositionDao,"4","1",new Long(0),new Long(0),"3",0,0,0,"定时4开始提取数据",opStation);
            /**end**/
            List list=openPositionDao.queryPurchase();
            log.debug("产品调整对应后台申购:"+list.toString());
            log.info("开始处理数据... step 2");
            int count = 0;
            if(null !=list&&list.size()>0) {
                CountDownLatch taskCounts = new CountDownLatch(list.size());
                TaskInfo.purchaseIsDone=false;
                new Thread(new Consumer(openPositionDao,taskCounts)).start();
                for(int i = 0;i < list.size(); i++) {
                    Map map= (Map) list.get(i);
                    String dt=map.get("SGCPCF").toString();
                    List listSum=new ArrayList();
                    String [] dtcpcf=dt.split("\\,");
                    for(int j=0;j<dtcpcf.length;j++){
                        String [] cf =dtcpcf[j].split("\\|");
                        if(cf.length>=4&&null!=cf){
                            JSONObject js = new JSONObject();
                            js.put("fund_code",cf[0]);//产品代码
                            js.put("fund_company",cf[1]);//基金公司代码
                            js.put("business_flag",cf[2]);//业务标志
                            js.put("balance",cf[3]);//申购金额
                            listSum.add(js);
                        }
                    }
                    JSONObject json = new JSONObject();
                    json.put("cust_type", map.get("KHLX"));//客户类型
                    json.put("cust_no",map.get("KHH"));//客户号
                    json.put("combine_stock_no",map.get("ZHBH"));//组合编号
                    json.put("cust_account",map.get("ZJZH"));//资金账户
                    json.put("combine_data",listSum);//组合委托成份清单
                    json.put("op_station",opStation);//站点地址
                    json.put("I_CZLSH",map.get("CZLSH"));//操作流水号
                    rs.lpush("pro_purchase",json.toJSONString());
                    count ++;
                }
                //等待所有线程执行完成
                taskCounts.await();
            }
            //记录数据提取完成日志
            taskLog.updPurchaseLog(openPositionDao,"4","2",System.currentTimeMillis() - startTime,
                    new Long(0),"4",list.size(),count,list.size() - count,"定时4提取数据完成",opStation);
            TaskInfo.purchaseIsDone = true;
            log.info(".................end.............");
            //记录数据完成日志
            String number=rs.getKey("purchase_number")==null?"0":rs.getKey("purchase_number");
            count=Integer.parseInt(number);
            taskLog.updPurchaseLog(openPositionDao,"4","2",new Long(0),
                    System.currentTimeMillis() - startTime,"1",list.size(),count,list.size() - count,"定时4任务完成",opStation);
            rs.del("purchase_number");
            if( TaskInfo.purchaseIsDone&&TaskInfo.autoapplyIsDone){
                taskLog.updMasterLog(openPositionDao,"2","1","定时任务结束");
                rs.set("taskIsRun","1");
            }
        } catch(Exception e) {
          log.error("轮询异常");
          e.printStackTrace();
        }
    }
}
