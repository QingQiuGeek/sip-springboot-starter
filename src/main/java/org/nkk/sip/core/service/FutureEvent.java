package org.nkk.sip.core.service;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.nkk.common.utils.SipContextHolder;
import org.nkk.sip.beans.enums.SipEnum;
import org.nkk.sip.beans.model.base.Message;
import org.nkk.sip.core.session.impl.InviteManager;
import org.nkk.sip.core.session.impl.ProxyManager;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

/**
 * 未来事件
 *
 * @author nkk
 * @date 2024/07/03
 */

@Slf4j
public class FutureEvent<T> {

    private final CompletableFuture<Message<?>> future;


    public FutureEvent(CompletableFuture<Message<?>> future) {
        this.future = future;
    }

    /**
     * 成功
     *
     * @param data 数据
     */
    public void onSuccess(Consumer<T> data) {
        future.thenAccept(message -> {
            Message.EnumState code = message.getCode();
            if (code == Message.EnumState.OK) {
                data.accept((T) message.getData());
            }
            log.info("【异步事件】OK");
        });
    }

    /**
     * 错误
     *
     * @param e 数据
     */
    public void onError(Consumer<Throwable> e) {
        future.thenAccept(message -> {
            Message.EnumState code = message.getCode();
            if (code == Message.EnumState.Error) {
                e.accept(new RuntimeException(message.getMsg()));
                this.releaseInviteData(message.getKey());
                log.info("【异步事件】error");
            }
        });
    }

    /**
     * 超时
     *
     * @param data 数据
     */
    public void onTimeOut(Consumer<String> data) {
        future.thenAccept(message -> {
            Message.EnumState code = message.getCode();
            if (code == Message.EnumState.timeOut) {
                data.accept(message.getKey());
                this.releaseInviteData(message.getKey());
                log.info("【异步事件】timeOut");
            }
        });
    }


    /**
     * 释放缓存数据
     *
     * @param key 关键
     */
    private void releaseInviteData(String key) {
        if (key.startsWith(InviteManager.PREFIX)) {
            InviteManager inviteManager = SipContextHolder.getBean(InviteManager.class);
            inviteManager.releaseData(key);
        } else if (key.startsWith(ProxyManager.PREFIX)) {
            ProxyManager proxyManager = SipContextHolder.getBean(ProxyManager.class);
            proxyManager.releaseData(key);
        }
    }

    /**
     * 获取结果
     *
     * @return {@link Message}<{@link T}>
     */
    public Message<T> get() {
        Message<?> message;
        try {
            message = future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e.getMessage());
        }
        Message.EnumState code = message.getCode();
        if (code == Message.EnumState.OK) {
            return (Message<T>) message;
        }
        log.info("获取结果：{}", JSON.toJSONString(message));
        this.releaseInviteData(message.getKey());
        throw new RuntimeException(message.getMsg());
    }
}
