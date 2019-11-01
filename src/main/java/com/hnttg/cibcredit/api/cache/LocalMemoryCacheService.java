package com.hnttg.cibcredit.api.cache;

import org.apache.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LocalMemoryCacheService {

    private static final Logger logger = Logger.getLogger(LocalMemoryCacheService.class);
    private final Map<String, LocalMemoryCacheable> cacheMap = new ConcurrentHashMap();
    private static final Long EXPIRY_IN_MILLS = 1800000L;

    public LocalMemoryCacheService() {
    }

    public void put(String key, Object value) {
        this.put(key, value, EXPIRY_IN_MILLS);
    }

    public void put(String key, Object value, Long expiryInMills) {
        if (key != null) {
            if (expiryInMills != null && expiryInMills >= 0L) {
                LocalMemoryCacheable cacheable = new LocalMemoryCacheable(value);
                if (expiryInMills > 0L) {
                    cacheable.setExpiryTime(System.currentTimeMillis() + expiryInMills);
                } else {
                    cacheable.setExpiryTime(9223372036854775807L);
                }

                this.cacheMap.put(key, cacheable);
            }
        }
    }

    public Object get(String key) {
        LocalMemoryCacheable cacheable = (LocalMemoryCacheable)this.cacheMap.get(key);
        if (cacheable == null) {
            return null;
        } else if (System.currentTimeMillis() > cacheable.getExpiryTime()) {
            this.delete(key);
            return null;
        } else {
            return cacheable.getObject();
        }
    }

    public <T> T get(String key, Class<T> clazz) {
        Object obj = this.get(key);
        if (obj == null) {
            return null;
        } else {
            return clazz.isInstance(obj) ? (T)obj : null;
        }
    }

    public void delete(String key) {
        if (key != null) {
            this.cacheMap.remove(key);
        }
    }

    public boolean hasKey(String key) {
        return key == null ? false : this.cacheMap.containsKey(key);
    }

    public int keySize() {
        return this.cacheMap.size();
    }

    @Scheduled(
            cron = "0 30 * * * ?"
    )
    public void doCleaning() {
        int cleaned = 0;
        Iterator var2 = this.cacheMap.entrySet().iterator();

        while(var2.hasNext()) {
            Map.Entry<String, LocalMemoryCacheable> entry = (Map.Entry)var2.next();
            LocalMemoryCacheable cacheable = (LocalMemoryCacheable)entry.getValue();
            if (cacheable != null && System.currentTimeMillis() > cacheable.getExpiryTime()) {
                this.cacheMap.remove(entry.getKey());
                ++cleaned;
            }
        }

        if (cleaned > 0) {
            logger.warn("<<< Total " + cleaned + " Expired Keys Cleaned, Remain " + this.cacheMap.size() + " Keys In Memory");
        }

    }
}
