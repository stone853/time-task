package com.apex.bss.cjsc.taskn.redemption;

import com.alibaba.fastjson.JSONObject;
import com.apex.bss.cjsc.base.CommonUtil;
import com.apex.bss.cjsc.base.ConfigUtil;
import com.apex.bss.cjsc.dao.OpenPositionDao;
import com.apex.bss.cjsc.redis.RedisUtil;
import com.apex.bss.cjsc.taskn.CommonUtils;
import com.apex.bss.cjsc.taskn.TaskInfo;
import com.apex.bss.cjsc.taskn.TaskLog;
import com.apex.bss.cjsc.taskn.fundin.FundinTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;


/**
 * Created by jinsh on 2017/3/14.
 * 定时查询数据库中的任务，将其插入redis队列中
 */
public class ThreadGetTask extends Thread {
    protected static final Logger log = LoggerFactory.getLogger(ThreadGetTask.class);
    private OpenPositionDao openPositionDao;
    private RedisUtil rs;

    //休眠时间
    private int sleepTime;
    /**
     * 轮询开始结束时间
     */
    private String startTime;
    private String endTime;

    public ThreadGetTask(OpenPositionDao openPositionDao) {
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
        try {
            boolean isQry = false;
            boolean isTradeDay = false;
            String opStation = CommonUtils.getOpStation(openPositionDao);
            //开始轮询
            Integer date = null;
            //开始轮询
            for (; ; ) {
                if (date == null) {
                    //判断当天是否为交易日
                    isTradeDay = CommonUtils.isTradeDay(openPositionDao);
                    date = 1;
                }
                System.out.println(isTradeDay);
                if (isTradeDay) {
                    //当天为交易日
                    isQry = CommonUtil.checkTime(this.startTime, this.endTime);
                    if (isQry && !TaskInfo.redemIsDone && TaskInfo.redemRetryTimes < 4 && date == 1) {
                        Long lv = rs.setnx("getTaskLock", "1");
                        if (null != lv && lv == 1L) {
                            rs.set("taskIsRun", "0");
                            this.execQry(opStation);
                            sleepTime = 1000 * 60 * 10;
                        } else {
                            sleepTime = 1000 * 60 * 10;
                            String taskIsRun = rs.getKey("taskIsRun");
                            if ("0".equals(taskIsRun) || taskIsRun == null) {
                                log.info("定时任务在其他服务器上执行");
                            } else if ("1".equals(taskIsRun)) {
                                log.info("今天已执行完成!");
                                TaskInfo.redemIsDone = true;

                            }
                        }
                    } else {
                        String taskIsRun = rs.getKey("taskIsRun");
                        if (TaskInfo.redemRetryTimes >= 4) {
                            log.info("失败次数超过3次,请明天再处理");
                            continue;
                        }
                        if ("2".equals(taskIsRun) || taskIsRun == null) {
                            log.info("非轮询时间范围内!");
                        } else if ("1".equals(taskIsRun) || TaskInfo.redemIsDone) {
                            log.info("今天已执行完成!");
                        } else {
                            log.info("定时任务在其他服务器上执行");
                        }
                    }
                } else {
                    //当天为非交易日
                    log.info("今天是非交易日");
                    sleepTime  = 1000 * 60;
                }
                if (CommonUtil.checkTime("23:00:00", "23:30:00")) {
                    //重置，以便明天重新开始
                    reset();
                }
                if (CommonUtil.checkTime("00:00:00", "00:10:00")) {
                    //重置，以便查询当天是否为交易日
                    date = null;
                }
                Thread.sleep(sleepTime);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("定时任务【1】异常...");
        }
    }


    /**
     * 开始执行轮询
     */
    private void execQry(String opStation) {
        try {
            log.info("查询数据库开始... step 1");
            rs.setex("redemisrun", 60 * 60, "redemisrun");
            /**记录日志**/
            Long startTime = System.currentTimeMillis();
            TaskLog taskLog = TaskLog.getTaskLog();
            taskLog.insMasterLog(openPositionDao, "1", "2", "定时任务开始");
            taskLog.insRedemLog(openPositionDao, "1", "1", new Long(0), new Long(0), "3", 0, 0, 0, "定时1开始提取数据", opStation);
            /**end**/
            log.info("开始处理数据... step 2");
            List list = openPositionDao.redemptionMoney();
            log.debug("划拨组合赎回完成金额:" + list.toString());
            int count = 0;
            if (null != list && list.size() > 0) {
                CountDownLatch taskCounts = new CountDownLatch(list.size());
                new Thread(new Consumer(openPositionDao, taskCounts)).start();
                for (int i = 0; i < list.size(); i++) {
                    Map map = (Map) list.get(i);
                    try {
                        JSONObject json = new JSONObject();
                        json.put("client_id", map.get("KHH"));//客户号
                        json.put("combine_stock_no", map.get("ZHBH"));//组合编号
                        json.put("fund_account", map.get("ZJZH"));//资金账户
                        json.put("occur_balance", map.get("SHJE"));//划拨金额
                        json.put("I_CZLSH", map.get("CZLSH"));//操作流水号
                        json.put("op_station", opStation);//站点地址
                        rs.lpush("pro_redem", json.toJSONString());
                    } catch (Exception e) {
                        log.error("插入redis失败:" + map.toString());
                        e.printStackTrace();
                    }
                    count++;
                }
                //等待所有线程执行完成
                taskCounts.await();
            }
            //记录数据提取完成日志
            taskLog.updRedemLog(openPositionDao, "1", "2", System.currentTimeMillis() - startTime,
                    new Long(0), "4", list.size(), count, list.size() - count, "定时1提取数据完成", opStation);
            TaskInfo.redemIsDone = true;
            log.info(".................end.............");
            //记录数据完成日志
            String number = rs.getKey("redem_number") == null ? "0" : rs.getKey("redem_number");
            count = Integer.parseInt(number);
            taskLog.updRedemLog(openPositionDao, "1", "2", new Long(0),
                    System.currentTimeMillis() - startTime, "1", list.size(), count, list.size() - count, "定时1任务完成", opStation);
            rs.del("redem_number");
            rs.del("redemisrun");
            //开启资金划转线程
            new FundinTask(openPositionDao).start();
        } catch (Exception e) {
            log.error("轮询异常");
            e.printStackTrace();
            TaskInfo.redemRetryTimes++;
        }
    }

    /**
     * 重置
     *
     * @return
     */
    private boolean reset() {
        try {
            TaskInfo.redemIsDone = false;
            TaskInfo.redemRetryTimes = 0;
            TaskInfo.fundinIsDone = false;
            TaskInfo.fundinRetryTimes = 0;
            TaskInfo.autoapplyIsDone = false;
            TaskInfo.autoapplyRetryTimes = 0;
            TaskInfo.purchaseIsDone = false;
            TaskInfo.purchaseRetryTimes = 0;
            sleepTime = 1000 * 60 * 2;
            rs.set("taskIsRun", "2");//0表示正在其他服务器上执行,1表示已经执行完成,2表示重置
            rs.del("getTaskLock");
            rs.del("masterID");
            return true;
        } catch (Exception e) {
            log.error("重置异常");
            e.printStackTrace();
            return false;
        }
    }
}
