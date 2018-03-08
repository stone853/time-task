package com.apex.bss.cjsc.asclient.action.bus;

import com.alibaba.fastjson.JSONObject;
import com.apex.bss.cjsc.base.SecurityHelper;
import com.apex.bss.cjsc.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jinsh on 2017/2/7.
 */
public class Bus20101 extends Bus{
    @Autowired
    private UserDao userDao;
    @Override
    public JSONObject invoke(JSONObject req_data) {
        JSONObject jsonRet = new JSONObject();
        int code = -1;
        String error_msg = "Bus20101接口出错";
        String userid = req_data.getString("userid");
        if (null == userid && "".equals(userid)) {
            jsonRet.put("code",-1);
            jsonRet.put("note","参数【userid】错误");
            return jsonRet;
        }
        String pwd = req_data.getString("pwd");
        if (null == pwd && "".equals(pwd)) {
            jsonRet.put("code",-1);
            jsonRet.put("note","参数【pwd】错误");
            return jsonRet;
        }

        String[] pwdSplit = pwd.split("_");
        if (pwdSplit.length > 1) {
            //数据库中查询密文密码
            // String encreptpwd = "LyI/Nt5JzU4=";
            Map map=new HashMap();
            map.put("O_MM","");
            map.put("I_YHBM",userid);
            userDao.selectPwd(map);
            String encreptpwd =  map.get("O_MM").toString();
            //加密验证
            String randomNumber = SecurityHelper.encode(pwdSplit[1],"");
            if ((encreptpwd + randomNumber).equals(pwdSplit[0])) { //匹配成功生成token
                String tokenEncrypted = SecurityHelper.encode(userid,"") +"_"+ SecurityHelper.encode(getCurrDateTime(),"");
                JSONObject json = new JSONObject();
                json.put("token",tokenEncrypted);
                jsonRet.put("data",json);
                code = 1;
                error_msg = "success";
            } else {//不匹配
                error_msg = "用户名或密码有误";
            }
        } else {
            error_msg = "用户名或密码有误";
        }

        jsonRet.put("code",code);
        jsonRet.put("note",error_msg);

        return jsonRet;
    }

    private String getCurrDateTime(){
        SimpleDateFormat localTime = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        Date date = new Date();
        return localTime.format(date).toString();
    }

    public static void main(String args[]){
        SimpleDateFormat localTime=new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();
        System.out.println(date.getTime());
        date.setTime(date.getTime()+60*1000*30);
        String s = "2017-05-02 34:234:23";
        System.out.println(s.split(" ")[0]);
        System.out.println(s.split(" ")[1]);

        System.out.println(localTime.format(date).toString());
        //System.out.println(CommonUtil.checkTime("17:02:08","17:05:08"));
    }
}
