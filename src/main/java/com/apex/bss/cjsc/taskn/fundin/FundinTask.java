package com.apex.bss.cjsc.taskn.fundin;

import com.alibaba.fastjson.JSONObject;
import com.apex.bss.cjsc.base.CommonUtil;
import com.apex.bss.cjsc.base.ConfigUtil;
import com.apex.bss.cjsc.dao.OpenPositionDao;
import com.apex.bss.cjsc.redis.RedisUtil;
import com.apex.bss.cjsc.taskn.CommonUtils;
import com.apex.bss.cjsc.taskn.TaskInfo;
import com.apex.bss.cjsc.taskn.TaskLog;
import com.apex.bss.cjsc.taskn.autoapply.AutoapplyTask;
import com.apex.bss.cjsc.taskn.purchase.PurchaseTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;


/**
 * Created by jinsh on 2017/3/14.
 * 定时查询数据库中的任务，将其插入redis队列中
 */
public class FundinTask extends Thread{
    protected static final Logger log= LoggerFactory.getLogger(FundinTask.class);
    private OpenPositionDao openPositionDao;
    private RedisUtil rs;

    //休眠时间
    private int sleepTime;
    /**
     * 轮询开始结束时间
     */
    private String startTime;
    private String endTime;

    public FundinTask(OpenPositionDao openPositionDao){
        this.openPositionDao = openPositionDao;
        this.startTime = ConfigUtil.getString("taskTime.startTask_1");
        this.endTime = ConfigUtil.getString("taskTime.endTask_1");
        this.sleepTime = ConfigUtil.getInt("taskTime.sleepTime");
        this.rs = new RedisUtil();
    }

    /**
     * 开始任务
     */
    public void run() {
        try{
            boolean isQry = false;
            String opStation = CommonUtils.getOpStation(openPositionDao);
            //开始轮询
            for(;;) {
                isQry = CommonUtil.checkTime(this.startTime,this.endTime);
                if(isQry && !TaskInfo.fundinIsDone && TaskInfo.fundinRetryTimes < 4) {
                    this.execQry(opStation);
                }else if(TaskInfo.fundinIsDone) { //已完成
                    break;
                }else if(TaskInfo.fundinRetryTimes >= 4){
                    log.info("失败次数超过3次,请明天再处理");
                    break;
                }
                else if(!isQry){  //非交易时间
                    log.info("非轮询时间范围内!");
                }else{
                    log.info("重试次数超过3次");
                }
                Thread.sleep(sleepTime);
            }
        }catch (Exception e){
            e.printStackTrace();
            log.error("定时任务【2】异常...");
        }
    }



    /**
     * 开始执行轮询
     */
    private void execQry(String opStation){
        try {
            log.info("查询数据库开始... step 1");
            /**记录日志**/
            rs.setex("fundinisrun",60*60,"fundinisrun");
            Long startTime = System.currentTimeMillis();
            TaskLog taskLog = TaskLog.getTaskLog();
            taskLog.insFundinLog(openPositionDao,"2","1",new Long(0),new Long(0),"3",0,0,0,"定时2开始提取数据",opStation);
            /**end**/
            List list=openPositionDao.fundIn(1);
            log.debug("自动定投资金划入:"+list.toString());
            log.info("开始处理数据... step 2");
            int count  = 0;
            if(null != list || list.size()>0) {
                CountDownLatch taskCounts = new CountDownLatch(list.size());
                new Thread(new Consumer(openPositionDao,taskCounts)).start();
                for(int i = 0;i < list.size(); i++) {
                    Map map= (Map) list.get(i);
                    JSONObject json = new JSONObject();
                    json.put("client_id",map.get("KHH"));//客户号
                    json.put("combine_stock_no",map.get("ZHBH"));//组合编号
                    json.put("occur_balance",map.get("HBJE"));//划拨金额
                    json.put("fund_account",map.get("ZJZH"));//资金账户
                    json.put("op_station",opStation);//组合委托成份清单
                    rs.lpush("pro_fundin",json.toJSONString());
                    count++;
                }
                //等待所有线程执行完成
                taskCounts.await();
            }
            //记录数据提取完成日志
            taskLog.updFundinLog(openPositionDao,"2","2",System.currentTimeMillis() - startTime,
                    new Long(0),"4",list.size(),count,list.size() - count,"定时2提取数据完成",opStation);

            TaskInfo.fundinIsDone = true;
            log.info(".................end.............");
            //记录数据完成日志
            String number=rs.getKey("fundin_number")==null?"0":rs.getKey("fundin_number");
            count=Integer.parseInt(number);
            taskLog.updFundinLog(openPositionDao,"2","2",new Long(0),
                    System.currentTimeMillis() - startTime,"1",list.size(),count,list.size() - count,"定时2任务完成",opStation);
            rs.del("fundin_number");
            rs.del("fundinisrun");
            //开启资金划转线程
            new AutoapplyTask(openPositionDao).start();
            new PurchaseTask(openPositionDao).start();
        } catch(Exception e) {
            log.error("轮询异常");
            e.printStackTrace();
            TaskInfo.fundinRetryTimes ++;
        }
    }


}
