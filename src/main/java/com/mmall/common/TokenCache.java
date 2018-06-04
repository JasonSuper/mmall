package com.mmall.common;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @Author Jason
 * Create in 2018-06-04 13:17
 */
public class TokenCache {

    private static Logger logger = LoggerFactory.getLogger(TokenCache.class);

    // LRU算法
    private static LoadingCache<String, String> localCache = CacheBuilder.newBuilder()
            .initialCapacity(1000)  // 初始容量
            .maximumSize(10000) // 最大容量
            .expireAfterAccess(12, TimeUnit.HOURS)  //  过期时间
            .build(new CacheLoader<String, String>() {

                // 默认数据加载实现，当get取值的时候，如果没有对应的key值，则调用load()方法进行加载
                @Override
                public String load(String s) throws Exception {
                    return "null";
                }
            });

    // 设置缓存
    public static void setKey(String key, String value) {
        localCache.put(key, value);
    }

    // 获取缓存
    public static String getKey(String key) {
        String value = null;
        try {
            value = localCache.get(key);
            if (value.equals("null")) {
                return null;
            }
            return value;
        } catch (Exception e) {
            logger.error("localCache get error", e);
        }
        return null;
    }


}
