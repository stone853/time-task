package com.apex.bss.cjsc.taskn.transferfund;

import com.apex.bss.cjsc.asclient.service.IfsService;
import com.apex.bss.cjsc.dao.OpenPositionDao;
import com.apex.bss.cjsc.taskn.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Created by Saphurot on 2017/5/5.
 */
public class QueryAccout implements Callable<Map> {
    protected static final Logger log = LoggerFactory.getLogger(QueryAccout.class);
    private OpenPositionDao openPositionDao;

    @Override
    public Map call() throws Exception {
        String opStation = CommonUtils.getOpStation(openPositionDao);
        IfsService ifs_Service = new IfsService();
        Map map_YE = ifs_Service.queryAccount(opStation, "", "b", "4");
       return map_YE;
    }
}
