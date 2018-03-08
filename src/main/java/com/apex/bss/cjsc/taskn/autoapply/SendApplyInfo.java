package com.apex.bss.cjsc.taskn.autoapply;

import com.alibaba.fastjson.JSONObject;
import com.apex.bss.cjsc.asclient.service.IfsService;
import com.apex.bss.cjsc.common.Common;
import com.apex.bss.cjsc.dao.OpenPositionDao;
import com.apex.bss.cjsc.redis.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
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
        this.openPositionDao=openPositionDao;
        this.rs = new RedisUtil();
        this.taskCounts = taskCounts;
    }

    @Override
    public void run() {
        String uuid = UUID.randomUUID().toString();
        try{
            log.info(taskInfo);
            JSONObject json = (JSONObject)JSONObject.parse(taskInfo);
            IfsService ifsService=new IfsService();
            List combine_list= (List) json.get("combine_data");
            StringBuffer buffer = new StringBuffer();
            for(int i=0;i<combine_list.size();i++){
                JSONObject js = new JSONObject();
                js = (JSONObject) combine_list.get(i);
                buffer.append(js.getString("fund_code")+"|"+js.getString("fund_company")+"|"+js.getString("balance")+";");
            }
            buffer.deleteCharAt(buffer.length()-1);
            String cpdm= String.valueOf(buffer);
            Map map_sg =ifsService.fundPurchase(json.getString("op_station"), "", json.getString("cust_no"), json.getString("cust_account"),
                    json.getString("combine_stock_no"), json.getString("ofchannel_type"), json.getString("entrust_way"),json.get("combine_data").toString());//调用组合基金申购接口
            JSONObject json_jy = new JSONObject();
            if(null != map_sg && Integer.parseInt(map_sg.get("errNo").toString())==0) {
                rs.incr("auto_number");
                List li= (List) map_sg.get("list");
                Map map_zt= (Map) li.get(0);
                json_jy=ifsService.tradingOperation("8",json.getString("cust_type"),json.getString("cust_no"),
                        json.getString("cust_account"),json.getString("combine_stock_no"),1,cpdm,"",json.getDouble("add_amount"),
                        json.getString("op_station"),map_zt.get("combine_entrust_no").toString(), map_zt.get("combine_entrust_no").toString(),
                        map_zt.get("entrust_status").toString());
            }else{
                json_jy=ifsService.tradingOperation("8",json.getString("cust_type"),json.getString("cust_no"),
                        json.getString("cust_account"),json.getString("combine_stock_no"),1,cpdm,"",json.getDouble("add_amount"),
                        json.getString("op_station"),"","","9");
            }
            rs.lpush(Common.PRO_PIF_ZHJY_JYCZ,json_jy.toJSONString());
        } catch (Exception e){
            e.printStackTrace();
            log.error("处理task出错");
        } finally {
            taskCounts.countDown();
        }
    }
}

