package com.apex.bss.cjsc.model;

/**
 * Created by jinsh on 2017/1/10.
 */
public class User {
    private int id;
    private String userid;
    private String name;
    public User(){}

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUserid() {
        return userid;
    }

    public String toString(){
        return "User[id="+id+" , userid="+userid+" , name="+name+"]";
    }


}