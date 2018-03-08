package com.apex.bss.cjsc.taskn.fundin;

import com.alibaba.fastjson.JSONObject;
import com.apex.bss.cjsc.asclient.service.IfsService;
import com.apex.bss.cjsc.dao.OpenPositionDao;
import com.apex.bss.cjsc.redis.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CountDownLatch;

/**
 * Created by jinsh on 2017/2/4.
 */
public class SendApplyInfo implements Runnable{
    protected static final Logger log= LoggerFactory.getLogger(Consumer.class);
    private OpenPositionDao openPositionDao;
    private String taskInfo;
    private RedisUtil rs;
    private CountDownLatch taskCounts;

    public SendApplyInfo(String taskInfo, OpenPositionDao openPositionDao,CountDownLatch taskCounts){
        this.taskInfo = taskInfo;
        this.openPositionDao = openPositionDao;
        this.rs = new RedisUtil();
        this.taskCounts = taskCounts;
    }

    @Override
    public void run() {
        String uuid = UUID.randomUUID().toString();
        try{
            log.info(taskInfo);
            JSONObject json = (JSONObject)JSONObject.parse(taskInfo);
            IfsService ifs_Service=new IfsService();
            Map map_hb =ifs_Service.capitalAllocation(json.getString("op_station"),"",json.getString("client_id"),
                    json.getString("fund_account"),json.getString("combine_stock_no"),json.getDouble("occur_balance"),"0","0");
            int I_ZT=0;
            Map map=new HashMap();
            if(null != map_hb && Integer.parseInt(map_hb.get("errNo").toString())==0) {
                List list_sg = (List) map_hb.get("list");
                I_ZT=1;
                rs.incr("fundin_number");
            }
            Date d = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            String dateNowStr = sdf.format(d);
            map.put("O_CODE","");
            map.put("O_NOTE","");
            map.put("I_TJRQ",dateNowStr);
            map.put("I_ZHBH",json.getString("combine_stock_no"));
            map.put("I_ZT",I_ZT);
            log.debug("资金划拨结果入参:"+map.toString());
            openPositionDao.allotMoney(map);
            log.debug("资金划拨结果出参:"+map.toString());
        }catch (Exception e){
            e.printStackTrace();
            log.error("处理task出错");
        }finally {
            taskCounts.countDown();
        }

    }
}

