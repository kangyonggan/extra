package com.kangyonggan.extra.model;

import java.util.Date;

/**
 * Cache entity
 *
 * @author kangyonggan
 * @since 11/1/17
 */
public class CacheItem {

    /**
     * Cache value
     */
    private Object value;

    /**
     * Cache expire time
     */
    private Long expire;

    /**
     * Cache update time
     */
    private Date updateDate;

    public CacheItem() {
    }

    public CacheItem(Object value, Long expire) {
        this.value = value;
        this.expire = expire;
        this.updateDate = new Date();
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Long getExpire() {
        return expire;
    }

    public void setExpire(Long expire) {
        this.expire = expire;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    @Override
    public String toString() {
        return "CacheItem{" +
                "value=" + value +
                ", expire=" + expire +
                ", updateDate=" + updateDate +
                '}';
    }

    /**
     * @return
     */
    public boolean isExpire() {
        if (expire == -1) {
            return false;
        }

        if (new Date().getTime() < updateDate.getTime() + expire) {
            return false;
        }

        return true;
    }
}
