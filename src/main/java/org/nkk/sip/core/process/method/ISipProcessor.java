package org.nkk.sip.core.process.method;


import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;

/**
 * Isip事件处理器
 *
 * @author nkk
 * @date 2024/01/10
 */
public interface ISipProcessor {

    /**
     * 处理请求
     *
     * @param event 请求事件
     */
    public abstract void request(RequestEvent event);

    /**
     * 处理响应
     *
     * @param event 响应事件
     */
    public abstract void response(ResponseEvent event);

}
