package com.apex.bss.cjsc.asclient.action;



import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.apex.bss.cjsc.asclient.action.bus.Bus;
import com.apex.bss.cjsc.base.CommonUtil;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Created by Jinshi on 2017/1/6.
 */
@Controller
@RequestMapping("/probus")
public class ProcessBusAction implements ApplicationContextAware{
    private ApplicationContext ctx;
    @RequestMapping(value = "/getInfo")
    @ResponseBody
    public JSON getInfo(HttpServletRequest request, HttpServletResponse response,Model model){
        JSONObject req_data = CommonUtil.getParams(request);
        String func = req_data.getString("funcId");
        try{
            Bus bus = (Bus)ctx.getBean("Bus"+func);
            return bus.invoke(req_data);
        }catch(Exception e){
            JSONObject ret = new JSONObject();
            ret.put("errmsg",e.getMessage());
            e.printStackTrace();
            return ret;
        }
    }

    @RequestMapping(value = "/view")
    public String view(Model model){

        model.addAttribute("js","test");
        return "account";
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ctx = applicationContext;
    }


}
