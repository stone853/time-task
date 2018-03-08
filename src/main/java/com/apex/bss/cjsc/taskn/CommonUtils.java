package com.apex.bss.cjsc.taskn;

import com.apex.bss.cjsc.base.ConfigUtil;
import com.apex.bss.cjsc.dao.OpenPositionDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jinsh on 2017/3/21.
 */
public class CommonUtils {
    protected static final Logger log = LoggerFactory.getLogger(CommonUtils.class);

    /**
     * 获取站点信息
     *
     * @return
     */
    public static String getOpStation(OpenPositionDao openPositionDao) {


            String mac_ip =ConfigUtil.getString("commonUtils.opStation");
            return mac_ip + "_DD00";

    }



    /**
     * 判断是否为交易日
     * true为交易日
     * false为非交易日
     */
    public static boolean isTradeDay(OpenPositionDao openPositionDao) {
        boolean bool = false;
        try {
            Map<String, Object> map_date = new HashMap();
            map_date.put("I_RQ", "");
            map_date.put("O_ISVALID", "");
            openPositionDao.judgmentDay(map_date);
            if ( Integer.parseInt(map_date.get("O_ISVALID").toString()) == 1 && map_date != null) {
                bool = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bool;
    }
}
