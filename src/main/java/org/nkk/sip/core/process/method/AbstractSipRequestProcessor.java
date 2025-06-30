package org.nkk.sip.core.process.method;

import lombok.extern.slf4j.Slf4j;
import org.nkk.sip.beans.annotations.SipEvent;
import org.nkk.sip.core.process.SipMethodContext;

import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;


/**
 * SIP请求处理器
 *
 * @author nkk
 * @date 2024/01/10
 */
@Slf4j
public abstract class AbstractSipRequestProcessor implements ISipProcessor {

    /**
     * 构造注入
     */
    public AbstractSipRequestProcessor() {
        SipEvent request = this.getClass().getAnnotation(SipEvent.class);
        if (request != null) {
            SipMethodContext.Method.registerStrategy(request.value(), this);
        }
    }

}
