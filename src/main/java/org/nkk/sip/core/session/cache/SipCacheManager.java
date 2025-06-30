package org.nkk.sip.core.session.cache;


import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

public interface SipCacheManager<V> {

    /**
     * 存入缓存数据
     *
     * @param key  关键
     * @param data 数据
     */
    void set(String key, V data);

    /**
     * 得到缓存数据
     *
     * @param key 关键
     */
    V get(String key);

    /**
     * 是否存在Key
     *
     * @param key 关键
     * @return {@link Boolean}
     */
    Boolean existsKey(String key);

    /**
     * 从右边存入数据到list
     *
     * @param key   关键
     * @param value 价值
     */
    void rightPush(String key, V value);

    /**
     * 从右边存入所有
     *
     * @param key  关键
     * @param data 数据
     */
    void rightPushAll(String key, List<V> data);

    /**
     * 从左边存入数据到list
     *
     * @param key   关键
     * @param value 价值
     */
    void leftPush(String key, V value);

    /**
     * 从左边存入所有
     *
     * @param key  关键
     * @param data 数据
     */
    void leftPushAll(String key, List<V> data);

    /**
     * 从右边移除数据
     *
     * @param key 关键
     * @return {@link Object}
     */
    V leftPop(String key);

    /**
     * 从左边移除数据
     *
     * @param key 关键
     * @return {@link Object}
     */
    V rightPop(String key);

    /**
     * 获取指定key里面存的数据的大小
     *
     * @param key 关键
     * @return int
     */
    int size(String key);

    /**
     * 清空指定key的缓存
     *
     * @param key 关键
     */
    void clear(String key);

    /**
     * 清空指定key的缓存
     *
     * @param keys 关键
     */
    void clear(String... keys);

    /**
     * 删除指定前缀的数据
     * @param prefix 前缀
     */
    void delAll(String prefix);


}
