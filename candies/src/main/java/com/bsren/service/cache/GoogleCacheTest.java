package com.bsren.service.cache;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.util.concurrent.TimeUnit;

//TODO 学习缓存的创建过程
public class GoogleCacheTest {

    public static void main(String[] args) throws InterruptedException {
        Cache<String, Object> cache = CacheBuilder.newBuilder()
                .expireAfterWrite(5, TimeUnit.SECONDS)
                .build();
        cache.put("1","2");
        System.out.println(cache.getIfPresent("1"));
        TimeUnit.SECONDS.sleep(6);
        System.out.println(cache.getIfPresent("1"));
    }
}
