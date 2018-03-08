package com.apex.bss.cjsc.redis;


import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;

import java.util.List;
import java.util.Set;

/**
 * Created by Jinshi on 2016/3/17.
 */
public class RedisUtil {
    protected static final Logger log = Logger.getLogger(RedisUtil.class);
    protected static JedisSentinelPool redisSentinelJedisPool;
   /* static {
        Set<String> sentinels = new HashSet<String>();
        String hostAndPort = ConfigUtil.getString("sentinel.hostAndPort");
        sentinels.add(hostAndPort);
        //sentinels.add("192.168.1.77:26379");

        String clusterName = ConfigUtil.getString("sentinel.clusterName");
        String password = ConfigUtil.getString("sentinel.pass");

        GenericObjectPoolConfig g = new GenericObjectPoolConfig();
        g.setMaxTotal(300);
        g.setMaxIdle(100);
        g.setMaxWaitMillis(10000);
        g.setTestOnBorrow(true);
        g.setTestOnReturn(true);
        //g.setTestWhileIdle(true);
        //g.setTimeBetweenEvictionRunsMillis(30000);

        redisSentinelJedisPool = new JedisSentinelPool(clusterName,sentinels,g,10000,password);
    }
*/


    public static JedisSentinelPool getRedisSentinelJedisPool() {
        return redisSentinelJedisPool;
    }

    public static void setRedisSentinelJedisPool(JedisSentinelPool redisSentinelJedisPool) {
        log.info("注入SentinelJedisPool");
        RedisUtil.redisSentinelJedisPool = redisSentinelJedisPool;
    }

    private Jedis getJedis(){
        Jedis jedis = null;
        try{
            jedis = redisSentinelJedisPool.getResource();
        }catch (Exception e){
            log.error("获取redis连接失败" +redisSentinelJedisPool.getCurrentHostMaster() );
            e.printStackTrace();
        }
        return jedis;
    }

    public void flushDB(){
        Jedis jedis = null;
        try{
            jedis = getJedis();
            jedis.flushDB();
        }catch (Exception e){
            log.error("清空redis数据报错");
        }finally {
            if(null !=jedis){
                jedis.close();
            }
        }
    }

    public String getKey(String key){
        Jedis jedis = null;
        try{
            jedis = getJedis();
            return jedis.get(key);
        }catch (Exception e){
            log.error("获取key报错");
        }finally {
            if(null !=jedis){
                jedis.close();
            }
        }
        return null;
    }

    public String set(String key,String v){
        Jedis jedis = null;
        try{
            jedis = getJedis();
            return jedis.set(key,v);
        }catch (Exception e){
            log.error("set 插入出错");
        }finally {
            if(null !=jedis){
                jedis.close();
            }
        }
        return null;
    }

    /*public Long setnx(String key,String v){
        Jedis jedis = null;
        try{
            jedis = getJedis();
            return  jedis.setnx(key,v);

        }catch (Exception e){
            log.error("setnx 插入报错");
        }finally {
            if(null !=jedis){
                jedis.close();
            }
        }
        return null;
    }*/

    public Long setnx(String key,String v){
        Jedis jedis = null;
        try{
            jedis = getJedis();
            Long setnx = jedis.setnx(key, v);
            if(setnx != null && setnx ==1L){
                jedis.expire(key,60*60*10);
            }
            return setnx;
        }catch (Exception e){
            log.error("setnx 插入报错");
        }finally {
            if(null !=jedis){
                jedis.close();
            }
        }
        return null;
    }



    public Long expire(String key,int seconds){
        Jedis jedis = null;
        try{
            jedis = getJedis();
            return jedis.expire(key,seconds);
        }catch (Exception e){
            log.error("setnx 插入报错");
        }finally {
            if(null !=jedis){
                jedis.close();
            }
        }
        return null;
    }

    public boolean exists(String key){
        Jedis jedis = null;
        Long lv = null;
        try{
            jedis = getJedis();
            return jedis.exists(key);
        }catch (Exception e){
            log.error("exists报错");
        }finally {
            if(null !=jedis){
                jedis.close();
            }
        }
        return false;
    }



    public void del(String key){
        Jedis jedis = null;
        try{
            jedis = getJedis();
            jedis.del(key);
        }catch (Exception e){
            log.error("del 删除报错");
        }finally {
            if(null !=jedis){
                jedis.close();
            }
        }
    }

    public void lpush(String key,String val){
        Jedis jedis = null;
        try{
            jedis = getJedis();
            jedis.lpush(key,val);
        }catch (Exception e){
            log.error("lpush 报错");
        }finally {
            if(null !=jedis){
                jedis.close();
            }
        }
    }

    public String rpoplpush(String key,String val){
        Jedis jedis = null;
        try{
            jedis = getJedis();
            return jedis.rpoplpush(key,val);
        }catch (Exception e){
            log.error("rpoplpush 报错");
        }finally {
            if(null !=jedis){
                jedis.close();
            }
        }
        return null;
    }

    public String rpop(String key){
        Jedis jedis = null;
        try{
            jedis = getJedis();
            return jedis.rpop(key);
        }catch (Exception e){
            log.error("rpoplpush 报错");
        }finally {
            if(null !=jedis){
                jedis.close();
            }
        }
        return null;
    }


    public List<String> brpop(int timeout, String key){
        Jedis jedis = null;
        try{
            jedis = getJedis();
            return jedis.brpop(timeout,key);
        }catch (Exception e){
            log.error("brpop 报错");
        }finally {
            if(null !=jedis){
                jedis.close();
            }
        }
        return null;
    }

    public String brpoplpush(String source, String destination,int timeout){
        Jedis jedis = null;
        try{
            jedis = getJedis();
            return jedis.brpoplpush(source,destination,timeout);
        }catch (Exception e){
            log.error("brpoplpush 报错");
        }finally {
            if(null !=jedis){
                jedis.close();
            }
        }
        return null;
    }

    public Set keys(String key){
        Jedis jedis = null;
        try{
            jedis = getJedis();
            return jedis.keys(key);
        }catch (Exception e){
            log.error("keys 报错");
        }finally {
            if(null !=jedis){
                jedis.close();
            }
        }
        return null;
    }

    public Long incr(String key){
        Jedis jedis = null;
        try{
            jedis = getJedis();
            return jedis.incr(key);
        }catch (Exception e){
            log.error("keys 报错");
        }finally {
            if(null !=jedis){
                jedis.close();
            }
        }
        return null;
    }

    public String setex(String key,int seconds,String value){
        Jedis jedis = null;
        try{
            jedis = getJedis();
            return jedis.setex(key,seconds,value);
        }catch (Exception e){
            log.error("keys 报错");
        }finally {
            if(null !=jedis){
                jedis.close();
            }
        }
        return null;
    }

    public String setex_long(String key,String value){
        Jedis jedis = null;
        try{
            jedis = getJedis();
            return jedis.setex(key,60*120,value);
        }catch (Exception e){
            log.error("keys 报错");
        }finally {
            if(null !=jedis){
                jedis.close();
            }
        }
        return null;
    }

    public List<String> lrange(String key,long start,long end){
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.lrange(key, start, end);
        }catch (Exception e){
            log.error("lrange 报错");
        }finally {
            if(null!=jedis){
                jedis.close();
            }
        }
        return null;
    }

    //设置日志打印间隔
    public Long setLogTime(String key,String v){
        Jedis jedis = null;
        try{
            jedis = getJedis();
            Long setnx = jedis.setnx(key, v);
            if(setnx == 1l) {
                jedis.expire(key, 60 * 10);
            }
            return setnx;

        }catch (Exception e){
            log.error("setnx 插入报错");
        }finally {
            if(null !=jedis){
                jedis.close();
            }
        }
        return null;
    }
}
