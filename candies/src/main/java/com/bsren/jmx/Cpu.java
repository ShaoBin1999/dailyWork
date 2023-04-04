package com.bsren.jmx;

import com.sun.management.OperatingSystemMXBean;

import java.lang.management.ManagementFactory;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Cpu {

    public static void main(String[] args) throws InterruptedException {
        for (int i=0;i<10;i++){
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true){
                        int i = 1;
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            thread.start();
        }
        OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getPlatformMXBean(
                OperatingSystemMXBean.class);
        double load;
        try {
            Method method = java.lang.management.OperatingSystemMXBean.class.getMethod("getSystemLoadAverage", new Class<?>[0]);
            load = (Double) method.invoke(operatingSystemMXBean, new Object[0]);
            if (load == -1) {
                com.sun.management.OperatingSystemMXBean bean =
                        (com.sun.management.OperatingSystemMXBean) operatingSystemMXBean;
                load = bean.getSystemCpuLoad();
            }
        } catch (Throwable e) {
            load = -1;
        }

        Thread.sleep(1000);
        int cpu = operatingSystemMXBean.getAvailableProcessors();
        System.out.println(load);
        System.out.println(cpu);
//        System.out.println(osBean);
//        System.out.println(osBean.getTotalPhysicalMemorySize());
//        //当前服务器空闲内存，单位B
//        System.out.println(osBean.getFreePhysicalMemorySize());
//        //当前CPU使用率，0.86这样的
//        System.out.println(osBean.getProcessCpuLoad());
//        System.out.println(osBean.getSystemCpuLoad());
//        System.out.println(osBean.getSystemLoadAverage());
//        System.out.println(osBean.getAvailableProcessors());
    }
}
