package com.apex.bss.cjsc.base;

/**
 * Created by jinsh on 2017/1/23.
 */
public class TestThread extends Thread {
    public TestThread(String name){
        super(name);
    }
    @Override
    public void run() {
        for(int i =0;i<4;i++){
            System.out.println(this.getName() + "laile");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    public static void main(String args[]){
        System.out.println(35.00/100.00  * 100 );
    }
}
