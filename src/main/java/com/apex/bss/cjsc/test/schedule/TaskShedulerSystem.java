package com.apex.bss.cjsc.test.schedule;

import com.apex.bss.cjsc.base.ConfigUtil;

/**
 * Created by Jinshi on 2017/1/6.
 */
public class TaskShedulerSystem {
    public static void main(String[] args) throws Exception {

        ConfigUtil.loadConfig();
        // 启动一个生产者线程，模拟任务的产生
        new Thread(new TaskProducer()).start();

        Thread.sleep(15000);

        //启动一个线程者线程，模拟任务的处理
        new Thread(new TaskConsumer()).start();

        //主线程休眠
        Thread.sleep(Long.MAX_VALUE);
    }
}
