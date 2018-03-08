package com.apex.bss.cjsc.taskn.transferfund;

import com.alibaba.fastjson.JSONObject;
import com.apex.bss.cjsc.base.CommonUtil;
import com.apex.bss.cjsc.base.ConfigUtil;
import com.apex.bss.cjsc.dao.OpenPositionDao;
import com.apex.bss.cjsc.redis.RedisUtil;
import com.apex.bss.cjsc.taskn.CommonUtils;
import com.apex.bss.cjsc.taskn.TaskInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;


/**
 * Created by wzy on 2017/4/6.
 * 定时查询数据库中的任务，将其插入redis队列中
 */
public class ThreadGetTask extends Thread{
    protected static final Logger log= LoggerFactory.getLogger(ThreadGetTask.class);
    private OpenPositionDao openPositionDao;
    private RedisUtil rs;

    //休眠时间
    private int sleepTime;
    /**
     * 轮询开始结束时间
     */
    private String startTime;
    private String endTime;

    public ThreadGetTask(OpenPositionDao openPositionDao){
        this.openPositionDao = openPositionDao;
        this.startTime = ConfigUtil.getString("taskTime.startTask_5");
        this.endTime = ConfigUtil.getString("taskTime.endTask_5");
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
                if(isQry && !TaskInfo.transIsDone && TaskInfo.transRetryTimes < 4) {
                    Long getTransTaskLock = rs.setnx("getTransTaskLock", "1");
                    if(getTransTaskLock != null && getTransTaskLock == 1L){
                        rs.set("transTaskIsRun","0");
                        rs.del("pro_trans");
                        this.execQry(opStation);
                        sleepTime = 1000 * 60 * 10;
                        rs.del("pro_trans_temp");
                    }else{
                        sleepTime = 1000 * 60 * 10;
                        String transTaskIsRun = rs.getKey("transTaskIsRun");
                        if("0".equals(transTaskIsRun) || transTaskIsRun == null){
                            log.info("定时任务在其他服务器上执行");
                        }else if("1".equals(transTaskIsRun)){
                            log.info("今天已执行完成!");
                            TaskInfo.redemIsDone = true;
                        }
                    }
                }else{
                    String transTaskIsRun = rs.getKey("transTaskIsRun");
                    if(TaskInfo.transRetryTimes >= 4){
                        log.info("失败次数超过3次,请明天再处理");
                        continue;
                    }
                    if("2".equals(transTaskIsRun) || transTaskIsRun == null){
                        log.info("非轮询时间范围内!");
                    }else if("1".equals(transTaskIsRun) || TaskInfo.redemIsDone ){
                        log.info("今天已执行完成!");
                    }else{
                        log.info("定时任务在其他服务器上执行");
                    }
                    if(CommonUtil.checkTime("23:00:00","23:30:00")) {
                        //重置，以便明天重新开始
                            reset();
                    }
                }
                Thread.sleep(sleepTime);
            }
        }catch (Exception e){
            e.printStackTrace();
            log.error("定时任务【余额转货基】异常...");
        }
    }


    /**
     * 开始执行轮询
     */
    private void execQry(String opStation){
        try {
            TaskInfo.autoOrSD = "1";
            log.info("余额转货基查询数据库开始... step 1");
            Map map_query=new HashMap();
            map_query.put("O_CODE","");
            map_query.put("O_NOTE","");
            map_query.put("O_RESULT",new ArrayList<Map<String, Object>>());
            openPositionDao.queryAccount(map_query);
            if(null == map_query || map_query.size() ==0) {
                return;
            }

            log.info("余额转货基开始处理数据... step 2");
            int count  = 0;
            List list= (List) map_query.get("O_RESULT");
            if( list != null && list.size() > 0) {
                CountDownLatch taskCounts = new CountDownLatch(list.size());
                for (int i = 0; i < list.size(); i++) {
                    Map map = (Map) list.get(i);
                    try {
                        JSONObject json = new JSONObject();
                        json.put("opStation", opStation);//站点地址
                        json.put("COMBINE_ID", map.get("COMBINE_ID"));//产品号
                        json.put("COMBINE_CODE", map.get("COMBINE_CODE"));//产品池编号
                        rs.lpush("pro_trans", json.toJSONString());
                    } catch (Exception e) {
                        log.error("插入redis失败:" + map.toString());
                        e.printStackTrace();
                    }
                    count++;
                }
                new Thread(new Consumer(openPositionDao, taskCounts,1)).start();
                taskCounts.await();
            }
            TaskInfo.transIsDone = true;
            rs.set("transTaskIsRun","1");
            log.info(".................余额转货基end.............");
        } catch(Exception e) {
          log.error("轮询异常");
          e.printStackTrace();
          TaskInfo.transRetryTimes ++;
        }
    }

    /**
     * 重置
     * @return
     */
    private boolean reset() {
        try {
            TaskInfo.transIsDone =false;
            TaskInfo.transRetryTimes = 0;
            sleepTime = 1000 * 60 * 2;
            rs.set("transTaskIsRun","2");
            return true;
        } catch (Exception e) {
            log.error("重置异常");
            e.printStackTrace();
            return false;
        }
    }
}
