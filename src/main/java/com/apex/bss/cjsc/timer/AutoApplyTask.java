package com.apex.bss.cjsc.timer;

import org.apache.log4j.Logger;

/**
 * Created by jinsh on 2017/1/27.
 */
public class AutoApplyTask implements  Runnable{
    protected static final Logger log = Logger.getLogger(AutoApplyTask.class);
    private String str;
    public AutoApplyTask(String str){
        this.str = str;
    }
    @Override
    public void run() {
        if("pool-1-thread-4".equals(Thread.currentThread().getName())){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println(Thread.currentThread().getName() + ":" + str);
    }
}
