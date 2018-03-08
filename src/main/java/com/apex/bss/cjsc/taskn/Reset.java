package com.apex.bss.cjsc.taskn;

import com.apex.bss.cjsc.redis.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by cjy on 2017/4/28.
 * 定时任务手动重置
 */

public class Reset {
    protected static final Logger log= LoggerFactory.getLogger(Reset.class);
    RedisUtil rs = new RedisUtil();
    public boolean resetRedem(){
        try {
            TaskInfo.redemIsDone =false;
            TaskInfo.redemRetryTimes = 0;
            TaskInfo.fundinIsDone =false;
            TaskInfo.fundinRetryTimes = 0;
            TaskInfo.autoapplyIsDone =false;
            TaskInfo.autoapplyRetryTimes = 0;
            TaskInfo.purchaseIsDone =false;
            TaskInfo.purchaseRetryTimes = 0;
            rs.del("getTaskLock");
            rs.del("masterID");
            return true;
        } catch (Exception e) {
            log.error("重置异常");
            e.printStackTrace();
            return false;
        }
    }
    public boolean resetTrans(){
        try {
            TaskInfo.transIsDone =false;
            TaskInfo.transRetryTimes = 0;
            rs.del("getTransTaskLock");
            return true;
        } catch (Exception e) {
            log.error("重置异常");
            e.printStackTrace();
            return false;
        }
    }
}
