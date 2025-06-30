package org.nkk.sip.beans.model.proxy;

import lombok.Data;
import org.nkk.media.hook.custom.MediaPlayInfo;
import org.nkk.sip.beans.model.base.ToDevice;

/**
 * 代理拉流
 */
@Data
public class ProxyStream {


    /**
     * 订阅的key
     */
    private String subscribeKey;

    /**
     * zlm IP
     */
    private String ip;

    /**
     * vhost
     */
    private String vhost;

    /**
     * 重试计数
     */
    private int retryCount;

    /**
     * 开启mp4录制
     */
    private boolean enableMp4Record;

    /**
     * Mp4最大秒
     */
    private int mp4MaxSecond;

    /**
     * 代理地址
     */
    private String proxyUrl;

    /**
     * 流id
     */
    private String streamId;

    /**
     * 播放信息
     */
    private MediaPlayInfo playInfo;

    /**
     * 设备
     */
    private ToDevice toDevice;
}
