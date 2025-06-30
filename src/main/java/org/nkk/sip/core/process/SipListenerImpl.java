package org.nkk.sip.core.process;

import lombok.extern.slf4j.Slf4j;
import org.nkk.sip.core.builder.SipBuilder;
import org.nkk.sip.utils.SipUtil;
import org.springframework.scheduling.annotation.Async;

import javax.sip.*;
import javax.sip.header.CSeqHeader;
import javax.sip.header.FromHeader;
import javax.sip.header.ToHeader;
import javax.sip.message.Response;


/**
 * Sip监听处理器入口
 *
 * @author nkk
 * @date 2024/01/10
 */
@Slf4j
public class SipListenerImpl implements SipListener {

    /**
     * 处理请求
     *
     * @param requestEvent 请求事件
     */
    @Override
    @Async("taskExecutor")
    public void processRequest(RequestEvent requestEvent) {
        String method = requestEvent.getRequest().getMethod();
        log.info("\033[36;2m 来自设备「{}」请求\033[36;0m", method);
        SipMethodContext.Method.execute(method).request(requestEvent);
    }

    /**
     * 处理响应
     *
     * @param responseEvent 响应事件
     */
    @Override
    @Async("taskExecutor")
    public void processResponse(ResponseEvent responseEvent) {
        Response response = responseEvent.getResponse();
        int status = response.getStatusCode();
        CSeqHeader cseqHeader = (CSeqHeader) responseEvent.getResponse().getHeader(CSeqHeader.NAME);
        String method = cseqHeader.getMethod();

        log.info("\033[36;2m 来自设备「{}」的{}响应\033[36;0m", method, status);
        if (((status >= Response.OK) && (status < Response.MULTIPLE_CHOICES)) || status == Response.UNAUTHORIZED) {
            SipMethodContext.Method.execute(method).response(responseEvent);
        }
        // 增加其它无需回复的响应，如101、180等
        else if ((status >= Response.TRYING) && (status < Response.OK)) {
            log.warn("\033[31;2m「无需回复的响应」{}\033[31;0m", response.getStatusCode());
        } else {
            log.error("\033[31;2m「错误信息」{}\033[31;0m", response.getReasonPhrase());
            SipBuilder.callMessage(response).callError(response.getReasonPhrase());
            if (responseEvent.getDialog() != null) {
                responseEvent.getDialog().delete();
            }
        }
    }

    /**
     * 处理超时
     *
     * @param timeoutEvent 超时事件
     */
    @Override
    public void processTimeout(TimeoutEvent timeoutEvent) {
        log.info("收到设备 超时回调");
        ClientTransaction clientTransaction = timeoutEvent.getClientTransaction();
        if (clientTransaction != null) {

        }
    }

    /**
     * 处理异常
     *
     * @param ioExceptionEvent IO异常事件
     */
    @Override
    public void processIOException(IOExceptionEvent ioExceptionEvent) {
        log.info("收到设备 IO异常的回调");
    }

    /**
     * 处理事务终止
     *
     * @param transactionTerminatedEvent 事务终止事件
     */
    @Override
    public void processTransactionTerminated(TransactionTerminatedEvent transactionTerminatedEvent) {
        log.info("收到设备 事务中断回调");
    }

    /**
     * 处理对话框终止
     *
     * @param dialogTerminatedEvent 对话终止事件
     */
    @Override
    public void processDialogTerminated(DialogTerminatedEvent dialogTerminatedEvent) {
        log.info("收到设备 对话框关闭事件");
    }
}
