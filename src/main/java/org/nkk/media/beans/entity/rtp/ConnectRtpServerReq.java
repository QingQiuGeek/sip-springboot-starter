package org.nkk.media.beans.entity.rtp;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author luna
 * @version 1.0
 * @date 2023/12/3
 * @description:
 */
@Data
public class ConnectRtpServerReq {

    /**
     * tcp主动模式时服务端端口
     */
    private int dstPort;

    /**
     * tcp主动模式时服务端地址
     */
    private int dstUrl;

    /**
     * 该端口绑定的流ID，该端口只能创建这一个流(而不是根据ssrc创建多个)。
     */
    private String streamId;

    /**
     * 转换成map
     *
     * @return {@link Map}<{@link String}, {@link Object}>
     */
    public Map<String, Object> toMap() {
        return BeanUtil.beanToMap(this, true, true);
    }

}
