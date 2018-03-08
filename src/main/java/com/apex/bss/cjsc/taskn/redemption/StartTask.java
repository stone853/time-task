package com.apex.bss.cjsc.taskn.redemption;

import com.apex.bss.cjsc.dao.OpenPositionDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by jinsh on 2017/2/4.
 */
public class StartTask {
    protected static final Logger log= LoggerFactory.getLogger(StartTask.class);
    private OpenPositionDao openPositionDao;
    private void start(){
        try {
                new ThreadGetTask(openPositionDao).start();
        }catch (Exception e){
            e.printStackTrace();
            log.error("定时任务启动异常");
        }

    }


    public void setOpenPositionDao(OpenPositionDao openPositionDao) {
        this.openPositionDao = openPositionDao;
    }
}
