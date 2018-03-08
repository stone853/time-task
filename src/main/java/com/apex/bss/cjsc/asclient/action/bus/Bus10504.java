package com.apex.bss.cjsc.asclient.action.bus;

import com.alibaba.fastjson.JSONObject;
import com.apex.bss.cjsc.asclient.service.BusService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Lenovo on 2017/1/12.
 * 组合基金部分赎回
 */
public class Bus10504 extends Bus{
    protected static final Logger log = Logger.getLogger(Bus10504.class);
    @Autowired
    BusService busService;
    @Override
    public JSONObject invoke(JSONObject req_data){
        String op_station=req_data.getString("op_station")+"_DD00";
        req_data.put("op_station",op_station);
        req_data.put("type","3");
        req_data.put("entrust_way","8");
        req_data.put("ofchannel_type","b");
        return busService.redeemof(req_data);
    }
}
