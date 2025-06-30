package org.nkk.media.beans.entity;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.nkk.media.beans.entity.req.MediaReq;

import java.util.Map;

/**
 * This class represents a stream with various properties.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class StreamPusherItem extends MediaReq {

    /**
     * 目标转推url，带参数需要自行url转义
     */
    private String dstUrl;

    /**
     * 转推失败重试次数，默认无限重试
     */
    private Integer retryCount;

    /**
     * rtsp推流时，推流方式，0：tcp，1：udp
     */
    private Integer rtpType;

    /**
     * 推流超时时间，单位秒，float类型
     */
    private Integer timeoutSec;


    // Getters and setters...
}
