package org.nkk.sip.core.service;

import lombok.extern.slf4j.Slf4j;
import org.nkk.common.utils.SipContextHolder;
import org.nkk.sip.beans.model.base.Message;
import org.nkk.sip.config.SipConfig;
import org.nkk.sip.core.session.impl.InviteManager;
import org.nkk.sip.core.session.impl.ProxyManager;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;

/**
 * 消息发布者
 *
 * @author nkk
 * @date 2024/07/03
 */

@Slf4j
public class SipPublisher {
    private static final Map<String, CompletableFuture<Message<?>>> subscriptionTimers = new ConcurrentHashMap<>();

    private static final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(12);

    private static final SipConfig sc = SipContextHolder.getBean(SipConfig.class);

    /**
     * 订阅key，默认10s过期
     *
     * @param key key值
     * @return {@link SipPublisherBuilder}
     */
    public static SipPublisherBuilder subscribe(String key) {
        return new SipPublisherBuilder(key, sc.getTimeout());
    }

    public static SipPublisherBuilder subscribeInvite(String key) {
        return new SipPublisherBuilder(InviteManager.getKey(key), sc.getTimeout());
    }


    public static SipPublisherBuilder subscribeProxy(String key) {
        return new SipPublisherBuilder(ProxyManager.getKey(key), sc.getTimeout());
    }


    /**
     * 订阅
     *
     * @param key     key值
     * @param expired 过期时间（秒）
     */
    public static SipPublisherBuilder subscribe(String key, long expired) {
        return new SipPublisherBuilder(key, expired);
    }


    /**
     * 取消订阅
     *
     * @param key 关键
     */
    public static void unSubscribe(String key) {
        subscriptionTimers.remove(key);
    }

    /**
     * 处理程序
     *
     * @param key 关键
     * @return {@link SipPublisherHandler}
     */
    public static SipPublisherHandler handler(String key) {
        return new SipPublisherHandler(key);
    }

    public static SipPublisherHandler handlerInvite(String streamId) {
        return new SipPublisherHandler(InviteManager.getKey(streamId));
    }

    public static SipPublisherHandler handlerProxy(String streamId) {
        return new SipPublisherHandler(ProxyManager.getKey(streamId));
    }

    public static class SipPublisherHandler {

        private final String key;
        private final CompletableFuture<Message<?>> future;

        /**
         * 构造处理器
         *
         * @param key 订阅的Key
         */
        public SipPublisherHandler(String key) {
            this.key = key;
            CompletableFuture<Message<?>> future = subscriptionTimers.get(key);
            if (Objects.isNull(future)) {
                future = new CompletableFuture<>();
                Message<Object> message = new Message<>(key).msg("未获取到处理程序").handlerError();
                future.complete(message);
            }
            this.future = future;
        }

        /**
         * 处理好了
         */
        public <T> void ofOk(T data) {
            Message<T> message = new Message<T>(this.key).data(data).handlerOk();
            future.complete(message);
            SipPublisher.unSubscribe(this.key);
        }

        public <T> void ofThen(T data) {
            Message<T> message = new Message<T>(this.key).data(data).handlerOk();
            future.complete(message);
            SipPublisher.unSubscribe(this.key);
        }

        /**
         * 处理程序失败
         */
        public void ofFail(String msg) {
            Message<Object> message = new Message<>(this.key).msg(msg).handlerError();
            log.info("出错了：{}", msg);
            future.complete(message);
            SipPublisher.unSubscribe(this.key);
        }

        /**
         * 处理程序超时
         */
        public void ofTimeOut(String msg) {
            Message<Object> message = new Message<>(this.key).msg(msg).handlerTimeOut();
            future.complete(message);
            SipPublisher.unSubscribe(this.key);
        }
    }

    /**
     * 构造器
     */
    public static class SipPublisherBuilder {
        /**
         * 订阅Key
         */
        private final String subscribeKey;


        private SipPublisherBuilder(String key, long expired) {
            this.subscribeKey = key;
            CompletableFuture<Message<?>> resultFuture = completeOnTimeout(key, expired, TimeUnit.SECONDS);
            subscriptionTimers.put(key, resultFuture);
        }

        /**
         * 超时完成
         *
         * @param key     唯一值
         * @param timeout 超时
         * @param unit    单位
         * @return {@link CompletableFuture}<{@link Message}>
         */
        private CompletableFuture<Message<?>> completeOnTimeout(String key, long timeout, TimeUnit unit) {
            final CompletableFuture<Message<?>> promise = new CompletableFuture<>();
            executorService.schedule(() -> {
                if (!promise.isDone()) {
                    Message<?> message = new Message<>(key).handlerTimeOut();
                    promise.complete(message);
                }
            }, timeout, unit);
            return promise;
        }

        /**
         * 构建future
         *
         * @return {@link FutureEvent}
         */
        public FutureEvent<?> build() {
            CompletableFuture<Message<?>> future = subscriptionTimers.get(this.subscribeKey);
            return new FutureEvent<Message<?>>(future);
        }
    }


}
