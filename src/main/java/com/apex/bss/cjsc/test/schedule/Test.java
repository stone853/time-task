package com.apex.bss.cjsc.test.schedule;

import com.apex.bss.cjsc.dao.OpenPositionDao;
import redis.clients.jedis.Jedis;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jinshi on 2017/1/9.
 */
public class Test {
    private OpenPositionDao openPositionDao;

    public void start(){
        Map param = new HashMap<>();
        param.put("I_RQ","20170506");
        param.put("O_ISVALID","");
        System.out.println("11111111111111111111111111111111111");
        openPositionDao.judgmentDay(param);
        System.out.println(param);
    }


    private static String id = "";
    public static void main(String args[]){
        System.out.println(new Test().getClass().getClassLoader());
    }

    public void setOpenPositionDao(OpenPositionDao openPositionDao) {
        this.openPositionDao = openPositionDao;
    }

    public OpenPositionDao getOpenPositionDao() {
        return openPositionDao;
    }

    private static void setID(String id){
        id = "9";
    }
}
