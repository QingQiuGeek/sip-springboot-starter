package org.nkk.sip.core.session.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.nkk.common.utils.CacheKeyUtil;
import org.nkk.media.hook.custom.MediaPlayInfo;
import org.nkk.media.hook.param.OnStreamChangedHookParam;
import org.nkk.sip.beans.model.base.ToDevice;
import org.nkk.sip.beans.model.invite.InviteStream;
import org.nkk.sip.beans.model.proxy.ProxyStream;
import org.nkk.sip.config.SipConfig;
import org.nkk.sip.core.service.SipPublisher;
import org.nkk.sip.core.session.cache.SipCacheManager;

import javax.annotation.Resource;
import java.util.Objects;

@Slf4j
public class ProxyManager {

    public static final String PREFIX = "PROXY";
    @Resource
    private SipCacheManager<ProxyStream> sipCacheManager;

    @Resource
    private SipConfig sipConfig;

    public static String getKey(String streamId) {
        return CacheKeyUtil.getKey(PREFIX, streamId);
    }

    /**
     * 获取
     *
     * @param streamId    流id
     * @param proxyStream 代理流
     */
    public void setData(String streamId, ProxyStream proxyStream) {
        String key = getKey(streamId);
        proxyStream.setSubscribeKey(key);
        sipCacheManager.set(key, proxyStream);
    }


    /**
     * 得到
     *
     * @param streamId 流id
     */
    public ProxyStream getData(String streamId) {
        return sipCacheManager.get(getKey(streamId));
    }

    /**
     * 过程好了
     *
     * @param streamId 流id
     * @param param    流信息
     */
    public void processOk(String streamId, OnStreamChangedHookParam param) {
        ProxyStream data = getData(streamId);
        if (Objects.nonNull(data)) {
            // 媒体信息
            MediaPlayInfo.PlayUrl playUrl = new MediaPlayInfo.PlayUrl();
            playUrl.setHls(this.setPath(param, "/hls.m3u8"));
            playUrl.setFlv(this.setPath(param, ".live.flv"));
            playUrl.setFmp4(this.setPath(param, ".live.mp4"));
            data.getPlayInfo().setPlayUrl(playUrl);
            sipCacheManager.set(getKey(streamId), data);
            SipPublisher.handler(data.getSubscribeKey()).ofOk(data.getPlayInfo());
        }
    }

    private String setPath(OnStreamChangedHookParam param, String suffix) {
        SipConfig.ZlmMedia media = sipConfig.getMedia();
        return media.getHost() + "/" + StringUtils.joinWith("/", param.getApp(), param.getStream()) + suffix;
    }

    /**
     * 移除
     *
     * @param streamId 流id
     */
    public void delData(String streamId) {
        sipCacheManager.clear(getKey(streamId));
    }

    /**
     * 释放数据
     *
     * @param key 缓存的key
     */
    public void releaseData(String key) {
        sipCacheManager.clear(key);
    }

    /**
     * 更改录制状态
     */
    public void processRecord(String streamId, boolean record) {
        ProxyStream proxy = this.getData(streamId);
        if (Objects.nonNull(proxy)) {
            proxy.setEnableMp4Record(record);
            ToDevice toDevice = proxy.getToDevice();
            toDevice.setEnableRecord(record);
            sipCacheManager.set(getKey(streamId), proxy);
        }
    }

    /**
     * 删除代理拉流数据
     */
    public void removeAll() {
        sipCacheManager.delAll(PREFIX);
    }
}
