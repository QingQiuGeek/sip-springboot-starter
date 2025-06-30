package org.nkk.sip.beans.model.device.Dto;

import lombok.Data;

/**
 * gb设备
 *
 *
 * @author nkk
 * @date 2024/07/19
 */
@Data
public class GbDevice {

    /**
     * 设备id
     */
    private String deviceId;

    /**
     * 名字
     */
    private String name;

    /**
     * 制造商
     */
    private String manufacturer;

    /**
     * 模型
     */
    private String model;

    /**
     * 固件
     */
    private String firmware;

    /**
     * 运输
     */
    private String transport;

    /**
     * 流模式
     */
    private String streamMode;

    /**
     * 在网上
     */
    private Boolean onLine;

    /**
     * IP
     */
    private String ip;

    /**
     * 端口
     */
    private Integer port;

    /**
     * 到期
     */
    private Integer expires;

    /**
     * 目录订阅周期
     */
    private Integer subscribeCycleForCatalog;

    /**
     * 移动位置订阅周期
     */
    private Integer subscribeCycleForMobilePosition;

    /**
     * 移动位置提交间隔
     */
    private Integer mobilePositionSubmissionInterval;

    /**
     * 报警订阅周期
     */
    private Integer subscribeCycleForAlarm;

    /**
     * 主机地址
     */
    private String hostAddress;

    /**
     * 字符集
     */
    private String charset;

    /**
     * 中心检查
     */
    private Boolean ssrcCheck;

    /**
     * 地球坐标系统
     */
    private String geoCoordSys;

    /**
     * 媒体服务器id
     */
    private String mediaServerId;

    /**
     * sdpIP
     */
    private String sdpIp;

    /**
     * 当地IP
     */
    private String localIp;

    /**
     * 密码
     */
    private String password;

    /**
     * 作为消息通道
     */
    private Boolean asMessageChannel;

    /**
     * 保持活动间隔时间
     */
    private Integer keepaliveIntervalTime;

    /**
     * 交换机主子流
     */
    private Boolean switchPrimarySubStream;

    /**
     * 广播后推
     */
    private Boolean broadcastPushAfterAck;
}
