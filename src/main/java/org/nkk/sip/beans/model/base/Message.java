package org.nkk.sip.beans.model.base;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 消息
 *
 * @author nkk
 * @date 2024/07/08
 */
@Getter
public class Message<T> {

    /**
     * callId
     */
    private final String key;
    private T data;
    /**
     * 状态：-1：-异常，0：-超时， 1:-成功
     */
    private EnumState code;
    /**
     * 消息
     */
    private String msg;


    public Message(String key) {
        this.key = key;
    }

    public Message<T> msg(String msg) {
        this.msg = msg;
        return this;
    }

    public Message<T> data(T data) {
        this.data = (T) data;
        return this;
    }

    public Message<T> handlerOk() {
        this.code = EnumState.OK;
        this.msg = StrUtil.isEmpty(this.msg) ? "成功" : this.getMsg();
        return this;
    }

    public Message<T> handlerError() {
        this.code = EnumState.Error;
        this.msg = StrUtil.isEmpty(this.msg) ? "服务器错误" : this.getMsg();
        return this;
    }

    public Message<T> handlerTimeOut() {
        this.code = EnumState.timeOut;
        this.msg = StrUtil.isEmpty(this.msg) ? "响应超时" : this.getMsg();
        return this;
    }

    @AllArgsConstructor
    public enum EnumState {
        timeOut(0),
        OK(1),
        Error(-1);

        @Getter
        private final Integer code;
    }
}
