package com.apex.bss.cjsc.taskn.transferfund;

import com.apex.bss.cjsc.base.CommonUtil;
import com.apex.bss.cjsc.base.ConfigUtil;
import com.apex.bss.cjsc.dao.OpenPositionDao;
import com.apex.bss.cjsc.redis.RedisUtil;
import com.apex.bss.cjsc.taskn.CommonUtils;
import com.apex.bss.cjsc.taskn.TaskInfo;
import com.apex.bss.cjsc.taskn.TimeoutTaskUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Saphurot on 2017/5/5.
 */
public class TransferFund extends Thread {
    protected static final Logger log = LoggerFactory.getLogger(TransferFund.class);
    private OpenPositionDao openPositionDao;
    private RedisUtil rs;
    public static Map map_YE;

    //休眠时间
    private int sleepTime;
    /**
     * 轮询开始结束时间
     */
    private String startTime;
    private String endTime;

    public TransferFund(OpenPositionDao openPositionDao) {
        this.openPositionDao = openPositionDao;
        this.startTime = ConfigUtil.getString("taskTime.startTask_5");
        this.endTime = ConfigUtil.getString("taskTime.endTask_5");
        this.sleepTime = ConfigUtil.getInt("taskTime.sleepTime");

        this.rs = new RedisUtil();
    }

    @Override
    public void run() {
        try {
            boolean isQry = false;
            String opStation = CommonUtils.getOpStation(openPositionDao);
            //开始轮询
            for (; ; ) {
                isQry = CommonUtil.checkTime(this.startTime, this.endTime);
                if (isQry && !TaskInfo.transIsDone && TaskInfo.transRetryTimes < 4) {
                    Long getTransTaskLock = rs.setnx("getTransTaskLock", "1");
                    if (getTransTaskLock != null && getTransTaskLock == 1L) {
                        rs.set("transTaskIsRun", "0");
                        this.execQry(opStation);
                        sleepTime = 1000 * 60 * 10;
                    } else {
                        sleepTime = 1000 * 60 * 10;
                        String transTaskIsRun = rs.getKey("transTaskIsRun");
                        if ("0".equals(transTaskIsRun) || transTaskIsRun == null) {
                            log.info("定时任务在其他服务器上执行");
                        } else if ("1".equals(transTaskIsRun)) {
                            log.info("今天已执行完成!");
                            TaskInfo.redemIsDone = true;
                        }
                    }
                } else {
                    String transTaskIsRun = rs.getKey("transTaskIsRun");
                    if (TaskInfo.transRetryTimes >= 4) {
                        log.info("失败次数超过3次,请明天再处理");
                        continue;
                    }
                    if ("2".equals(transTaskIsRun) || transTaskIsRun == null) {
                        log.info("非轮询时间范围内!");
                    } else if ("1".equals(transTaskIsRun) || TaskInfo.redemIsDone) {
                        log.info("今天已执行完成!");
                    } else {
                        log.info("定时任务在其他服务器上执行");
                    }
                    if (CommonUtil.checkTime("23:00:00", "23:30:00")) {
                        //重置，以便明天重新开始
                        reset();
                    }
                }
                Thread.sleep(sleepTime);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("定时任务【余额转货基】异常...");
        }
    }

    private void execQry(String opStation) {
        try {
            Map map_YE = TimeoutTaskUtils.execute(new QueryAccout(), 10);
            Map map = new HashMap();
            map.put("O_CODE", "");
            map.put("O_NOTE", "");
            map.put("I_FAID", "");
            if (null != map_YE && Integer.parseInt(map_YE.get("errNo").toString()) == 0) {
                map.put("I_ZT", 1);
                map.put("I_CWDM", "");
                map.put("I_CWXX", "");
            } else if (null == map_YE) {
                map.put("I_ZT", 1);
                map.put("I_CWDM", "");
                map.put("I_CWXX", "");
            } else {
                map.put("I_ZT", 0);
                map.put("I_CWDM", map_YE.get("errNo"));
                map.put("I_CWXX", map_YE.get("errMsg"));
            }
            map.put("I_OP_STATION", opStation);
            map.put("I_DDFS", 1);
            log.debug("记录余额转货基日志入参:"+map.toString());
            openPositionDao.fundlog(map);//记录余额转货基日志
            log.debug("记录余额转货基日志出参:"+map.toString());
            TaskInfo.transIsDone = true;
            rs.set("transTaskIsRun","1");
            log.info(".................余额转货基end.............");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("轮询异常...");
            TaskInfo.transRetryTimes++;
        }

    }


    private boolean reset() {
        try {
            TaskInfo.transIsDone = false;
            TaskInfo.transRetryTimes = 0;
            sleepTime = 1000 * 60 * 2;
            rs.set("transTaskIsRun", "2");
            return true;
        } catch (Exception e) {
            log.error("重置异常");
            e.printStackTrace();
            return false;
        }
    }
}
