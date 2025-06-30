package org.nkk.sip.core.session.impl;

import gov.nist.javax.sip.message.SIPResponse;
import lombok.extern.slf4j.Slf4j;
import org.nkk.common.utils.CacheKeyUtil;
import org.nkk.sip.beans.model.base.DeviceBase;
import org.nkk.sip.beans.model.base.Message;
import org.nkk.sip.beans.model.base.ToDevice;
import org.nkk.sip.beans.model.device.Dto.GbDevice;
import org.nkk.sip.beans.model.device.Dto.SipSession;
import org.nkk.sip.beans.model.device.Dto.SipTransactionInfo;
import org.nkk.sip.beans.model.device.Resp.DeviceInfo;
import org.nkk.sip.core.service.FutureEvent;
import org.nkk.sip.core.service.SipEventProcess;
import org.nkk.sip.core.service.SipMessageTemplate;
import org.nkk.sip.core.session.SipManager;
import org.nkk.sip.core.session.cache.SipCacheManager;

import javax.annotation.Resource;

/**
 * 设备登陆信息管理器
 * <p>该信息好像没有什么用</p>
 */
@Slf4j
public class SessionManager implements SipManager {

    private static final String PREFIX = "SESSION";

    @Resource
    private SipCacheManager<SipSession> sipCacheManager;

    @Resource
    private SipEventProcess eventProcess;

    @Resource
    private SipMessageTemplate sipMessageTemplate;

    public void online(GbDevice device, SIPResponse response) {
        // 添加缓存
        String key = CacheKeyUtil.getKey(PREFIX, device.getDeviceId());

        SipSession sipSession = new SipSession();
        sipSession.setUserId(device.getDeviceId());
        sipSession.setIp(device.getIp());
        sipSession.setPort(device.getPort());
        sipSession.setTransport(device.getTransport());
        sipSession.setExpires(device.getExpires());
        sipSession.setTransactionInfo(new SipTransactionInfo(response));
        sipCacheManager.set(key, sipSession);

        try {
            ToDevice toDevice = ToDevice.builder()
                    .deviceId(device.getDeviceId())
                    .channelId(device.getDeviceId())
                    .ip(device.getIp())
                    .port(device.getPort())
                    .transport(device.getTransport())
                    .build();
            FutureEvent<DeviceInfo> deviceInfo = sipMessageTemplate.getDeviceInfo(toDevice);
            Message<DeviceInfo> message = deviceInfo.get();
            DeviceInfo data = message.getData();
            device.setFirmware(data.getFirmware());
            device.setManufacturer(data.getManufacturer());
        } catch (Exception e) {
            log.warn("[设备上线],查询信息失败，error={}", e.getMessage());
        }
        // 事件处理
        eventProcess.online(device);
    }


    public void offline(GbDevice device) {
        // 移除缓存
        String key = CacheKeyUtil.getKey(PREFIX, device.getDeviceId());
        sipCacheManager.clear(key);
        // 事件处理
        eventProcess.offline(device);
    }

    public void keepalive(DeviceBase deviceBase) {
        // 事件处理
        GbDevice device = new GbDevice();
        device.setDeviceId(deviceBase.getDeviceId());
        eventProcess.keepalive(device);
    }


}
