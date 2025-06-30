package org.nkk.media.beans.entity.rtp;

import cn.hutool.core.bean.BeanUtil;
import lombok.Data;

import java.util.Map;

/**
 * @author luna
 * @version 1.0
 * @date 2023/12/3
 * @description:
 */
@Data
public class OpenRtpServerReq {

    /**
     * 接收端口，0则为随机端口。
     */
    private int port;

    /**
     * 0 udp 模式，1 tcp 被动模式, 2 tcp 主动模式。 (兼容enable_tcp 为0/1)。
     */
    private int tcpMode;

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
