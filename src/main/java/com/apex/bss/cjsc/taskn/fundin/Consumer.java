package com.apex.bss.cjsc.taskn.fundin;

import com.apex.bss.cjsc.dao.OpenPositionDao;
import com.apex.bss.cjsc.redis.RedisUtil;
import com.apex.bss.cjsc.taskn.TaskInfo;
import com.apex.bss.cjsc.timer.TimerTaskThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by jinsh on 2017/2/4.
 */
public class Consumer implements Runnable{
    protected static final Logger log= LoggerFactory.getLogger(Consumer.class);

    private OpenPositionDao openPositionDao;
    private RedisUtil rs;
    private CountDownLatch taskCounts;
    private ThreadPoolExecutor threadPoolExecutor;

    public Consumer(OpenPositionDao openPositionDao, CountDownLatch taskCounts){
        this.rs = new RedisUtil();
        this.openPositionDao = openPositionDao;
        this.taskCounts = taskCounts;
        this.threadPoolExecutor = TimerTaskThreadPool.getThreadPool();
    }
    @Override
    public void run() {
        try {
            //开始轮询调用 ifs
            for(;;){
                String task = rs.brpoplpush("pro_fundin","pro_fundin_temp",10);
                if(task != null && !"".equals(task)){
                    threadPoolExecutor.execute(new SendApplyInfo(task,openPositionDao,taskCounts));
                }
                if(TaskInfo.fundinIsDone){
                    break;
                }
            }
        } catch (Exception e){
            e.printStackTrace();
            log.error("redemption 模块异常");
        }
    }
}
