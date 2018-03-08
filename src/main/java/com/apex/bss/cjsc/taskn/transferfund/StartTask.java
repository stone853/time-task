package com.apex.bss.cjsc.taskn.transferfund;

import com.apex.bss.cjsc.dao.OpenPositionDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by wzy on 2017/4/6.
 */
public class StartTask {
    protected static final Logger log= LoggerFactory.getLogger(StartTask.class);
    private OpenPositionDao openPositionDao;
    private void start(){
        try {
            Map<String,Object> map_date = new HashMap();
            map_date.put("I_RQ","");
            map_date.put("O_ISVALID","");
            openPositionDao.judgmentDay(map_date);
            int date = Integer.parseInt(map_date.get("O_ISVALID").toString());
            if(map_date != null && date == 1){
                new TransferFund(openPositionDao).start();
            }else{
                log.info("今天不是交易日");
            }
        }catch (Exception e){
            e.printStackTrace();
            log.error("余额转货基定时任务启动异常");
        }


    }


    public void setOpenPositionDao(OpenPositionDao openPositionDao) {
        this.openPositionDao = openPositionDao;
    }
}
