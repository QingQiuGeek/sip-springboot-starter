package org.nkk.sip.core.session.cache.impl;

import java.util.concurrent.ConcurrentLinkedDeque;
import org.apache.commons.lang3.StringUtils;
import org.nkk.sip.core.session.cache.SipCacheManager;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 懒大王Smile
 * 把 private final Map<String, Linked<V>> CACHE = new ConcurrentHashMap<>(); 改为
 *  private final Map<String, ConcurrentLinkedDeque<V>> CACHE = new ConcurrentHashMap<>();支持并发
 * @param <V>
 */
public class DefaultSipCacheManger<V> implements SipCacheManager<V> {

    private final Map<String, ConcurrentLinkedDeque<V>> CACHE = new ConcurrentHashMap<>();

    @Override
    public void set(String key, V data) {
        ConcurrentLinkedDeque<V> list = CACHE.computeIfAbsent(key, k -> new ConcurrentLinkedDeque<>());
        list.addLast(data);
    }

    @Override
    public V get(String key) {
        ConcurrentLinkedDeque<V> list = CACHE.get(key);
        return list != null && !list.isEmpty() ? list.getFirst() : null;
    }

    @Override
    public Boolean existsKey(String key) {
        return CACHE.containsKey(key);
    }

    @Override
    public void rightPush(String key, V value) {
        ConcurrentLinkedDeque<V> list = CACHE.computeIfAbsent(key, k -> new ConcurrentLinkedDeque<>());
        list.addLast(value);
    }

    @Override
    public void rightPushAll(String key, List<V> data) {
        CACHE.put(key, new ConcurrentLinkedDeque<>(data));
    }

    @Override
    public void leftPush(String key, V value) {
        ConcurrentLinkedDeque<V> list = CACHE.computeIfAbsent(key, k -> new ConcurrentLinkedDeque<>());
        list.addFirst(value);
    }

    @Override
    public void leftPushAll(String key, List<V> data) {
        CACHE.put(key, new ConcurrentLinkedDeque<>(data));
    }


    @Override
    public V leftPop(String key) {
        ConcurrentLinkedDeque<V> list = CACHE.get(key);
        return list != null && !list.isEmpty() ? list.removeFirst() : null;
    }

    @Override
    public V rightPop(String key) {
        ConcurrentLinkedDeque<V> list = CACHE.get(key);
        return list != null && !list.isEmpty() ? list.removeLast() : null;
    }

    @Override
    public int size(String key) {
        ConcurrentLinkedDeque<V> list = CACHE.get(key);
        return list.size();
    }

    @Override
    public void clear(String key) {
        CACHE.remove(key);
    }

    @Override
    public void clear(String... keys) {
        for (String key : keys) {
            CACHE.remove(key);
        }
    }

    @Override
    public void delAll(String prefix) {
        CACHE.forEach((key, value) -> {
            if (StringUtils.startsWithIgnoreCase(key, prefix)) {
                CACHE.remove(key);
            }
        });
    }
}
