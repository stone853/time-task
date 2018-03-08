package com.apex.bss.cjsc.asclient.action.bus;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by jinsh on 2017/1/12.
 */
public abstract class Bus {
    public abstract JSONObject invoke(JSONObject req_data);
}
