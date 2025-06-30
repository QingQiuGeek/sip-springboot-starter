package org.nkk.media.beans.entity;

import cn.hutool.core.bean.BeanUtil;
import lombok.Data;

import java.util.Map;

/**
 * This class represents a stream with various properties.
 */
@Data
public class StreamFfmpegItem {

    /**
     * FFmpeg拉流地址,支持任意协议或格式(只要FFmpeg支持即可)
     */
    private String  srcUrl;

    /**
     * FFmpeg rtmp推流地址，一般都是推给自己，例如rtmp://127.0.0.1/live/stream_form_ffmpeg
     */
    private String  dstUrl;

    /**
     * FFmpeg推流成功超时时间
     */
    private Integer timeoutMs;

    /**
     * 是否开启hls录制
     */
    private Boolean enableHls;

    /**
     * 是否开启mp4录制
     */
    private Boolean enableMp4;

    /**
     * 配置文件中FFmpeg命令参数模板key(非内容)，置空则采用默认模板:ffmpeg.cmd
     */
    private String  ffmpegCmdKey;

    public Map<String, Object> toMap() {
        return BeanUtil.beanToMap(this, true, true);
    }
    // Getters and setters...
}
