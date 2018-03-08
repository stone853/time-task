package com.apex.bss.cjsc.timer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by jinsh on 2017/1/27.
 */
public class AutoApplyStart {
    private void start(){
        ThreadPoolExecutor timerTaskThreadPool = TimerTaskThreadPool.getThreadPool();
        List<String> list = new ArrayList<>();
        list.add("aaa");
        list.add("bbb");
        list.add("ccc");
        list.add("ddd");
        list.add("eee");
        list.add("fff");
        list.add("ggg");
        list.add("hhh");
        list.add("iii");
        for(int i =0;i<list.size();i++){
            timerTaskThreadPool.execute(new AutoApplyTask(list.get(i)));
        }

    }
}
