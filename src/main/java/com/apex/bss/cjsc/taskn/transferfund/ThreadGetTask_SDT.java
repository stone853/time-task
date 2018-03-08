package com.apex.bss.cjsc.taskn.transferfund;

import com.alibaba.fastjson.JSONObject;
import com.apex.bss.cjsc.base.CommonUtil;
import com.apex.bss.cjsc.base.ConfigUtil;
import com.apex.bss.cjsc.dao.OpenPositionDao;
import com.apex.bss.cjsc.redis.RedisUtil;
import com.apex.bss.cjsc.taskn.TaskInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * Created by Saphurot on 2017/4/17.
 */
public class ThreadGetTask_SDT extends Thread{
    protected static final Logger log= LoggerFactory.getLogger(ThreadGetTask_SDT.class);
    private OpenPositionDao openPositionDao;
    private String opStation;
    private RedisUtil rs;

    public ThreadGetTask_SDT(OpenPositionDao openPositionDao, String opStation){
        this.openPositionDao = openPositionDao;
        this.opStation = opStation;

        this.rs = new RedisUtil();
    }

    /**
     * 开始任务
     */
    public void run() {
        try{
            Map<String,Object> map_date = new HashMap();
            map_date.put("I_RQ","");
            map_date.put("O_ISVALID","");
            openPositionDao.judgmentDay(map_date);
            int date = Integer.parseInt(map_date.get("O_ISVALID").toString());
            if(map_date != null && date == 1){
                rs.del("pro_trans");
                this.execQry();
                rs.del("pro_trans_temp");
            }else{
                log.info("今天不是交易日");
            }


        }catch (Exception e){
            e.printStackTrace();
            log.error("定时任务【余额转货基】异常...");
        }
    }


    /**
     * 开始执行轮询
     */
    private void execQry(){
        try {
            TaskInfo.autoOrSD = "0";
            log.info("查询数据库开始... step 1");
            Map map_query=new HashMap();
            map_query.put("O_CODE","");
            map_query.put("O_NOTE","");
            map_query.put("O_RESULT",new ArrayList<Map<String, Object>>());
            openPositionDao.queryAccount(map_query);
            if(null == map_query || map_query.size() ==0) {
                return;
            }
            log.info("开始处理数据... step 2");
            int count  = 0;
            List list= (List) map_query.get("O_RESULT");
            if(null !=list&&list.size()>0) {
                CountDownLatch taskCounts = new CountDownLatch(list.size());
                for (int i = 0; i < list.size(); i++) {
                    Map map = (Map) list.get(i);
                    try {
                        JSONObject json = new JSONObject();
                        json.put("opStation", opStation);//站点地址
                        json.put("COMBINE_ID", map.get("COMBINE_ID"));//产品号
                        json.put("COMBINE_CODE", map.get("COMBINE_CODE"));//产品池编号
                        rs.lpush("pro_trans", json.toJSONString());
                    } catch (Exception e) {
                        log.error("插入redis失败:" + map.toString());
                        e.printStackTrace();
                    }
                    count++;
                }
                TaskInfo.transIsDone_SD=false;
                new Thread(new Consumer(openPositionDao, taskCounts,0)).start();
                taskCounts.await();
            }

            TaskInfo.transIsDone_SD=true;
            log.info(".................end.............");
        } catch(Exception e) {
            log.error("轮询异常");
            e.printStackTrace();
        }
    }
}
