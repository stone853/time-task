package com.apex.bss.cjsc.asclient.action.bear;

import com.alibaba.fastjson.JSONObject;
import com.apex.bss.cjsc.asclient.service.BearService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Saphurot on 2017/5/24.
 * 牛熊轮动转换操作
 */
public class Bear10605 extends Bear{
    protected static final Logger log = Logger.getLogger(Bear10605.class);
    @Autowired
    BearService bearService;
    @Override
    public JSONObject invoke(JSONObject req_data) {
        String op_station=req_data.getString("op_station")+"_DD00";
        req_data.put("op_station",op_station);
        return bearService.transform(req_data);
    }
}
