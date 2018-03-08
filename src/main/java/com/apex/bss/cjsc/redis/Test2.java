package com.apex.bss.cjsc.redis;



//import redis.clients.jedis.ShardedJedisSentinelPool;


import com.alibaba.fastjson.JSONObject;
import com.apex.bss.cjsc.base.ConfigUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Jinshi on 2016/3/7.
 */
public class Test2 {

    public static void main(String args[]){
        List list = new ArrayList();
        Map map = new HashMap();

        map.put("a","1");
        map.put("b","2");
        list.add(map);
        list.add(map);
        System.out.println(list.toString());


    }
}
