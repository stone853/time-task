package com.apex.bss.cjsc.taskn;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Created by Saphurot on 2017/5/5.
 */
public class TimeoutTaskUtils {
    /**
     * 执行一个有时间限制的任务
     *
     * @param task    待执行的任务
     * @param seconds 超时时间(单位: 秒)
     * @return
     */
    public static Map execute(Callable<Map> task, int seconds) {
        Map result = new HashMap();
        ExecutorService threadPool = Executors.newCachedThreadPool();

        try {
            Future<Map> future = threadPool.submit(task);
            result = future.get(seconds, TimeUnit.SECONDS);
        } catch (Exception e) {
            result = null;
        } finally {
            threadPool.shutdownNow();
        }

        return result;
    }
}
