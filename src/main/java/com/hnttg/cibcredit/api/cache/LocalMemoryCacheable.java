package com.hnttg.cibcredit.api.cache;

import java.io.Serializable;

public class LocalMemoryCacheable implements Serializable {

    private Object object;
    private Long expiryTime;

    public LocalMemoryCacheable() {
    }

    public LocalMemoryCacheable(Object object) {
        this.object = object;
    }

    public LocalMemoryCacheable(Object object, Long expiryTime) {
        this.object = object;
        this.expiryTime = expiryTime;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public Long getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(Long expiryTime) {
        this.expiryTime = expiryTime;
    }

}
