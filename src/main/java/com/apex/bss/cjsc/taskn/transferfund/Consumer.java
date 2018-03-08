package com.apex.bss.cjsc.taskn.transferfund;

import com.apex.bss.cjsc.dao.OpenPositionDao;
import com.apex.bss.cjsc.redis.RedisUtil;
import com.apex.bss.cjsc.taskn.TaskInfo;
import com.apex.bss.cjsc.timer.TimerTaskThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;


/**
 * Created by wzy on 2017/4/6.
 *
 */
public class Consumer implements Runnable{
    protected static final Logger log= LoggerFactory.getLogger(Consumer.class);

    private OpenPositionDao openPositionDao;
    private RedisUtil rs;
    private CountDownLatch taskCounts;
    private ThreadPoolExecutor threadPoolExecutor;
    private Integer I_DDFS;

    public Consumer(OpenPositionDao openPositionDao, CountDownLatch taskCounts,Integer I_DDFS){
        this.I_DDFS =I_DDFS;
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
                String task = rs.brpoplpush("pro_trans","pro_trans_temp",10);
                if(task != null && !"".equals(task)){
                    threadPoolExecutor.execute(new SendApplyInfo(task,openPositionDao,taskCounts,I_DDFS));
                }
                if(TaskInfo.autoOrSD.equals("1")){
                    if(TaskInfo.transIsDone ){
                        break;
                    }
                }
                if(TaskInfo.autoOrSD.equals("0")){
                    if(TaskInfo.transIsDone_SD){
                        break;
                    }
                }


            }
        } catch (Exception e){
            e.printStackTrace();
            log.error("余额转货基定时模块异常");
        }
    }
}
