package com.apex.bss.cjsc.taskn.purchase;

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
            JSONObject json = (JSONObject) JSONObject.parse(taskInfo);
            IfsService ifs_Service=new IfsService();
            List combine_list= (List) json.get("combine_data");
            StringBuffer buffer = new StringBuffer();
            for(int i=0;i<combine_list.size();i++){
                JSONObject js = new JSONObject();
                js = (JSONObject) combine_list.get(i);
                buffer.append(js.getString("fund_code")+"|"+js.getString("fund_company")+"|"+js.getString("balance")+";");
            }
            buffer.deleteCharAt(buffer.length()-1);
            String cpdm= String.valueOf(buffer);
            Map map_sg =ifs_Service.fundPurchase(json.getString("op_station"),"",json.getString("cust_no"),json.getString("cust_account"),
                    json.getString("combine_stock_no"),"b","8",json.get("combine_data").toString());
            JSONObject json_jy = new JSONObject();
            if(null != map_sg && Integer.parseInt(map_sg.get("errNo").toString())==0){//申购成功
                rs.incr("purchase_number");
                List list_sg = (List) map_sg.get("list");
                Map map_zt = (Map) list_sg.get(0);
                json_jy=ifs_Service.tradingOperation("11",json.getString("cust_type"),json.getString("cust_no"),
                        json.getString("cust_account"),json.getString("combine_stock_no"),1,cpdm,json.getString("I_CZLSH"),
                        json.getDouble("add_amount"), json.getString("op_station"),map_zt.get("combine_entrust_no").toString(), map_zt.get("combine_entrust_no").toString(),
                        map_zt.get("entrust_status").toString());
                log.info(json_jy.toJSONString());
            }else{
                json_jy=ifs_Service.tradingOperation("11",json.getString("cust_type"),json.getString("cust_no"),
                        json.getString("cust_account"),json.getString("combine_stock_no"),1,cpdm,json.getString("I_CZLSH"),json.getDouble("add_amount"),
                        json.getString("op_station"),"","","9");
            }
            rs.lpush(Common.PRO_PIF_ZHJY_JYCZ,json_jy.toJSONString());
        } catch (Exception e){
            e.printStackTrace();
            log.error("处理task出错");
        }finally {
            taskCounts.countDown();
        }

    }
}

