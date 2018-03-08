package com.apex.bss.cjsc.test.schedule;

import com.apex.bss.cjsc.redis.RedisUtil;
import redis.clients.jedis.Jedis;

import java.util.Random;
import java.util.UUID;

/**
 * Created by Jinshi on 2017/1/6.
 */
public class TaskProducer implements Runnable{
    RedisUtil rs;

    public TaskProducer(){
        rs = new RedisUtil();
    }

    public void run() {
        Random random = new Random();
        while(true){
            try{
                Thread.sleep(random.nextInt(600) + 600);
                // 模拟生成一个任务
                UUID taskid = UUID.randomUUID();
                //将任务插入任务队列：task-queue
                rs.lpush("task-queue", taskid.toString());
                System.out.println("插入了一个新的任务： " + taskid);

            }catch(Exception e){
                e.printStackTrace();
            }
        }

    }
}
