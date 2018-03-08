package com.apex.bss.cjsc.asclient.action.bus;

import com.alibaba.fastjson.JSONObject;
import com.apex.bss.cjsc.asclient.service.BusService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Lenovo on 2017/1/13.
 * 撤销操作
 */
public class Bus10507 extends Bus{
    protected static final Logger log = Logger.getLogger(Bus10507.class);
    @Autowired
    BusService busService;
    @Override
    public JSONObject invoke(JSONObject req_data){
        String op_station=req_data.getString("op_station")+"_DD00";
        req_data.put("op_station",op_station);
        req_data.put("entrust_way","8");
        req_data.put("ofchannel_type","b");
        return busService.cancel(req_data);
    }
}
