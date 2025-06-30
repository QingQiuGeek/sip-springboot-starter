package org.nkk.media.beans.entity.rtp;


import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.nkk.media.beans.entity.req.MediaReq;

import java.util.Map;

/**
 * 作为GB28181客户端，启动ps-rtp推流，支持rtp/udp方式；该接口支持rtsp/rtmp等协议转ps-rtp推流。
 * 第一次推流失败会直接返回错误，成功一次后，后续失败也将无限重试。。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class StartSendRtpReq extends MediaReq {

    /**
     * 推流的rtp的ssrc,指定不同的ssrc可以同时推流到多个服务器。
     */
    private int ssrc;

    /**
     * 目标ip或域名。
     */
    private String dstUrl;

    /**
     * 目标端口。
     */
    private int dstPort;

    /**
     * 是否为udp模式,否则为tcp模式。
     */
    private boolean isUdp;

    /**
     * 使用的本机端口，为0或不传时默认为随机端口。
     */
    private Integer srcPort;

    /**
     * 发送时，rtp的pt（uint8_t）,不传时默认为96。
     */
    private Integer pt;

    /**
     * 发送时，rtp的负载类型。为1时，负载为ps；为0时，为es；不传时默认为1。
     */
    private Integer usePs;

    /**
     * 当use_ps 为0时，有效。为1时，发送音频；为0时，发送视频；不传时默认为0。
     */
    private Boolean onlyAudio;

    // Getters and setters...

    /**
     * 转换成map
     *
     * @return {@link Map}<{@link String}, {@link Object}>
     */
    public Map<String, Object> toMap() {
        return BeanUtil.beanToMap(this, true, true);
    }

    /**
     * 作为GB28181 Passive TCP服务器；该接口支持rtsp/rtmp等协议转ps-rtp被动推流。调用该接口，zlm会启动tcp服务器等待连接请求，连接建立后，zlm会关闭tcp服务器，然后源源不断的往客户端推流。
     * 第一次推流失败会直接返回错误，成功一次后，后续失败也将无限重试(不停地建立tcp监听，超时后再关闭)。
     *
     * @return
     */
    public Map<String, Object> getPassiveMap() {
        Map<String, Object> map = toMap();
        map.put("ssrc", String.valueOf(ssrc));
        map.put("src_port", String.valueOf(srcPort));
        map.put("pt", String.valueOf(pt));
        map.put("use_ps", String.valueOf(usePs));
        map.put("only_audio", String.valueOf(onlyAudio));
        return map;
    }
}
