package org.nkk.media.hook.param;

import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 对HTTP访问钩子结果
 * Java 类表示
 *
 * @author nkk
 * @date 2024/07/10
 */
@Data
public class HookResultForOnHttpAccess extends HookResult {

    /**
     * 不允许访问的错误提示，允许访问请置空
     */
    @JSONField(name = "err")
    @JsonProperty("err")
    private String err;

    /**
     * 该客户端能访问或被禁止的顶端目录，如果为空字符串，则表述为当前目录
     */
    @JSONField(name = "path")
    @JsonProperty("path")
    private String path;

    /**
     * 本次授权结果的有效期，单位秒
     */
    @JSONField(name = "second")
    @JsonProperty("second")
    private int second = 600;


    public static HookResultForOnHttpAccess SUCCESS() {
        return new HookResultForOnHttpAccess();
    }
}

// getters and setters