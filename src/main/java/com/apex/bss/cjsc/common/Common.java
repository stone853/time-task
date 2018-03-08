package com.apex.bss.cjsc.common;

import com.apex.bss.cjsc.base.ConfigUtil;

/**
 * Created by Lenovo on 2017/1/24.
 */
public class Common {
    //异步写入数据库过程名
    public static final String PRO_PIF_TZZH_ZHFA="PRO_PIF_TZZH_ZHFA";
    public static final String PRO_PIF_TZZH_DTFA="PRO_PIF_TZZH_DTFA";
    public static final String PRO_PIF_ZHJY_JYCZ="PRO_PIF_ZHJY_JYCZ";
    public static final String PRO_PIF_JYCZ_CZGL="PRO_PIF_JYCZ_CZGL";
    public static final String PRO_PIF_JYCZ_SCSG ="PRO_PIF_JYCZ_SCSG";


    public static final String PRO_PIF_ZHJY_BGDTZT="PRO_PIF_ZHJY_BGDTZT";
    public static final String PRO_PIF_ZJGL_CZZJMX="PRO_PIF_ZJGL_CZZJMX";

    public static final String HS_IFS_FUNC_NO = "HS850002";
    public static final String HS_IFS_USER_ID ="100005";
    public static final String HS_IFS_PASSWORD= "111111";

//    public static final String opStation= "_DD00";

    //IFS接口常量
//    public static final int op_branch_no = 8999;//操作分支机构
//    public static final String operator_no = "38888";//操作员编号
//    public static final String user_type = "1";//用户类别
//    public static final String op_password = "111111";//操作员密码

    public static int op_branch_no;
    public static String operator_no;
    public static String user_type;
    public static String op_password;


//    public void getLocalMac(){this.opStation = ConfigUtil.getString("commonUtils.opStation");}

    public void initCommom(){
        this.op_branch_no = ConfigUtil.getInt("ifsCommom.op_branch_no");
        this.operator_no = ConfigUtil.getString("ifsCommom.operator_no");
        this.user_type = ConfigUtil.getString("ifsCommom.user_type");
        this.op_password = ConfigUtil.getString("ifsCommom.op_password");
    }


}
