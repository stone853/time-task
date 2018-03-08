package com.apex.bss.cjsc.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.apex.bss.cjsc.base.CommonUtil;
import com.apex.bss.cjsc.redis.RedisUtil;
import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**[
 * Created by jinsh on 2017/2/9.
 */
public class BusInterceptor extends HandlerInterceptorAdapter {
    protected static final Logger log = Logger.getLogger(BusInterceptor.class);
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler) throws Exception {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        JSONObject json = new JSONObject();
        PrintWriter out = null;
        try {
            JSONObject req_data = CommonUtil.getParams(request);
            int code = CommonUtil.checkToken(req_data.getString("token"));
            json.put("code",code);


            if(code > 0){
//                json.put("note","请求成功");
//                out.append(json.toString());
                return true;
            }else{
                out = response.getWriter();
                json.put("note","无效会话");
                out.append(json.toString());
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            log.error("拦截器：请求异常");
            return false;
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    @Override
    public void postHandle(HttpServletRequest request,
                           HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
    }


}
