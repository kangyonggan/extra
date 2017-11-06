package com.kangyonggan.extra.core.help;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * @author kangyonggan
 * @since 11/6/17
 */
public class LimitCountHelp {

    private static Map<String, LinkedBlockingDeque<Long>> map = new HashMap();

    public static boolean isLimited(String key, Long interval, int count) {
        LinkedBlockingDeque<Long> queue = LimitCountHelp.getQueue(key, count);

        // if queue is full, remove first
        if (queue.size() >= count) {
            queue.removeFirst();
        }

        // add to last
        queue.addLast(System.currentTimeMillis());

        // when queue is full，we need deal
        if (queue.size() >= count) {
            if (interval > queue.getFirst() - queue.getFirst()) {
                return true;
            }
        }

        return false;
    }

    public static LinkedBlockingDeque<Long> getQueue(String key, int count) {
        LinkedBlockingDeque<Long> queue = map.get(key);
        if (queue == null) {
            synchronized (map) {
                if (queue == null) {
                    queue = new LinkedBlockingDeque(count);
                    map.put(key, queue);
                }
            }
        }

        return queue;
    }


}
