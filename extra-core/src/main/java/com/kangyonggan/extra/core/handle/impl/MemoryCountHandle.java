package com.kangyonggan.extra.core.handle.impl;

import com.kangyonggan.extra.core.handle.CountHandle;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * @author kangyonggan
 * @since 11/6/17
 */
public class MemoryCountHandle implements CountHandle {

    private static Map<String, LinkedBlockingDeque<Long>> map = new HashMap();
    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public boolean limit(String key, long interval, int count, boolean interrupt) {
        boolean isLimited = isLimited(key, interval, count);
        if (isLimited) {
            System.out.println(String.format("[%s] Method %s during %dms was called %d times.interrupt is %s, next code will be %s", format.format(new Date()), key, interval, count, interrupt, interrupt ? "interrupt" : "continue"));
        }

        return isLimited;
    }

    private synchronized boolean isLimited(String key, Long interval, int count) {
        LinkedBlockingDeque<Long> queue = getQueue(key, count);

        // when queue is full，we need deal
        if (queue.size() >= count) {
            try {
                // take first
                Long first = queue.takeFirst();
                // add to last
                queue.addLast(System.currentTimeMillis());

                if (interval > queue.getLast() - first) {
                    return true;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            queue.addLast(System.currentTimeMillis());
        }

        return false;
    }

    private LinkedBlockingDeque<Long> getQueue(String key, int count) {
        LinkedBlockingDeque<Long> queue = map.get(key);
        if (queue == null) {
            queue = new LinkedBlockingDeque(count);
            map.put(key, queue);
        }

        return queue;
    }

}
