package com.apex.bss.cjsc.taskn.redemption;

import com.alibaba.fastjson.JSONObject;
import com.apex.bss.cjsc.asclient.service.IfsService;
import com.apex.bss.cjsc.dao.OpenPositionDao;
import com.apex.bss.cjsc.redis.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
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

    public SendApplyInfo(String taskInfo,OpenPositionDao openPositionDao,CountDownLatch taskCounts){
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
            Map map_hb =ifs_Service.capitalAllocation(json.getString("op_station"),"",json.getString("client_id"),json.getString("fund_account"),
                    json.getString("combine_stock_no"),json.getDouble("occur_balance"),"0","1");
            Map map=new HashMap();
            map.put("O_CODE","");
            map.put("O_NOTE","");
            map.put("I_CZLSH",json.getString("I_CZLSH"));
            map.put("I_KHH",json.getString("client_id"));
            map.put("I_ZJZH",json.getString("fund_account"));
            map.put("I_ZHBH",json.getString("combine_stock_no"));
            map.put("I_DBFX",1);
            map.put("I_DBJE",json.getDouble("occur_balance"));
            if(null != map_hb && Integer.parseInt(map_hb.get("errNo").toString())==0){//划拨成功
                map.put("I_ZT",1);
                rs.incr("redem_number");
            }else{
                map.put("I_ZT",0);
            }
            log.debug("记录组合资金划拨状态入参:"+map.toString());
            openPositionDao.allocationStatus(map);//记录组合资金划拨状态
            log.debug("记录组合资金划拨状态出参:"+map.toString());
        } catch (Exception e){
            e.printStackTrace();
            log.error("处理task出错");
        } finally {
            this.taskCounts.countDown();
        }
    }
}

