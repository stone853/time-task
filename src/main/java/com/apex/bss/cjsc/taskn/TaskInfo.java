package com.apex.bss.cjsc.taskn;

import com.apex.bss.cjsc.redis.RedisUtil;

import java.util.Set;

/**
 * Created by jinsh on 2017/3/15.
 * 用来获取当前任务信息
 */
public class TaskInfo {
    public static boolean redemIsDone = false;
    public static boolean redemIsDone_SD = false;
    public static boolean fundinIsDone = false;
    public static boolean fundinIsDone_SD = false;
    public static boolean autoapplyIsDone = false;
    public static boolean autoapplyIsDone_SD = false;
    public static boolean purchaseIsDone = false;
    public static boolean purchaseIsDone_SD = false;
    public static boolean transIsDone = false;
    public static boolean transIsDone_SD = false;


    public static int redemRetryTimes = 0;
    public static int fundinRetryTimes = 0;
    public static int autoapplyRetryTimes = 0;
    public static int purchaseRetryTimes = 0;
    public static int transRetryTimes = 0;

    public static String autoOrSD = "1";// 1为自动 0为手动
}
