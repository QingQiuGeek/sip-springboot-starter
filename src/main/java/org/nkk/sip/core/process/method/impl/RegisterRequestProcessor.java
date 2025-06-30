package org.nkk.sip.core.process.method.impl;

import cn.hutool.core.util.StrUtil;
import gov.nist.javax.sip.message.SIPRequest;
import gov.nist.javax.sip.message.SIPResponse;
import lombok.extern.slf4j.Slf4j;
import org.nkk.sip.beans.annotations.SipEvent;
import org.nkk.sip.beans.constants.SipConstant;
import org.nkk.sip.beans.enums.SipEnum;
import org.nkk.sip.beans.model.device.Dto.GbDevice;
import org.nkk.sip.beans.model.device.Dto.RemoteInfo;
import org.nkk.sip.config.SipConfig;
import org.nkk.sip.core.builder.SipBuilder;
import org.nkk.sip.core.process.method.AbstractSipRequestProcessor;
import org.nkk.sip.core.sdp.media.MediaStreamMode;
import org.nkk.sip.core.session.impl.SessionManager;
import org.nkk.sip.utils.DigestAuthenticationHelper;
import org.nkk.sip.utils.SipUtil;

import javax.annotation.Resource;
import javax.sip.ListeningPoint;
import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;
import javax.sip.address.Address;
import javax.sip.header.AuthorizationHeader;
import javax.sip.header.ViaHeader;
import javax.sip.message.Response;

/**
 * 注册事件处理器
 *
 * @author nkk
 * @date 2024/01/10
 */
@Slf4j
@SipEvent(SipEnum.Method.REGISTER)
public class RegisterRequestProcessor extends AbstractSipRequestProcessor {
    @Resource
    private SipConfig sipConfig;

    @Resource
    private SessionManager sessionManager;

    @Override
    public void request(RequestEvent event) {
        SIPRequest request = (SIPRequest) event.getRequest();
        Address address = SipUtil.getAddressFromFromHeader(request);
        String deviceId = SipUtil.getUserIdFromFromHeader(request);

        // 注册/注销
        int expires = request.getExpires().getExpires();
        boolean registerFlag = expires > 0;

        try {
            SipConfig.SipServerConf sipServe = sipConfig.getServer();
            String password = sipServe.getPassword();
            AuthorizationHeader authHead = request.getAuthorization();
            if (authHead == null && StrUtil.isNotEmpty(password)) {
                // 创建消息
                SipBuilder
                        .buildUnauthorizedOfResponse(request, sipServe.getDomain(), password)
                        .execute();
                return;
            }
            /*=====================================开始处理=====================================*/
            RemoteInfo remoteInfo = SipUtil.getRemoteAddressFromRequest(request, false);
            log.info("「From」: {},{}:{}", address, remoteInfo.getIp(), remoteInfo.getPort());

            boolean authPass = StrUtil.isBlank(password) ||
                    DigestAuthenticationHelper.doAuthenticatePlainTextPassword(request, password);
            if (!authPass) {
                SipBuilder
                        .buildResponse(Response.FORBIDDEN, request)
                        .addReasonPhrase("认证失败")
                        .execute();
                log.info("「To」：设备{}, 认证失败", deviceId);
                return;
            }
            String reason = registerFlag ? "注册成功" : "注销成功";
            SIPResponse response = (SIPResponse) SipBuilder
                    .buildRegisterOfResponse(request)
                    .addReasonPhrase(reason)
                    .execute();
            log.info("「To」：设备{}, {}", deviceId, reason);

            GbDevice device = this.setDevice(deviceId, remoteInfo, request);
            if (registerFlag) {
                device.setOnLine(true);
                sessionManager.online(device, response);
            } else {
                device.setOnLine(false);
                sessionManager.offline(device);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private GbDevice setDevice(String deviceId, RemoteInfo remoteInfo, SIPRequest request) {
        GbDevice device = new GbDevice();
        device.setStreamMode(MediaStreamMode.UDP.getMode());
        device.setCharset(SipConstant.CHARSET);
        device.setGeoCoordSys(SipConstant.GEO_COORD_SYS);
        device.setOnLine(false);

        device.setDeviceId(deviceId);
        device.setIp(remoteInfo.getIp());
        device.setPort(remoteInfo.getPort());
        device.setKeepaliveIntervalTime(60);

        ViaHeader viaHeader = request.getTopmostViaHeader();
        String transport = viaHeader.getTransport();
        device.setTransport(ListeningPoint.TCP.equalsIgnoreCase(transport) ? ListeningPoint.TCP : ListeningPoint.UDP);

        int expires = request.getExpires().getExpires();
        device.setExpires(expires);
        return device;
    }


    @Override
    public void response(ResponseEvent event) {
        SIPResponse response = (SIPResponse) event.getResponse();
        String callId = response.getCallIdHeader().getCallId();
        log.debug("「国标级联」：callId=[{}]", callId);
    }
}
