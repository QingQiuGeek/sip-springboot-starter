package org.nkk.sip.core.process.method.impl;

import gov.nist.javax.sip.message.SIPRequest;
import gov.nist.javax.sip.message.SIPResponse;
import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;
import javax.sip.address.Address;
import javax.sip.address.URI;
import javax.sip.header.FromHeader;
import lombok.extern.slf4j.Slf4j;
import org.nkk.sip.beans.annotations.SipEvent;
import org.nkk.sip.beans.enums.SipEnum.Method;
import org.nkk.sip.beans.model.base.DeviceBase;
import org.nkk.sip.core.builder.SipBuilder;
import org.nkk.sip.core.process.method.AbstractSipRequestProcessor;
import org.nkk.sip.utils.SipUtil;
import org.nkk.sip.utils.XmlUtils;

/**
 * @author 懒大王Smile
 */
@Slf4j
@SipEvent(Method.NOTIFY)
public class NotifyRequestProcessor extends AbstractSipRequestProcessor {

    @Override
    public void request(RequestEvent event) {
        SIPRequest request = (SIPRequest) event.getRequest();
        URI fromUri = request.getFromHeader().getAddress().getURI();
        // 解析xml
        byte[] content = request.getRawContent();
        DeviceBase deviceBase = XmlUtils.parse(content, DeviceBase.class);
        log.info("接收到Notify通知，来自: {}", fromUri.toString());
        // 发送OK
        SipBuilder.buildOKResponse(request).execute();
    }

    @Override
    public void response(ResponseEvent event) {
        SIPResponse response = (SIPResponse) event.getResponse();
        SipBuilder.callMessage(response).callOk();
        log.debug("处理「Notify」200响应-OK");
    }
}
