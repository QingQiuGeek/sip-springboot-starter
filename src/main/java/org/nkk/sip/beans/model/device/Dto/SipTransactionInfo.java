package org.nkk.sip.beans.model.device.Dto;

import gov.nist.javax.sip.message.SIPRequest;
import gov.nist.javax.sip.message.SIPResponse;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Sip事务信息（发送要求请求时，记录当前请求的信息）
 *
 * @author nkk
 * @date 2024/07/22
 */
@NoArgsConstructor
@Data
public class SipTransactionInfo implements Serializable {

    private static final long serialVersionUID = 8830528727032342028L;
    private String callId;
    private String fromTag;
    private String toTag;
    private String viaBranch;
    public SipTransactionInfo(SIPResponse response) {
        this.callId = response.getCallIdHeader().getCallId();
        this.fromTag = response.getFromTag();
        this.toTag = response.getToTag();
        this.viaBranch = response.getTopmostViaHeader().getBranch();
    }

    public SipTransactionInfo(SIPRequest request) {
        this.callId = request.getCallIdHeader().getCallId();
        this.fromTag = request.getFromTag();
        this.toTag = request.getToTag();
        this.viaBranch = request.getTopmostViaHeader().getBranch();
    }

}
