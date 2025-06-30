package org.nkk.sip.config;

import cn.hutool.core.util.StrUtil;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * sip属性p配置
 *
 * @author nkk
 * @date 2024/01/09
 */
@Data
@ConfigurationProperties(prefix = "sip", ignoreInvalidFields = true)
public class SipConfig {
    /**
     * 是否启用SIP日志，ERROR, INFO, WARNING, OFF, DEBUG, TRACE 默认是：OFF
     */
    private String logs = "OFF";

    /**
     * 超时时间 【单位秒】
     */
    private Integer timeout = 20;

    /**
     * 服务器
     */
    private SipServerConf server;

    /**
     * 全局订阅
     */
    private SipSubscribe subscribe;

    /**
     * 配置信息
     */
    private SipMapConf map;

    /**
     * 流媒体配置
     */
    private ZlmMedia media;

    /**
     * 流配置
     */
    private StreamConf stream;

    /**
     * sip订阅
     *
     * @author nkk
     * @date 2024/06/29
     */
    @Data
    public static class SipSubscribe {

        /**
         * 全局-订阅目录
         */
        private Boolean catalog = Boolean.FALSE;
        /**
         * 全局-订阅报警
         */
        private Boolean alarm = Boolean.FALSE;
        /**
         * 全局-订阅位置
         */
        private Boolean location = Boolean.FALSE;

    }


    @Data
    public static class SipMapConf {

        /**
         * 启用
         */
        private Boolean enable = false;

        /**
         * 地图中心
         */
        private String center = "117.17159813310452,31.83907609118903";


    }

    @Data
    public static class SipServerConf {
        /**
         * SIP服务器ID
         */
        private String id;

        /**
         * SIP服务器域 (domain宜采用ID统一编码的前十位编码)
         */
        private String domain;

        /**
         * SIP服务器地址，一般是本机IP
         * 不填写默认为：`0.0.0.0`
         */
        private String ip;

        /**
         * SIP服务器端口
         */
        private Integer port = 5060;

        /**
         * 设备接入认证密码
         */
        private String password;

    }

    /**
     * zlm 流媒体服务
     *
     * @author nkk
     * @date 2024/07/09
     */
    @Data
    public static class ZlmMedia {

        /**
         * IP
         */
        private String ip;

        /**
         * 端口
         */
        private int port;

        /**
         * 媒体id
         */
        private String mediaId;

        /**
         * 密钥
         */
        private String secret;

        /**
         * Mp4最大秒
         */
        private int mp4MaxSecond = 600;

        /**
         * 录制文件地址,默认 `./www下`
         */
        private String recordPath;

        /**
         * 录制倍率
         */
        private Double recordSpeed = 1.0;

        /**
         * 获得主机地址
         *
         * @return {@link String}
         */
        public String getHost() {
            return StrUtil.format("http://{}:{}", this.ip, this.port);
        }
    }

    /**
     * 流相关配置，这里的配置生效的前提是流媒体也开启相关配置
     */
    @Data
    public static class StreamConf {

        /**
         * 无人观看时自动关闭流，默认false
         */
        private boolean autoClose = false;

        /**
         * 是否开启rtmp流，默认true
         * rtsp无论如何最好开启，否则可能zlm拉不到流
         */
        private boolean enableRtsp = true;

        /**
         * 是否开启rtmp流，默认true
         */
        private boolean enableRtmp = true;

        /**
         * 是否开启hlsFm4流，默认true
         */
        private boolean enableHlsFmp4 = true;

        /**
         * 是否开启hls流，默认true
         */
        private boolean enableHls = true;

        /**
         * 是否开启fmp4流，默认true
         */
        private boolean enableFmp4 = true;

        /**
         * 是否开启ts流。默认true
         */
        private boolean enableTs = true;

    }


}
