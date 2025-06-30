package org.nkk.sip.core.session.impl;

import com.alibaba.fastjson2.JSON;
import gov.nist.javax.sip.message.SIPResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.nkk.common.utils.CacheKeyUtil;
import org.nkk.media.api.ZlmRestTemplate;
import org.nkk.media.beans.entity.rtp.CloseRtpServerResult;
import org.nkk.media.hook.custom.MediaPlayInfo;
import org.nkk.media.hook.param.OnPublishHookParam;
import org.nkk.media.utils.SsrcUtil;
import org.nkk.sip.beans.enums.SipEnum;
import org.nkk.sip.beans.model.base.ToDevice;
import org.nkk.sip.beans.model.device.Dto.SipTransactionInfo;
import org.nkk.sip.beans.model.invite.InviteStream;
import org.nkk.sip.config.SipConfig;
import org.nkk.sip.core.builder.SipBuilder;
import org.nkk.sip.core.service.SipPublisher;
import org.nkk.sip.core.session.cache.SipCacheManager;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * 邀请管理器
 *
 * @author nkk
 * @date 2024/07/23
 */
@Slf4j
public class InviteManager {

    public static final String PREFIX = SipEnum.Method.INVITE.name();

    @Resource
    private SipCacheManager<InviteStream> sipCacheManager;

    @Resource
    private SipConfig sipConfig;

    @Resource
    private ZlmRestTemplate zlmRestTemplate;


    public static String getKey(String streamId) {
        return CacheKeyUtil.getKey(PREFIX, streamId);
    }

    /**
     * 邀请
     * 设备邀请
     *
     * @param streamId 流ID
     */
    public void setData(String streamId, InviteStream inviteStream) {
        String key = getKey(streamId);
        inviteStream.setSubscribeKey(key);
        sipCacheManager.set(key, inviteStream);
    }

    public InviteStream getData(String streamId) {
        return sipCacheManager.get(getKey(streamId));
    }


    /**
     * 身份验证
     *
     * @param param 流id
     */
    public void processAuth(OnPublishHookParam param) {
        String streamId = param.getStream();
        InviteStream invite = this.getData(streamId);
        // 未收到点播邀请成功的处理
        if(Objects.isNull(invite.getTransactionInfo())){
            SipPublisher.handler(invite.getSubscribeKey()).ofFail("未收到设备的响应");
            return;
        }

        invite.setSchema(param.getSchema());
        // 媒体信息
        MediaPlayInfo mediaPlayInfo = new MediaPlayInfo();
        MediaPlayInfo.PlayUrl playUrl = new MediaPlayInfo.PlayUrl();
        playUrl.setHls(this.setPath(param, "/hls.m3u8"));
        playUrl.setFlv(this.setPath(param, ".live.flv"));
        playUrl.setFmp4(this.setPath(param, ".live.mp4"));
        mediaPlayInfo.setPlayUrl(playUrl);
        mediaPlayInfo.setApp(param.getApp());
        mediaPlayInfo.setStreamId(param.getStream());
        invite.setPlayInfo(mediaPlayInfo);

        sipCacheManager.set(CacheKeyUtil.getKey(PREFIX, streamId), invite);
    }

    private String setPath(OnPublishHookParam param, String suffix) {
        SipConfig.ZlmMedia media = sipConfig.getMedia();
        return media.getHost() + "/" + StringUtils.joinWith("/", param.getApp(), param.getStream()) + suffix;
    }


    /**
     * 删除邀请
     *
     * @param streamId 流id
     */
    public void delData(String streamId) {
        sipCacheManager.clear(getKey(streamId));
    }

    /**
     * 释放数据并情况缓存
     *
     * @param key 缓存的key
     */
    public void releaseData(String key) {
        InviteStream inviteStream = sipCacheManager.get(key);
        if (Objects.nonNull(inviteStream)) {
            ToDevice toDevice = inviteStream.getToDevice();
            SipTransactionInfo transactionInfo = inviteStream.getTransactionInfo();
            if(Objects.nonNull(transactionInfo)){
                SipBuilder.buildRequest(toDevice)
                        .ofByeRequest(transactionInfo)
                        .execute();
            }

            CloseRtpServerResult closeResponse = zlmRestTemplate.closeRtpServer(inviteStream.getStreamId());
            log.info("[释放资源] 关闭rtp:{}", JSON.toJSONString(closeResponse));
            SsrcUtil.releaseSsrc(inviteStream.getSsrc());
            log.info("[释放资源] 釋放Ssrc");
            sipCacheManager.clear(key);
        }
        log.info("[释放资源] {}釋放完成", key);
    }

    /**
     * register流
     *
     * @param streamId 流
     */
    public void processOk(String streamId) {
        InviteStream invite = this.getData(streamId);
        if (Objects.nonNull(invite)) {
            SipPublisher.handler(invite.getSubscribeKey()).ofOk(invite.getPlayInfo());
        }
    }

    /**
     * 邀请ok
     *
     * @param key         关键
     * @param sipResponse sip响应
     */
    public void processInviteOk(String key, SIPResponse sipResponse) {
        InviteStream inviteStream = sipCacheManager.get(key);
        log.info("[invite]成功,等待推流鉴权...");
        if (Objects.nonNull(inviteStream)) {
            inviteStream.setTransactionInfo(new SipTransactionInfo(sipResponse));
            sipCacheManager.set(key, inviteStream);
        }
    }


    /**
     * 更改录制状态
     */
    public void processRecord(String streamId, boolean record) {
        InviteStream invite = this.getData(streamId);
        if (Objects.nonNull(invite)) {
            invite.setEnableMp4Record(record);
            ToDevice toDevice = invite.getToDevice();
            toDevice.setEnableRecord(record);
            sipCacheManager.set(getKey(streamId), invite);
        }
    }

    /**
     * 删除点播邀请的数据
     */
    public void removeAll(){
        sipCacheManager.delAll(PREFIX);
    }
}
