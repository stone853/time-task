package com.apex.bss.cjsc.asclient.action.bear;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by Saphurot on 2017/5/3.
 */
public abstract class Bear {
    public abstract JSONObject invoke(JSONObject req_data);
}
