package com.apex.bss.cjsc.asclient.service;

import com.apex.bss.cjsc.dao.UserDao;
import com.apex.bss.cjsc.model.User;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jinshi on 2017/1/6.
 */
public class Test {
    public static void main(String args[]){
        ApplicationContext ctx=null;
        ctx=new ClassPathXmlApplicationContext("spring/applicationContext.xml");
        UserDao userDao=(UserDao) ctx.getBean("userDao");
        User user=new User();
        //添加两条数据
        user.setId(1);
        //System.out.println(userDao.getUserById(1).toString());
    }
}
