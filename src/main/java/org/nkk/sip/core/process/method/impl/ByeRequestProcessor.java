package org.nkk.sip.core.process.method.impl;

import gov.nist.javax.sip.message.SIPResponse;
import lombok.extern.slf4j.Slf4j;
import org.nkk.sip.beans.annotations.SipEvent;
import org.nkk.sip.beans.enums.SipEnum;
import org.nkk.sip.core.process.method.AbstractSipRequestProcessor;

import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;
import javax.sip.message.Response;

@Slf4j
@SipEvent(SipEnum.Method.BYE)
public class ByeRequestProcessor extends AbstractSipRequestProcessor {

    @Override
    public void request(RequestEvent event) {
        log.info("「ACK」Request");
    }

    @Override
    public void response(ResponseEvent event) {
        SIPResponse response = (SIPResponse) event.getResponse();
        if (response.getStatusCode() != Response.OK) {
            log.info("「BYE」出错了：{}", response.getReasonPhrase());
            return;
        }
        String callId = response.getCallIdHeader().getCallId();
        log.info("「BYE」响应, callId={}", callId);
    }
}
