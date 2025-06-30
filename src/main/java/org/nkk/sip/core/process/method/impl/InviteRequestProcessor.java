package org.nkk.sip.core.process.method.impl;

import gov.nist.javax.sip.ResponseEventExt;
import gov.nist.javax.sip.message.SIPResponse;
import lombok.extern.slf4j.Slf4j;
import org.nkk.sip.beans.annotations.SipEvent;
import org.nkk.sip.beans.enums.SipEnum;
import org.nkk.sip.beans.model.base.ToDevice;
import org.nkk.sip.beans.model.invite.InviteStream;
import org.nkk.sip.core.builder.SipBuilder;
import org.nkk.sip.core.parser.GB28181DescriptionParser;
import org.nkk.sip.core.process.method.AbstractSipRequestProcessor;
import org.nkk.sip.core.sdp.GB28181Description;
import org.nkk.sip.core.sdp.GB28181SDPBuilder;
import org.nkk.sip.core.service.FutureEvent;
import org.nkk.sip.core.service.SipPublisher;

import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;
import javax.sip.message.Response;

@Slf4j
@SipEvent(SipEnum.Method.INVITE)
public class InviteRequestProcessor extends AbstractSipRequestProcessor {

    @Override
    public void request(RequestEvent event) {
        log.info("request-INVITE:");
    }

    @Override
    public void response(ResponseEvent event) {
        SIPResponse response = (SIPResponse) event.getResponse();
        int statusCode = response.getStatusCode();
        try {
            // 尝试处理
            if (statusCode == Response.TRYING) {
                log.info("尝试..");
            }
            // 回复ACK
            else if (statusCode == Response.OK) {
                // 这句一定要发ack请求前处理
                ResponseEventExt ext = (ResponseEventExt) event;
                String contentString = new String(response.getRawContent());
                GB28181DescriptionParser msgParser = new GB28181DescriptionParser(contentString);
                GB28181Description sdp = msgParser.parse();
                String trans = response.getTopmostViaHeader().getTransport();
                // 记录邀请处理ok
                ToDevice toDevice = ToDevice.builder()
                        .deviceId(sdp.getOrigin().getUsername())
                        .channelId(sdp.getOrigin().getUsername())
                        .ip(ext.getRemoteIpAddress())
                        .port(ext.getRemotePort())
                        .transport(trans)
                        .build();
                // 回复ok
                 SipBuilder.buildRequest(toDevice)
                        .ofAckRequest(response)
                        .execute();
                log.info("「回复ACK」OK");
                // 点播邀请请求成功，回复ack 继续等待结果
                SipBuilder.callMessage(response).callOk();

                SipPublisher.handlerInvite("")
                        .ofThen("");
            }
        } catch (Exception e) {
            log.error("「点播回复ACK」异常：", e);
        }

    }

}
