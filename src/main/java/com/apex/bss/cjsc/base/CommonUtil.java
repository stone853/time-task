package com.apex.bss.cjsc.base;

import com.alibaba.fastjson.JSONObject;
import com.apex.bss.cjsc.asclient.service.AsClientService;
import com.apex.bss.cjsc.common.Common;
import com.apex.bss.cjsc.dao.OpenPositionDaoImp;
import com.apex.bss.cjsc.redis.RedisUtil;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by jinsh on 2017/2/7.
 */
public class CommonUtil {
    protected static final Logger log = Logger.getLogger(CommonUtil.class);

    public static boolean checkTime(String star,String end){
        SimpleDateFormat localTime=new SimpleDateFormat("HH:mm:ss");
        try{
            Date sdate = localTime.parse(star);
            Date edate=localTime.parse(end);
            Date ndate = localTime.parse((localTime.format(new Date()).toString()));
            if(ndate.getTime()>=sdate.getTime()&& ndate.getTime()<=edate.getTime()){
                return true;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }



    public static JSONObject getParams(HttpServletRequest request){
        JSONObject req_data = new JSONObject();
        Enumeration paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = (String) paramNames.nextElement();

            String[] paramValues = request.getParameterValues(paramName);
            if (paramValues.length == 1) {
                String paramValue = paramValues[0];
                if (paramValue.length() != 0) {
                    req_data.put(paramName, paramValue);
                }
            }
        }
        return req_data;
    }

    /**
     * 验证token是否有效，
     * @param token
     * @return  0 有效, -1 无效  -2 redis 中不存在 -3 token 格式错误
     */
    public static int checkToken(String token){
        if (null != token && !"".equals(token)) {
            RedisUtil rs = new RedisUtil();
            if (rs.exists(token)) {
                String[] tokenSplit = token.split("_");
                if (tokenSplit.length > 1) {
                    rs.expire(token,60*30);
                    return 1;
                } else {
                    return -3;
                }
            } else {
                return -2;
            }
        }
        return -1; //失败
    }

    /**
     * 获取恒生ifstoken
     * @return
     */
    public static String getHSToken(){
        try{
            RedisUtil rs = new RedisUtil();
            if(rs.exists("hsIfsToken")){
                return rs.getKey("hsIfsToken");
            }else{
                AsClientService as_ClientService =new AsClientService();
                Map paramMap = new HashMap();
                paramMap.put("hs_func_no",Common.HS_IFS_FUNC_NO);//业务功能号
                paramMap.put("extsystem_user_id",Common.HS_IFS_USER_ID);//业务功能号
                paramMap.put("extsystem_password",Common.HS_IFS_PASSWORD);//业务功能号
                Map retMap = as_ClientService.getResultListMap(140346,paramMap);
                List list= (List) retMap.get("list");
                Map mm= (Map) list.get(0);
                String cjtocken= (String) mm.get("cjtocken");
                String ifsToken="";
                String [] tocken=cjtocken.split("\\;");
                if(tocken!=null&&tocken.length>=3){
                    String [] token=tocken[2].split("\\,");
                    if(tocken!=null&&token.length>=2){
                        ifsToken = token[1];
                    }
                }
                rs.setnx("hsIfsToken",ifsToken);
                rs.expire("hsIfsToken",60*20);
                return ifsToken;
            }
        }catch (Exception e) {
            log.error("获取hs token出错" + e.getMessage());
            e.printStackTrace();
            return null;
        }

    }

    /**
     * 判断是否是交易日
     * true为交易日
     * false为非交易日
     */
    public static boolean isTradeDay(int date){
        boolean bool=false;
        try{
            Map map_BD=new HashMap();
            map_BD.put("I_RQ",date);
            map_BD.put("O_ISVALID","");
            OpenPositionDaoImp openPositionDao=new OpenPositionDaoImp();
            openPositionDao.judgmentDay(map_BD);
            if(null != map_BD && Integer.parseInt(map_BD.get("O_ISVALID").toString())==1){
                bool = true;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return bool;
    }


    public static void main(String args[]){
        System.out.println(new CommonUtil().checkTime("10:01:00","24:00:00"));
    }


}
