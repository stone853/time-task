package com.apex.bss.cjsc.asclient.action.bear;

import com.alibaba.fastjson.JSONObject;
import com.apex.bss.cjsc.asclient.service.BearService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Saphurot on 2017/5/3.
 * 牛熊轮动基金赎回
 */
public class Bear10604 extends Bear{
    protected static final Logger log = Logger.getLogger(Bear10604.class);
    @Autowired
    BearService bearService;
    @Override
    public JSONObject invoke(JSONObject req_data) {
        String op_station=req_data.getString("op_station")+"_DD00";
        req_data.put("op_station",op_station);
        return bearService.redeemBear(req_data);
    }
}
