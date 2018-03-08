package com.apex.bss.cjsc.invokebus;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

/**
 * Created by jinsh on 2017/2/15.
 */
public class SendReq {
    private static final Logger logger = LoggerFactory.getLogger(SendReq.class);

    //private static final String REQ_URL = ProjectConfig.Instance.getProperty("prod.cj.invokebus.url");

    static {
        logger.info("111111111111111111111111111111111111111111");

    }
    public static HashMap invokeBus(String id){

        String param = "id="+id;
        try{
            return doReturn(0, HttpUtil.sendPost("http://127.0.0.1/ZHLC/processPlatform/synProduct",param));
        }catch(Exception e){
            e.printStackTrace();
            return doReturn(-1,e.getMessage());
        }
    }



    @SuppressWarnings("unchecked")
    private static HashMap doReturn(long code, String msg) {
        HashMap result = new HashMap();
        result.put("RESULT", code);
        result.put("RETCODE", code);
        result.put("SUMMARY", msg);
        result.put("RETNOTE", msg);
        return result;
    }

    public static void main(String args[]){
        System.out.println("123");
    }
}
