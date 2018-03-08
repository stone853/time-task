package com.apex.bss.cjsc.taskn.transferfund;

import com.alibaba.fastjson.JSONObject;
import com.apex.bss.cjsc.asclient.service.IfsService;
import com.apex.bss.cjsc.dao.OpenPositionDao;
import com.apex.bss.cjsc.redis.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * Created by wzy on 2017/4/6.
 */
public class SendApplyInfo implements Runnable{
    protected static final Logger log= LoggerFactory.getLogger(Consumer.class);
    private OpenPositionDao openPositionDao;
    private String taskInfo;
    private RedisUtil rs;
    private CountDownLatch taskCounts;
    private Integer I_DDFS;

    public SendApplyInfo(String taskInfo, OpenPositionDao openPositionDao, CountDownLatch taskCounts,Integer I_DDFS){
        this.I_DDFS = I_DDFS;
        this.taskInfo = taskInfo;
        this.openPositionDao = openPositionDao;
        this.rs = new RedisUtil();
        this.taskCounts = taskCounts;
    }

    @Override
    public void run() {
        try{
            log.info("余额转货基"+taskInfo);
            JSONObject json = (JSONObject)JSONObject.parse(taskInfo);
            IfsService ifs_Service=new IfsService();
            Map map_YE =ifs_Service.queryAccount(json.getString("opStation"),json.getString("COMBINE_CODE"),"b","4");
            log.info(map_YE.toString()+json.getString("COMBINE_CODE"));
            Map map=new HashMap();
            map.put("O_CODE","");
            map.put("O_NOTE","");
            map.put("I_FAID",json.getInteger("COMBINE_ID"));
            if(null != map_YE && Integer.parseInt(map_YE.get("errNo").toString()) == 0){
                map.put("I_ZT",1);
                map.put("I_CWDM","");
                map.put("I_CWXX","");
            }else{
                map.put("I_ZT",0);
                map.put("I_CWDM",map_YE.get("errNo"));
                map.put("I_CWXX",map_YE.get("errMsg"));
            }
            map.put("I_OP_STATION",json.getString("opStation"));
            map.put("I_DDFS",I_DDFS);
            openPositionDao.fundlog(map);//记录余额转货基日志
        } catch (Exception e){
            e.printStackTrace();
            log.error("处理task出错");
        } finally {
            this.taskCounts.countDown();
        }
    }
}

