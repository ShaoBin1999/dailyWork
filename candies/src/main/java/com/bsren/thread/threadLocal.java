package com.bsren.thread;
import org.apache.dubbo.common.threadlocal.*;
public class threadLocal {

    private static InternalThreadLocal<Integer> map0 = new InternalThreadLocal<>();
    private static InternalThreadLocal<Integer> map1 = new InternalThreadLocal<>();
    private static InternalThreadLocal<Integer> map2 = new InternalThreadLocal<>();
    private static InternalThreadLocal<Integer> map3 = new InternalThreadLocal<>();
    private static InternalThreadLocal<Integer> map4 = new InternalThreadLocal<>();
    public static void main(String[] args) {
        new InternalThread(new InternalRunnable(new Runnable() {
            @Override
            public void run() {
                for (int i=0;i<5;i++){
                    map0.set(i);
                    map1.set(i);
                    map2.set(i);
                    map3.set(i);
                    map4.set(i);
                }
            }
        })).start();
    }

}
