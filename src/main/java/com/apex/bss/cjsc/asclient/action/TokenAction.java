package com.apex.bss.cjsc.asclient.action;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.apex.bss.cjsc.base.CommonUtil;
import com.apex.bss.cjsc.base.SecurityHelper;
import com.apex.bss.cjsc.dao.UserDao;
import com.apex.bss.cjsc.redis.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by jinsh on 2017/2/10.
 */
@Controller
public class TokenAction {
    @Autowired
    private UserDao userDao;

    @RequestMapping(value = "/getToken")
    @ResponseBody
    public JSON getToken(HttpServletRequest request, HttpServletResponse response, Model model){
        JSONObject jsonRet = new JSONObject();
        JSONObject req_data = CommonUtil.getParams(request);
        int code = -1;
        String error_msg = "获取token接口出错";
        try{
            String userid = req_data.getString("user");
            if (null == userid && "".equals(userid)) {
                jsonRet.put("code",-1);
                jsonRet.put("note","参数【user】错误");
                return jsonRet;
            }
            String pwd = req_data.getString("pwd").replace(" ","+");
            if (null == pwd && "".equals(pwd)) {
                jsonRet.put("code",-1);
                jsonRet.put("note","参数【pwd】错误");
                return jsonRet;
            }

            String[] pwdSplit = pwd.split("_");
            if (pwdSplit.length > 1) {
                //数据库中查询密文密码
                Map map=new HashMap();
                map.put("O_CODE","");
                map.put("O_MM","");
                map.put("I_YHBM",userid);
                userDao.selectPwd(map);
                String encreptpwd = map.get("O_MM").toString();
                //加密验证
                String randomNumber = SecurityHelper.encode(pwdSplit[1],"");
                if ((encreptpwd + randomNumber).equals(pwdSplit[0])) { //匹配成功生成token
                    String tokenEncrypted = SecurityHelper.encode(userid,"") +"_"+ SecurityHelper.encode(getCurrDateTime(),"");
                    JSONObject json = new JSONObject();
                    json.put("token",tokenEncrypted);
                    List list = new ArrayList();
                    list.add(json);
                    jsonRet.put("data",list);
                    code = 1;
                    error_msg = "success";

                    //放入redis
                    RedisUtil rs = new RedisUtil();
                    rs.setnx(tokenEncrypted,tokenEncrypted);
                    rs.expire(tokenEncrypted,60*30);
                    System.out.println(rs.exists(tokenEncrypted));
                } else {//不匹配
                    error_msg = "用户名或密码有误";
                }
            } else {
                error_msg = "请求格式出错";
            }
        }catch(Exception e){
            e.printStackTrace();
            error_msg = "获取token失败";
        }

        jsonRet.put("code",code);
        jsonRet.put("note",error_msg);

        return jsonRet;
    }


    @RequestMapping(value = "/checkToken")
    @ResponseBody
    public JSON checkToken(HttpServletRequest request, HttpServletResponse response, Model model){
        JSONObject jsonRet = new JSONObject();
        JSONObject req_data = CommonUtil.getParams(request);
        int code = CommonUtil.checkToken(req_data.getString("token"));
        jsonRet.put("code",code);
        if(code > 0){
            jsonRet.put("note","请求成功");
            return jsonRet;
        }else{
            jsonRet.put("note","token无效");
            return jsonRet;
        }
    }

    private String getCurrDateTime(){
        SimpleDateFormat localTime = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        Date date = new Date();
        return localTime.format(date).toString();
    }
}
