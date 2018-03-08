package com.apex.bss.cjsc.taskn.transferfund;

import com.apex.bss.cjsc.dao.OpenPositionDao;
import com.apex.bss.cjsc.redis.RedisUtil;
import com.apex.bss.cjsc.taskn.TimeoutTaskUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Saphurot on 2017/5/5.
 */
public class TransferFund_SD extends Thread {
    protected static final Logger log = LoggerFactory.getLogger(TransferFund_SD.class);
    private OpenPositionDao openPositionDao;
    private String opStation;
    private RedisUtil rs;

    public TransferFund_SD(OpenPositionDao openPositionDao, String opStation) {
        this.openPositionDao = openPositionDao;
        this.opStation = opStation;

        this.rs = new RedisUtil();
    }


    public void run() {
        try {
            Map<String, Object> map_date = new HashMap();
            map_date.put("I_RQ", "");
            map_date.put("O_ISVALID", "");
            openPositionDao.judgmentDay(map_date);
            int date = Integer.parseInt(map_date.get("O_ISVALID").toString());
            if (map_date != null && date == 1) {
                this.execQry();
            } else {
                log.info("今天不是交易日");
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("定时任务【余额转货基】异常...");
        }
    }


    private void execQry() {
        try {
            Map map_YE = TimeoutTaskUtils.execute(new QueryAccout(), 10);
            Map map = new HashMap();
            map.put("O_CODE", "");
            map.put("O_NOTE", "");
            map.put("I_FAID", "");
            if (null != map_YE && Integer.parseInt(map_YE.get("errNo").toString()) == 0) {
                map.put("I_ZT", 1);
                map.put("I_CWDM", "");
                map.put("I_CWXX", "");
            } else if (null == map_YE) {
                map.put("I_ZT", 1);
                map.put("I_CWDM", "");
                map.put("I_CWXX", "");
            } else {
                map.put("I_ZT", 0);
                map.put("I_CWDM", map_YE.get("errNo"));
                map.put("I_CWXX", map_YE.get("errMsg"));
            }
            map.put("I_OP_STATION", opStation);
            map.put("I_DDFS", 1);
            log.debug("记录余额转货基日志入参:"+map.toString());
            openPositionDao.fundlog(map);//记录余额转货基日志
            log.debug("记录余额转货基日志出参:"+map.toString());
            log.info(".................余额转货基end.............");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("定时任务【余额转货基】异常...");
        }
    }
}
