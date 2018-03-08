package com.apex.bss.cjsc.redis;

import redis.clients.jedis.Jedis;

/**
 * Created by Jinshi on 2016/3/8.
 */
public class RedisSyn {
    String ip;
    int port;
    Jedis jedis;

    public RedisSyn(String ip,int port,String pass){
        this.ip = ip;
        this.port = port;
        this.jedis = new Jedis(ip,port);
        this.jedis.auth(pass);
    }
    /**
     * 使用同步机制，防止获取到脏数据
     * @param key
     * @return
     */
    public synchronized boolean isRun(String key){
        boolean bool = jedis.exists(key);
        if(bool){
            return false;
        }else{
            jedis.set(key,"1");
            return true;
        }
    }


    public synchronized void delKey(String key){
        jedis.del(key);
    }
}
