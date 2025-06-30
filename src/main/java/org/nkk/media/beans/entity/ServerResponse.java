package org.nkk.media.beans.entity;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

@Data
public class ServerResponse<T> {

    @JSONField(name = "code")
    private Integer code;

    @JSONField(name = "data")
    private T data;

    @JSONField(name = "msg")
    private String msg;

    @JSONField(name = "result")
    private String result;
}