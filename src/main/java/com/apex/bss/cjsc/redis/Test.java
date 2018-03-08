package com.apex.bss.cjsc.redis;



//import redis.clients.jedis.ShardedJedisSentinelPool;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.apex.bss.cjsc.base.ConfigUtil;
import org.springframework.core.env.SystemEnvironmentPropertySource;

import java.util.List;

/**
 * Created by Jinshi on 2016/3/7.
 */
public class Test {

    public static void main(String args[]){
        String a = "[{\"fund_code\":\"8991C5\",\"fund_company\":\"89\",\"business_flag\":\"22\",\"balance\":\"1975.00\"},{\"fund_code\":\"899928\",\"fund_company\":\"89\",\"business_flag\":\"22\",\"balance\":\"83.00\"},{\"fund_code\":\"899664\",\"fund_company\":\"89\",\"business_flag\":\"22\",\"balance\":\"1162.00\"},{\"fund_code\":\"8991A9\",\"fund_company\":\"89\",\"business_flag\":\"22\",\"balance\":\"1085.00\"},{\"fund_code\":\"8991C0\",\"fund_company\":\"89\",\"business_flag\":\"22\",\"balance\":\"3415.00\"},{\"fund_code\":\"8991C2\",\"fund_company\":\"89\",\"business_flag\":\"22\",\"balance\":\"2280.00\"}]";
        JSONArray list = JSON.parseArray(a);
        System.out.println(list.toString());
        for(int i = 0;i < list.size();i++){
            JSONObject json = (JSONObject) list.get(i);
            System.out.println(json.getString("fund_code"));
        }


    }
}
