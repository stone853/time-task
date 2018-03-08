package com.apex.bss.cjsc.taskn;

import com.apex.bss.cjsc.dao.OpenPositionDao;
import com.apex.bss.cjsc.redis.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by jinsh on 2017/3/16.
 */
public class TaskLog {
    protected static final Logger log= LoggerFactory.getLogger(TaskLog.class);
    public static TaskLog taskLog;
    private RedisUtil rs;

    public synchronized  static TaskLog getTaskLog(){
        if(null == taskLog) {
            return new TaskLog();
        }
        return taskLog;
    }

    /**
     * 主任务
     */
    private String taskMain;

    /**
     * 赎回
     */
    private String taskRedem = "redem";

    /**
     * 资金划转
     */
    private String taskFundin = "fundid";

    /**
     * 自动定投
     */
    private String taskAutoapply = "autoapply";

    /**
     * 追加申购
     */
    private String taskPurchase = "purchase";


    private TaskLog() {
        String uuid = UUID.randomUUID().toString();
        this.taskMain = "main"+ uuid;
        this.taskRedem = "redem"+uuid;
        this.taskFundin = "fundid" + uuid;
        this.taskAutoapply = "autoapply" + uuid;
        this.taskPurchase = "purchase" + uuid;
        this.rs = new RedisUtil();
    }


    /**
     * 主任务
     * @param type    ====> I_GXZT
     * @param status ====> I_ZT
     * @param mark   ====>I_BZ
     */
    public void insMasterLog(OpenPositionDao openPositionDao, String type, String status, String mark){
        try {
            Map<String,Object> map = new HashMap<String,Object>();
            map.put("I_GXZT",type);
            map.put("I_ID","");
            map.put("I_ZT",status);
            map.put("I_BZ",mark);
            openPositionDao.masterLog(map);
            //如果为新增 则赋值
            String masterID = null != map.get("O_NOTE") ? map.get("O_NOTE").toString() : "-1" ;
            rs.set("masterID",masterID);
        }catch (Exception e) {
            log.error("插入主任务日志失败");
            e.printStackTrace();
        }

    }

    /**
     * 主任务
     * @param type    ====> I_GXZT
     * @param status ====> I_ZT
     * @param mark   ====>I_BZ
     */
    public void updMasterLog(OpenPositionDao openPositionDao, String type, String status, String mark){
        try {
            String masterID=rs.getKey("masterID");
            if(null == masterID || "".equals(masterID)) {
                log.error("masterId 为空");
                return;
            }
            Map<String,Object> map = new HashMap<String,Object>();
            map.put("I_GXZT",type);
            map.put("I_ID",masterID);
            map.put("I_ZT",status);
            map.put("I_BZ",mark);
            openPositionDao.masterLog(map);
        } catch (Exception e) {
            log.error("修改主任务日志失败");
            e.printStackTrace();
        }

    }

    /**
     * 子任务
     * @param
     * @param taskType      ====> I_CZLX
     * @param operType      ====> I_GXZT
     * @param qryTimes      ====> I_TQSC
     * @param handleTimes   ====> I_ZXSC
     * @param status        ====> I_ZT
     * @param count         ====> I_ZJLS
     * @param succCount     ====> I_CGJLS
     * @param failCount     ====> I_SBJLS
     * @param mark          ====> I_BZ
     */
    public void insRedemLog(OpenPositionDao openPositionDao,String taskType, String operType, Long qryTimes, Long handleTimes,
                                 String status,int count,int succCount,
                                 int failCount,String mark,String opStation){
        try {
            String masterID=rs.getKey("masterID");
            if(null == masterID || "".equals(masterID)) {
                log.error("masterId 为空");
                return;
            }
            Map<String,Object> map = new HashMap<String,Object>();
            map.put("I_CZLX",taskType);
            map.put("I_GXZT",operType);
            map.put("I_TQSC",qryTimes);
            map.put("I_ZXSC",handleTimes);
            map.put("I_ID","");
            map.put("I_ZRWID",masterID);
            map.put("I_ZT",status);
            map.put("I_ZJLS",count);
            map.put("I_CGJLS",succCount);
            map.put("I_SBJLS",failCount);
            map.put("I_BZ",mark);
            map.put("I_OP_STATION",opStation);
            openPositionDao.subLog(map);
            //如果为新增 则赋值
            taskRedem = null != map.get("O_NOTE") ? map.get("O_NOTE").toString() : "-1" ;
        } catch (Exception e) {
            log.error("插入赎回子任务日志失败");
            e.printStackTrace();
        }
    }

    /**
     * 子任务
     * @param
     * @param taskType      ====> I_CZLX
     * @param operType      ====> I_GXZT
     * @param qryTimes      ====> I_TQSC
     * @param handleTimes   ====> I_ZXSC
     * @param status        ====> I_ZT
     * @param count         ====> I_ZJLS
     * @param succCount     ====> I_CGJLS
     * @param failCount     ====> I_SBJLS
     * @param mark          ====> I_BZ
     */
    public void updRedemLog(OpenPositionDao openPositionDao,String taskType, String operType, Long qryTimes, Long handleTimes,
                            String status,int count,int succCount,
                            int failCount,String mark,String opStation){
        try {
            String masterID=rs.getKey("masterID");
            if(null == masterID || "".equals(masterID)) {
                log.error("masterId 为空");
                return;
            }

            if(null == taskRedem || "".equals(taskRedem)) {
                log.error("taskRedem 为空");
                return;
            }
            Map<String,Object> map = new HashMap<String,Object>();
            map.put("I_CZLX",taskType);
            map.put("I_GXZT",operType);
            map.put("I_TQSC",qryTimes);
            map.put("I_ZXSC",handleTimes);
            map.put("I_ID",taskRedem);
            map.put("I_ZRWID",masterID);
            map.put("I_ZT",status);
            map.put("I_ZJLS",count);
            map.put("I_CGJLS",succCount);
            map.put("I_SBJLS",failCount);
            map.put("I_BZ",mark);
            map.put("I_OP_STATION",opStation);
            openPositionDao.subLog(map);
        } catch (Exception e) {
            log.error("修改赎回子任务日志失败");
            e.printStackTrace();
        }
    }

    /**
     * 子任务
     * @param
     * @param taskType      ====> I_CZLX
     * @param operType      ====> I_GXZT
     * @param qryTimes      ====> I_TQSC
     * @param handleTimes   ====> I_ZXSC
     * @param status        ====> I_ZT
     * @param count         ====> I_ZJLS
     * @param succCount     ====> I_CGJLS
     * @param failCount     ====> I_SBJLS
     * @param mark          ====> I_BZ
     */
    public void insFundinLog(OpenPositionDao openPositionDao,String taskType, String operType, Long qryTimes, Long handleTimes,
                            String status,int count,int succCount,
                            int failCount,String mark,String opStation){
        try {
            String masterID=rs.getKey("masterID");
            if(null == masterID || "".equals(masterID)) {
                log.error("masterId 为空");
                return;
            }
            Map<String,Object> map = new HashMap<String,Object>();
            map.put("I_CZLX",taskType);
            map.put("I_GXZT",operType);
            map.put("I_TQSC",qryTimes);
            map.put("I_ZXSC",handleTimes);
            map.put("I_ID","");
            map.put("I_ZRWID",masterID);
            map.put("I_ZT",status);
            map.put("I_ZJLS",count);
            map.put("I_CGJLS",succCount);
            map.put("I_SBJLS",failCount);
            map.put("I_BZ",mark);
            map.put("I_OP_STATION",opStation);
            openPositionDao.subLog(map);
            //如果为新增 则赋值
            taskFundin = null != map.get("O_NOTE") ? map.get("O_NOTE").toString() : "-1" ;
        } catch (Exception e) {
            log.error("插入子任务资金划转日志失败");
        }
    }

    /**
     * 子任务
     * @param
     * @param taskType      ====> I_CZLX
     * @param operType      ====> I_GXZT
     * @param qryTimes      ====> I_TQSC
     * @param handleTimes   ====> I_ZXSC
     * @param status        ====> I_ZT
     * @param count         ====> I_ZJLS
     * @param succCount     ====> I_CGJLS
     * @param failCount     ====> I_SBJLS
     * @param mark          ====> I_BZ
     */
    public void updFundinLog(OpenPositionDao openPositionDao,String taskType, String operType, Long qryTimes, Long handleTimes,
                           String status,int count,int succCount,
                           int failCount,String mark,String opStation){
        try {
            String masterID=rs.getKey("masterID");
            if(null == masterID || "".equals(masterID)) {
                log.error("masterId 为空");
                return;
            }
            if(null == taskFundin || "".equals(taskFundin)) {
                log.error("taskFundin 为空");
                return;
            }
            Map<String,Object> map = new HashMap<String,Object>();
            map.put("I_CZLX",taskType);
            map.put("I_GXZT",operType);
            map.put("I_TQSC",qryTimes);
            map.put("I_ZXSC",handleTimes);
            map.put("I_ID",taskFundin);
            map.put("I_ZRWID",masterID);
            map.put("I_ZT",status);
            map.put("I_ZJLS",count);
            map.put("I_CGJLS",succCount);
            map.put("I_SBJLS",failCount);
            map.put("I_BZ",mark);
            map.put("I_OP_STATION",opStation);
            openPositionDao.subLog(map);
        } catch (Exception e) {
            log.error("修改子任务资金划转失败");
            e.printStackTrace();
        }
    }


    /**
     * 子任务
     * @param
     * @param taskType      ====> I_CZLX
     * @param operType      ====> I_GXZT
     * @param qryTimes      ====> I_TQSC
     * @param handleTimes   ====> I_ZXSC
     * @param status        ====> I_ZT
     * @param count         ====> I_ZJLS
     * @param succCount     ====> I_CGJLS
     * @param failCount     ====> I_SBJLS
     * @param mark          ====> I_BZ
     */
    public void insAutoapplyLog(OpenPositionDao openPositionDao,String taskType, String operType, Long qryTimes, Long handleTimes,
                            String status,int count,int succCount,
                            int failCount,String mark,String opStation){
        try {
            String masterID=rs.getKey("masterID");
            if(null == masterID || "".equals(masterID)) {
                log.error("masterId 为空");
                return;
            }
            Map<String,Object> map = new HashMap<String,Object>();
            map.put("I_CZLX",taskType);
            map.put("I_GXZT",operType);
            map.put("I_TQSC",qryTimes);
            map.put("I_ZXSC",handleTimes);
            map.put("I_ID","");
            map.put("I_ZRWID",masterID);
            map.put("I_ZT",status);
            map.put("I_ZJLS",count);
            map.put("I_CGJLS",succCount);
            map.put("I_SBJLS",failCount);
            map.put("I_BZ",mark);
            map.put("I_OP_STATION",opStation);
            openPositionDao.subLog(map);
            //如果为新增 则赋值
            taskAutoapply = null != map.get("O_NOTE") ? map.get("O_NOTE").toString() : "-1" ;
        } catch (Exception e) {
            log.error("插入子任务自动定投日志失败");
            e.getMessage();
        }
    }

    /**
     * 子任务
     * @param
     * @param taskType      ====> I_CZLX
     * @param operType      ====> I_GXZT
     * @param qryTimes      ====> I_TQSC
     * @param handleTimes   ====> I_ZXSC
     * @param status        ====> I_ZT
     * @param count         ====> I_ZJLS
     * @param succCount     ====> I_CGJLS
     * @param failCount     ====> I_SBJLS
     * @param mark          ====> I_BZ
     */
    public void updAutoapplyLog(OpenPositionDao openPositionDao,String taskType, String operType, Long qryTimes, Long handleTimes,
                            String status,int count,int succCount,
                            int failCount,String mark,String opStation){
        try {
            String masterID=rs.getKey("masterID");
            if(null == masterID || "".equals(masterID)) {
                log.error("masterId 为空");
                return;
            }
            if(null == taskAutoapply || "".equals(taskAutoapply)) {
                log.error("taskAutoapply 为空");
                return;
            }
            Map<String,Object> map = new HashMap<String,Object>();
            map.put("I_CZLX",taskType);
            map.put("I_GXZT",operType);
            map.put("I_TQSC",qryTimes);
            map.put("I_ZXSC",handleTimes);
            map.put("I_ID",taskAutoapply);
            map.put("I_ZRWID",masterID);
            map.put("I_ZT",status);
            map.put("I_ZJLS",count);
            map.put("I_CGJLS",succCount);
            map.put("I_SBJLS",failCount);
            map.put("I_BZ",mark);
            map.put("I_OP_STATION",opStation);
            openPositionDao.subLog(map);
        } catch (Exception e) {
            log.error("修改子任务自动定投日志失败");
            e.printStackTrace();
        }
    }


    /**
     * 子任务
     * @param
     * @param taskType      ====> I_CZLX
     * @param operType      ====> I_GXZT
     * @param qryTimes      ====> I_TQSC
     * @param handleTimes   ====> I_ZXSC
     * @param status        ====> I_ZT
     * @param count         ====> I_ZJLS
     * @param succCount     ====> I_CGJLS
     * @param failCount     ====> I_SBJLS
     * @param mark          ====> I_BZ
     */
    public void insPurchaseLog(OpenPositionDao openPositionDao,String taskType, String operType, Long qryTimes, Long handleTimes,
                               String status,int count,int succCount,
                               int failCount,String mark,String opStation){
        try {
            String masterID=rs.getKey("masterID");
            if(null == masterID || "".equals(masterID)) {
                log.error("masterId 为空");
                return;
            }
            Map<String,Object> map = new HashMap<String,Object>();
            map.put("I_CZLX",taskType);
            map.put("I_GXZT",operType);
            map.put("I_TQSC",qryTimes);
            map.put("I_ZXSC",handleTimes);
            map.put("I_ID","");
            map.put("I_ZRWID",masterID);
            map.put("I_ZT",status);
            map.put("I_ZJLS",count);
            map.put("I_CGJLS",succCount);
            map.put("I_SBJLS",failCount);
            map.put("I_BZ",mark);
            map.put("I_OP_STATION",opStation);
            openPositionDao.subLog(map);
            //如果为新增 则赋值
            taskPurchase = null != map.get("O_NOTE") ? map.get("O_NOTE").toString() : "-1" ;
        } catch (Exception e) {
            log.error("插入子任务追加申购日志失败");
            e.printStackTrace();
        }
    }

    /**
     * 子任务
     * @param
     * @param taskType      ====> I_CZLX
     * @param operType      ====> I_GXZT
     * @param qryTimes      ====> I_TQSC
     * @param handleTimes   ====> I_ZXSC
     * @param status        ====> I_ZT
     * @param count         ====> I_ZJLS
     * @param succCount     ====> I_CGJLS
     * @param failCount     ====> I_SBJLS
     * @param mark          ====> I_BZ
     */
    public void updPurchaseLog(OpenPositionDao openPositionDao,String taskType, String operType, Long qryTimes, Long handleTimes,
                               String status,int count,int succCount,
                               int failCount,String mark,String opStation){
        try {
            String masterID=rs.getKey("masterID");
            if(null == masterID || "".equals(masterID)) {
                log.error("masterId 为空");
                return;
            }
            if(null == taskPurchase || "".equals(taskPurchase)) {
                log.error("taskPurchase 为空");
                return;
            }

            Map<String,Object> map = new HashMap<String,Object>();
            map.put("I_CZLX",taskType);
            map.put("I_GXZT",operType);
            map.put("I_TQSC",qryTimes);
            map.put("I_ZXSC",handleTimes);
            map.put("I_ID",taskPurchase);
            map.put("I_ZRWID",masterID);
            map.put("I_ZT",status);
            map.put("I_ZJLS",count);
            map.put("I_CGJLS",succCount);
            map.put("I_SBJLS",failCount);
            map.put("I_BZ",mark);
            map.put("I_OP_STATION",opStation);
            openPositionDao.subLog(map);
        } catch (Exception e) {
            log.error("修改子任务追加申购日志失败");
            e.printStackTrace();
        }
    }

}
