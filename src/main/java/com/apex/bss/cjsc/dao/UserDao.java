package com.apex.bss.cjsc.dao;

import com.apex.bss.cjsc.model.User;

import java.util.Map;

/**
 * Created by jinsh on 2017/1/10.
 */
public interface UserDao {
   /* public User getUser(User user);
    public User getUserById(int id);
    public void addUser(User user);
    public void updateUser(User user);
    public void deleteUser(int UserId);*/
   public String selectPwd(Map map);
}