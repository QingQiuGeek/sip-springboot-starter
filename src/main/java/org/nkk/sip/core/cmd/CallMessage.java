package org.nkk.sip.core.cmd;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.util.StrUtil;
import gov.nist.javax.sip.message.SIPRequest;
import gov.nist.javax.sip.message.SIPResponse;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.nkk.common.utils.SipContextHolder;
import org.nkk.sip.config.SipConfig;
import org.nkk.sip.core.service.SipPublisher;
import org.nkk.sip.core.session.impl.InviteManager;
import org.nkk.sip.utils.SipUtil;

import javax.sip.SipProvider;
import javax.sip.header.UserAgentHeader;
import javax.sip.header.ViaHeader;
import javax.sip.message.Message;
import javax.sip.message.Request;
import javax.sip.message.Response;

@Getter
@Slf4j
public class CallMessage {

    private static final TimedCache<String, String> timedCache = CacheUtil.newTimedCache(1000);
    /**
     * 空订阅
     */
    private static final String EMPTY_SUBSCRIBE = "*_*";

    /**
     * 邀请订阅
     */
    private static final String INVITE_SUBSCRIBE = InviteManager.PREFIX;

    private static final SipConfig sc = SipContextHolder.getBean(SipConfig.class);

    /**
     * sip消息
     */
    private final Message message;

    /**
     * 用于处理向摄像机发起请求后的响应结果。
     */
    private String callId = null;


    public CallMessage(Message message) {
        this.message = message;
        if (message instanceof SIPRequest) {
            callId = ((SIPRequest) message).getCallIdHeader().getCallId();
        } else if (message instanceof SIPResponse) {
            callId = ((SIPResponse) message).getCallIdHeader().getCallId();
        } else {
            throw new RuntimeException("[Call-消息类型异常]");
        }
    }

    @SneakyThrows
    public void exec() {
        ViaHeader viaHeader = (ViaHeader) message.getHeader(ViaHeader.NAME);
        String transport = "UDP";
        if (viaHeader == null) {
            log.warn("[消息头缺失]： ViaHeader， 使用默认的UDP方式处理数据");
        } else {
            transport = viaHeader.getTransport();
        }
        if (message.getHeader(UserAgentHeader.NAME) == null) {
            try {
                message.addHeader(SipUtil.createUserAgentHeader());
            } catch (Exception e) {
                log.error("添加UserAgentHeader失败", e);
            }
        }
        SipProvider sipProvider = SipUtil.getSipProvider(transport);
        if (message instanceof Request) {
            sipProvider.sendRequest((Request) message);
        } else if (message instanceof Response) {
            sipProvider.sendResponse((Response) message);
        }
    }

    /**
     *
     * 呼叫等待 `像摄像机发送请求` 的响应结果， 只有2个结果： 成功、和失败。
     * @param subscribeKey 订阅结果的key
     */
    public CallMessage call(String subscribeKey) {
        if (StrUtil.isEmpty(this.callId)) {
            return this;
        }
        // 如果不包含不存
        if(!timedCache.containsKey(this.callId)){
            String str = StrUtil.isEmpty(subscribeKey) ? EMPTY_SUBSCRIBE : subscribeKey;
            timedCache.put(this.callId, str, DateUnit.SECOND.getMillis() * sc.getTimeout());
        }
        return this;
    }

    /**
     * 请求出错了
     *
     * @param errorMsg 错误消息
     */
    public void callError(String errorMsg) {
        if (StrUtil.isEmpty(this.callId)) {
            return;
        }
        String key = timedCache.get(this.callId);
        if (StringUtils.isEmpty(key)) {
            return;
        }
        if (!StrUtil.equals(key, EMPTY_SUBSCRIBE)) {
            SipPublisher.handler(key).ofFail(errorMsg);
        }
        timedCache.remove(this.callId);
        log.error("[请求-{}] 发生错误：{}", this.callId, errorMsg);
    }

    /**
     * 呼叫成功
     *
     */
    public void callOk() {
        if (StrUtil.isEmpty(this.callId)
                || timedCache.get(this.callId) == null ) {
            return;
        }
        String key = timedCache.get(this.callId);
        if (!StringUtils.equals(key, EMPTY_SUBSCRIBE) && key.startsWith(INVITE_SUBSCRIBE)) {
            InviteManager bean = SipContextHolder.getBean(InviteManager.class);
            bean.processInviteOk(key, (SIPResponse) message);
        }

        timedCache.remove(this.callId);
    }




}
