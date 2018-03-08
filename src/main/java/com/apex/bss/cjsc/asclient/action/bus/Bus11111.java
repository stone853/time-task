package com.apex.bss.cjsc.asclient.action.bus;

import com.alibaba.fastjson.JSONObject;
import com.apex.bss.cjsc.dao.OpenPositionDao;
import com.apex.bss.cjsc.dao.TestProcDao;
import com.apex.bss.cjsc.redis.RedisUtil;
import org.apache.log4j.Logger;
import org.omg.CORBA.MARSHAL;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * Created by Jinshi on 2017/1/9.
 * 组合首次申购
 */
public class Bus11111 extends Bus{
    protected static final Logger log = Logger.getLogger(Bus11111.class);
    @Autowired
    private TestProcDao testProcDao;
    @Override
    public JSONObject invoke(JSONObject req_data) {
//        Map<String,Object> map = new HashMap<String,Object>();
//        map.put("i_userid","");
//        map.put("v_cursor",new ArrayList<Map<String,Object>>());
//        long starTime=System.currentTimeMillis();
//        testProcDao.getList(map);
//        List list = (List)map.get("v_cursor");
//        if(null != list){
//            RedisUtil rs = new RedisUtil();
//            rs.del("timedTask1");
//            int count = 0;
//            for(int i=0;i<list.size(); i++){
//                rs.lpush("timedTask1",list.get(i).toString());
//                count++;
//            }
//            System.out.println("总数:"+count);
//        }
//        long endTime=System.currentTimeMillis();
//        long Time=endTime-starTime;
//        System.out.println(Time);
//        RedisUtil redisUtil = new RedisUtil();
//        redisUtil.lpush("test1","111");
//        redisUtil.lpush("test1","222");
//        redisUtil.lpush("test1","333");
        System.out.println("llllllllllllllllllllllll");

        return null;
    }
}
