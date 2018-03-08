package com.apex.bss.cjsc.timer;

import com.apex.bss.cjsc.base.ConfigUtil;
import com.apex.bss.cjsc.redis.RedisUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by jinsh on 2017/1/24.
 */
public class TimerTaskThreadPool {
    protected static final Logger log = Logger.getLogger(TimerTaskThreadPool.class);
    private static ThreadPoolExecutor threadPool;

    private int corePoolSize;
    private int maximumPoolSize;
    private int keepAliveTime;
    private int queueNumber;


    private TimerTaskThreadPool(){}

    /**
     * 初始化定时任务线程池
     */
    private void initThreadPool(){
        threadPool = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(queueNumber),
                new ThreadPoolExecutor.CallerRunsPolicy());

    }

    public static ThreadPoolExecutor getThreadPool(){
        return threadPool;
    }



    private static boolean checkTime(String star,String end){
        boolean bool = false;
        SimpleDateFormat localTime=new SimpleDateFormat("HH:mm:ss");
        try{
            Date sdate = localTime.parse(star);
            Date edate=localTime.parse(end);
            Date ndate = localTime.parse((localTime.format(new Date()).toString()));
            //System.out.println(ndate.getTime()+"##"+sdate.getTime()+"##"+edate.getTime());
            if(ndate.getTime()>=sdate.getTime()&& ndate.getTime()<=edate.getTime()){
                bool = true;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return bool;
    }

    public static void main(String args[]){
        boolean a = checkTime("10:30:00", "11:30:00");
        System.out.println(a);
    }

    public void setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    public void setKeepAliveTime(int keepAliveTime) {
        this.keepAliveTime = keepAliveTime;
    }

    public void setMaximumPoolSize(int maximumPoolSize) {
        this.maximumPoolSize = maximumPoolSize;
    }

    public void setQueueNumber(int queueNumber) {
        this.queueNumber = queueNumber;
    }
}
