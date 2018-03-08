package com.apex.bss.cjsc.redis;

/**
 * Created by jinsh on 2017/2/13.
 */

import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;




import com.apex.bss.cjsc.base.ConfigUtil;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Jinshi on 2016/3/17.
 */
public class RedisUtilTest {
    protected static final Logger log = Logger.getLogger(RedisUtilTest.class);
    protected static JedisSentinelPool redisSentinelJedisPool;
    static {
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
            log.error("获取reids连接失败" +redisSentinelJedisPool.getCurrentHostMaster() );
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

    public Long setnx(String key,String v){
        Jedis jedis = null;
        try{
            jedis = getJedis();
            return jedis.setnx(key,v);

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
            log.error("setnx 插入报错");
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


}
