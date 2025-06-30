package org.nkk.sip.core.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import gov.nist.javax.sip.message.SIPRequest;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.nkk.media.api.ZlmRestTemplate;
import org.nkk.media.beans.entity.ServerResponse;
import org.nkk.media.beans.entity.StreamKey;
import org.nkk.media.beans.entity.StreamProxyItem;
import org.nkk.media.beans.entity.req.MediaReq;
import org.nkk.media.beans.entity.req.RecordReq;
import org.nkk.media.beans.entity.rtp.OpenRtpServerReq;
import org.nkk.media.beans.entity.rtp.OpenRtpServerResult;
import org.nkk.media.beans.entity.rtp.RtpInfoResult;
import org.nkk.media.hook.custom.MediaPlayInfo;
import org.nkk.media.utils.SsrcUtil;
import org.nkk.sip.beans.constants.SipConstant;
import org.nkk.sip.beans.enums.SipEnum;
import org.nkk.sip.beans.model.base.DeviceQuery;
import org.nkk.sip.beans.model.base.ToDevice;
import org.nkk.sip.beans.model.device.Dto.SipTransactionInfo;
import org.nkk.sip.beans.model.device.Req.DeviceRecordInfoQuery;
import org.nkk.sip.beans.model.device.Resp.DeviceCatalog;
import org.nkk.sip.beans.model.device.Resp.DeviceInfo;
import org.nkk.sip.beans.model.device.Resp.DeviceRecordList;
import org.nkk.sip.beans.model.device.Resp.DeviceStatus;
import org.nkk.sip.beans.model.invite.InviteStream;
import org.nkk.sip.beans.model.proxy.ProxyStream;
import org.nkk.sip.config.SipConfig;
import org.nkk.sip.core.builder.SipBuilder;
import org.nkk.sip.core.sdp.GB28181SDPBuilder;
import org.nkk.sip.core.sdp.media.MediaStreamMode;
import org.nkk.sip.core.session.impl.InviteManager;
import org.nkk.sip.core.session.impl.ProxyManager;
import org.nkk.sip.utils.SipUtil;
import org.nkk.sip.utils.XmlUtils;

import javax.annotation.Resource;
import javax.sip.message.Request;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * sip消息模板
 *
 * @author nkk
 * @date 2024/07/02
 */
@Slf4j
@SuppressWarnings("all")
public class SipMessageTemplate {

    @Resource
    private ZlmRestTemplate zlmRestTemplate;

    @Resource
    private SipConfig sipConfig;

    @Resource
    private InviteManager inviteManager;

    @Resource
    private ProxyManager proxyManager;

    /**
     * 获取设备信息
     *
     * @param toDevice 设备
     * @return {@link FutureEvent}
     */
    @SneakyThrows
    public FutureEvent<DeviceInfo> getDeviceInfo(ToDevice toDevice) {

        DeviceQuery xmlQuery = DeviceQuery.builder()
                .deviceId(toDevice.getDeviceId())
                .sn(SipUtil.generateSn())
                .cmdType(SipEnum.Cmd.DeviceInfo.name())
                .build();
        byte[] xmlData = XmlUtils.toByteXml(xmlQuery, SipConstant.CHARSET);

        return SipBuilder
                .buildRequest(toDevice)
                .ofMessageRequest(xmlData)
                .subscribeResult(SipUtil.genSubscribeKey(xmlQuery.getCmdType(), xmlQuery.getDeviceId(), xmlQuery.getSn()))
                .executeAsync();
    }


    /**
     * 获取设备状态
     *
     * @param toDevice 设备
     * @return {@link FutureEvent}
     */
    @SneakyThrows
    public FutureEvent<DeviceStatus> getDeviceStatus(ToDevice toDevice) {

        DeviceQuery xmlQuery = DeviceQuery.builder()
                .deviceId(toDevice.getDeviceId())
                .sn(SipUtil.generateSn())
                .cmdType(SipEnum.Cmd.DeviceStatus.name())
                .build();
        byte[] xmlData = XmlUtils.toByteXml(xmlQuery, SipConstant.CHARSET);

        return SipBuilder
                .buildRequest(toDevice)
                .ofMessageRequest(xmlData)
                .subscribeResult(SipUtil.genSubscribeKey(xmlQuery.getCmdType(), xmlQuery.getDeviceId(), xmlQuery.getSn()))
                .executeAsync();

    }

    /**
     * 获取设备目录
     *
     * @param toDevice 设备
     * @return {@link FutureEvent}
     */
    @SneakyThrows
    public FutureEvent<DeviceCatalog> getDeviceCatalog(ToDevice toDevice) {

        DeviceQuery xmlQuery = DeviceQuery.builder()
                .deviceId(toDevice.getDeviceId())
                .sn(SipUtil.generateSn())
                .cmdType(SipEnum.Cmd.Catalog.name())
                .build();
        byte[] xmlData = XmlUtils.toByteXml(xmlQuery, SipConstant.CHARSET);

        return SipBuilder
                .buildRequest(toDevice)
                .ofMessageRequest(xmlData)
                .subscribeResult(SipUtil.genSubscribeKey(xmlQuery.getCmdType(), xmlQuery.getDeviceId(), xmlQuery.getSn()))
                .executeAsync();
    }

    /**
     * 获取记录信息
     * 获取设备录制记录
     *
     * @param beginTime 开始时间
     * @param endTime   结束时间
     * @param toDevice  设备
     * @return {@link FutureEvent}
     */
    @SneakyThrows
    public FutureEvent<DeviceRecordList> getRecordInfo(ToDevice toDevice, Date beginTime, Date endTime) {

        DeviceRecordInfoQuery xmlQuery = DeviceRecordInfoQuery.builder()
                .deviceId(toDevice.getChannelId())
                .sn(SipUtil.generateSn())
                .endTime(endTime)
                .startTime(beginTime)
                .cmdType(SipEnum.Cmd.RecordInfo.name())
                .build();

        byte[] xmlData = XmlUtils.toByteXml(xmlQuery, SipConstant.CHARSET);

        toDevice.setChannelId(toDevice.getDeviceId());
        return SipBuilder
                .buildRequest(toDevice)
                .ofMessageRequest(xmlData)
                .subscribeResult(SipUtil.genSubscribeKey(xmlQuery.getCmdType(), xmlQuery.getDeviceId(), xmlQuery.getSn()))
                .executeAsync();
    }

    /**
     * 国标点播实时
     *
     * @param toDevice 设备
     * @return {@link FutureEvent}
     */
    @SneakyThrows
    public FutureEvent<MediaPlayInfo> openRtpRealTime(ToDevice toDevice) {
        // 生成流信息
        String streamId = GB28181SDPBuilder.getStreamId(toDevice.getDeviceId(), toDevice.getChannelId());
        FutureEvent event = SipPublisher.subscribeInvite(streamId).build();
        OpenRtpServerResult rtp = this.commonRtp(streamId);
        if(Objects.isNull(rtp)){
            return event;
        }
        // 发起点播邀请
        String playSsrc = SsrcUtil.getPlaySsrc();
        this.cacheInviteStream(toDevice, rtp, streamId, playSsrc, GB28181SDPBuilder.Action.PLAY);
        log.info("打开rtp「{}」打开成功. 准备发起点播邀请：{}", streamId, playSsrc);

        SipBuilder.buildRequest(toDevice)
                .ofPlayInviteRequest(rtp.getPort(), playSsrc, MediaStreamMode.of(toDevice.getTransport()), String.valueOf(0))
                .subscribeResult(inviteManager.getKey(streamId))
                .execute();

        return event;
    }

    private InviteStream cacheInviteStream(ToDevice toDevice, OpenRtpServerResult rtp, String streamId, String playSsrc, GB28181SDPBuilder.Action action) {
        // 记录邀请信息
        SipConfig.ZlmMedia media = sipConfig.getMedia();
        InviteStream inviteStream = InviteStream.builder()
                .port(rtp.getPort())
                .action(action.getAction())
                .ip(media.getIp())
                .enableMp4Record(toDevice.isEnableRecord())
                .mp4MaxSecond(media.getMp4MaxSecond())
                .streamId(streamId)
                .ssrc(playSsrc)
                .toDevice(toDevice)
                .build();
        inviteManager.setData(streamId, inviteStream);
        return inviteStream;
    }

    /**
     * 判断流是否存在
     * @return
     */
    private boolean streamExists(String streamId) {
        log.info("判断流「{}」是否存", streamId);
        RtpInfoResult rtpInfo = zlmRestTemplate.getRtpInfo(streamId);
        InviteStream invite = inviteManager.getData(streamId);
        if (rtpInfo.isExist()) {
            if (Objects.nonNull(invite)) {
                log.info("流「{}」存在", streamId);
                return true;
            }
            zlmRestTemplate.closeStream(MediaReq.getRtpInstance(streamId, MediaReq.class));
            log.warn("流「{}」存在、无缓存，关闭流", streamId);
            return false;
        }
        if (Objects.nonNull(invite)) {
            log.warn("流「{}」不存在、有缓存，清空缓存", streamId);
            inviteManager.delData(streamId);
        }
        return false;
    }

    private OpenRtpServerResult commonRtp( String streamId) {
        boolean streamExists = this.streamExists(streamId);

        // 存在信息则不需要推流
        if (streamExists) {
            InviteStream invite = inviteManager.getData(streamId);
            SipPublisher.handlerInvite(streamId).ofOk(invite.getPlayInfo());
            return null;
        }

        // 打开国标流
        OpenRtpServerReq req = new OpenRtpServerReq();
        req.setPort(0);
        req.setTcpMode(0);
        req.setStreamId(streamId);
        OpenRtpServerResult rtp = zlmRestTemplate.openRtpServer(req);
        int code = rtp.getCode();
        if (code != 0) {
            log.info("打开rtp「{}」打开流失败{}", streamId, JSON.toJSONString(rtp));
            SipPublisher.handlerInvite(streamId).ofFail("无法打开国标流");
            return null;
        }
        return rtp;
    }

    /**
     * 国标点播回放
     *
     * @param beginTime 开始时间
     * @param endTime   结束时间
     * @param toDevice  设备
     * @return {@link FutureEvent}<{@link MediaPlayInfo}>
     */
    public FutureEvent<MediaPlayInfo> openRtpRecord(ToDevice toDevice, Date beginTime, Date endTime) {

        String beginStr = DateUtil.format(beginTime, "yyyyMMddHHmmss"), endStr = DateUtil.format(endTime, "yyyyMMddHHmmss");
        String streamId = GB28181SDPBuilder.getStreamId(toDevice.getDeviceId(), toDevice.getChannelId(), beginStr, endStr);
        FutureEvent event = SipPublisher.subscribeInvite(streamId).build();

        OpenRtpServerResult rtp = this.commonRtp(streamId);
        if(Objects.isNull(rtp)){
            return event;
        }

        // 发起点播邀请
        String playBackSsrc = SsrcUtil.getPlayBackSsrc();
        this.cacheInviteStream(toDevice, rtp, streamId, playBackSsrc, GB28181SDPBuilder.Action.PLAY_BACK);
        log.info("打开rtp「{}」打开成功. 准备发起点播邀请：{}", streamId, playBackSsrc);

        SipBuilder.buildRequest(toDevice)
            .ofPlayBackInviteRequest(rtp.getPort(), playBackSsrc, MediaStreamMode.of(toDevice.getTransport()), String.valueOf(0), beginTime, endTime)
            .subscribeResult(inviteManager.getKey(streamId))
            .execute();

        return event;
    }

    /**
     * 关闭点播
     *
     * @param deviceId  设备id
     * @param transport 传输方式
     * @param channelId 通道ID
     */
    @SneakyThrows
    public FutureEvent<?> closeRtp(ToDevice toDevice, String streamId) {

        String subscribeKey = SipUtil.genSubscribeKey(Request.BYE, streamId);
        FutureEvent event = SipPublisher.subscribe(subscribeKey).build();

        try {
            // 获取邀请记录
            InviteStream invite = inviteManager.getData(streamId);
            if (Objects.isNull(invite)) {
                SipPublisher.handler(subscribeKey).ofFail("未找到点播记录");
                return event;
            }

            // 发送Bye请求
            log.info("[发送Bye请求],staream={}", streamId);
            SipTransactionInfo transactionInfo = invite.getTransactionInfo();
            if(Objects.nonNull(transactionInfo)){
                Request execute = SipBuilder.buildRequest(toDevice)
                        .ofByeRequest(invite.getTransactionInfo())
                        .subscribeResult(subscribeKey)
                        .execute();
                TimeUnit.MILLISECONDS.sleep(400);
            }

            // 关闭流
            ServerResponse<String> rtp = zlmRestTemplate.closeStream(MediaReq.getRtpInstance(streamId, MediaReq.class));
            if (rtp.getCode() != 0) {
                log.error("[关流-Error], streamId={}, 原因：{}", streamId, rtp.getMsg());
                SipPublisher.handler(subscribeKey).ofFail(rtp.getMsg());
                return event;
            }
            log.info("[关流-Ok],stream={}", streamId);
            SipPublisher.handler(subscribeKey).ofOk("OK");
            return event;
        } catch (Exception e) {
            log.error("发送BYE 出错", e.getMessage());
            SipPublisher.handler(subscribeKey).ofFail(e.getMessage());
            return event;
        }
    }


    /**
     * 代理推流
     *
     * @param toDevice 设备
     * @param url      拉流地址 [rtsp://]
     * @param record   是否开启mp4录制
     * @return {@link MediaPlayInfo}
     */
    public FutureEvent<MediaPlayInfo> openProxyRealTime(ToDevice toDevice, String url) {
        String streamId = GB28181SDPBuilder.getStreamId(
                toDevice.getDeviceId(),
                toDevice.getChannelId());

        FutureEvent event = SipPublisher.subscribeProxy(streamId).build();

        ProxyStream data = proxyManager.getData(streamId);
        if (Objects.nonNull(data)) {
            if (Objects.nonNull(data.getPlayInfo())) {
                SipPublisher.handlerProxy(streamId).ofOk(data.getPlayInfo());
                return event;
            } else {
                proxyManager.delData(streamId);
            }
        }
        SipConfig.ZlmMedia media = sipConfig.getMedia();
        StreamProxyItem req = StreamProxyItem.getProxyInstance(streamId, StreamProxyItem.class);
        req.setSchema(null);
        req.setUrl(url);
        req.setRetryCount(-1);
        req.setTimeoutSec(5);
        req.setEnableRtmp(true);
        req.setModifyStamp(2);
        req.setMp4MaxSecond(media.getMp4MaxSecond());
        if (toDevice.isEnableRecord()) {
            req.setEnableMp4(true);
        }
        ServerResponse<StreamKey> response = zlmRestTemplate.addStreamProxy(req);
        if (response.getCode() != 0) {
            throw new RuntimeException(StrUtil.format("操作失败,{}", response.getMsg()));
        }

        String key = response.getData().getKey();
        log.info("[打开代理拉流OK], key={}", key);
        MediaPlayInfo playUrl = new MediaPlayInfo();
        playUrl.setStreamId(req.getStream());
        playUrl.setProxyKey(key);
        playUrl.setApp(req.getApp());
        playUrl.setSnap("");
        playUrl.setPlayUrl(null);

        ProxyStream proxyStream = new ProxyStream();
        proxyStream.setIp(media.getIp());
        proxyStream.setVhost(req.getVhost());
        proxyStream.setRetryCount(req.getRetryCount());
        proxyStream.setEnableMp4Record(req.isEnableMp4());
        proxyStream.setMp4MaxSecond(req.getMp4MaxSecond());
        proxyStream.setProxyUrl(req.getUrl());
        proxyStream.setStreamId(req.getStream());
        proxyStream.setPlayInfo(playUrl);
        proxyStream.setToDevice(toDevice);
        proxyManager.setData(streamId, proxyStream);
        return event;
    }


    /**
     * 开启录制 [支持国标和代理]
     *
     * @param app      应用程序
     * @param streamId 流id
     */
    public void startRecord(String app, String streamId) {
        SipConfig.ZlmMedia media = sipConfig.getMedia();
        RecordReq req = RecordReq.getInstance(app, streamId, RecordReq.class);
        req.setMaxSecond(media.getMp4MaxSecond());
        req.setSchema(null);
        req.setType(1);

        ServerResponse<String> response = zlmRestTemplate.startRecord(req);
        if (response.getCode() == 0) {
            log.info("[云录像打开OK], strean：{}", req.getStream());
            inviteManager.processRecord(streamId, true);
            proxyManager.processRecord(streamId, true);
            return;
        }
        // 出错了
        throw new RuntimeException(response.getMsg());
    }


    /**
     * 关闭录制
     *
     * @param app      应用程序
     * @param streamId 流id
     */
    public void closeRecord(String app, String streamId) {
        SipConfig.ZlmMedia media = sipConfig.getMedia();
        // 发起关闭录像功能
        RecordReq req = RecordReq.getInstance(app, streamId, RecordReq.class);
        req.setType(1); // mp4录制
        req.setSchema(null);
        ServerResponse<String> response = zlmRestTemplate.stopRecord(req);
        if (response.getCode() == 0) {
            log.info("[云录像关闭OK], stream：{}", req.getStream());
            inviteManager.processRecord(streamId, false);
            proxyManager.processRecord(streamId, false);
            return;
        }
        // 出错了
        throw new RuntimeException(response.getMsg());
    }

    /**
     * 云录像路径
     *
     * @param relativePath 相对路径
     * @return {@link String}
     */
    public String cloudRecordPath(String relativePath) {
        SipConfig.ZlmMedia media = sipConfig.getMedia();
        String pathTemplate = "%s://%s:%s/index/api/downloadFile?file_path=" + relativePath;
        return String.format(pathTemplate, "http", media.getIp(), media.getPort());
    }

    /**
     * 设备配置信息
     *
     * @return
     */
    public SipConfig deviceConfigInfo() {
        return sipConfig;
    }

    /**
     * 根据流信息获取播放地址
     *
     * @param streamId
     * @return
     */
    public MediaPlayInfo getPlay(String streamId) {
        InviteStream data = inviteManager.getData(streamId);
        if (Objects.isNull(data)) {
            throw new RuntimeException("无点播记录");
        }
        return data.getPlayInfo();
    }
}
