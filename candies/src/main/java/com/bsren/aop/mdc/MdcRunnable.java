package com.bsren.aop.mdc;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.slf4j.MDC;

import java.util.Map;

@Slf4j
public class MdcRunnable implements Runnable{

    public static void main(String[] args) {
        MDC.put("1","2");
        Thread thread = new Thread(new MdcRunnable(new Runnable() {
            @Override
            public void run() {
                for (Map.Entry<String, String> entry : MDC.getCopyOfContextMap().entrySet()) {
                    log.debug(entry.getKey()+" "+entry.getValue());
                }
                log.debug("log in other thread");
            }
        }));
        thread.start();
    }




    private Runnable runnable;

    private final Map<String,String> mainMdcMap;

    public MdcRunnable(Runnable runnable) {
        this.runnable = runnable;
        this.mainMdcMap = MDC.getCopyOfContextMap();
    }


    @Override
    public void run() {
        if(MapUtils.isEmpty(mainMdcMap)){
            runnable.run();
            return;
        }
        for (Map.Entry<String, String> entry : mainMdcMap.entrySet()) {
            MDC.put(entry.getKey(),entry.getValue());
        }
        try {
            runnable.run();
        }finally {
            for (Map.Entry<String, String> entry : mainMdcMap.entrySet()) {
                MDC.remove(entry.getKey());
            }
        }


    }
}
