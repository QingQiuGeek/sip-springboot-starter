package org.nkk.media.beans.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.nkk.media.beans.entity.req.MediaReq;

/**
 * This class represents a stream with various properties.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class StreamProxyItem extends MediaReq {


    /**
     * 拉流地址，例如rtmp://live.hkstv.hk.lxdns.com/live/hks2
     */
    private String url;

    /**
     * 拉流重试次数，默认为-1无限重试
     */
    private Integer retryCount;

    /**
     * rtsp拉流时，拉流方式，0：tcp，1：udp，2：组播
     */
    private int rtpType;

    /**
     * 拉流超时时间，单位秒，float类型
     */
    private Integer timeoutSec;

    /**
     * 是否转换成hls-mpegts协议
     */
    private boolean enableHls;

    /**
     * 是否转换成hls-fmp4协议
     */
    private boolean enableHlsFmp4;

    /**
     * 是否允许mp4录制
     */
    private boolean enableMp4;

    /**
     * 是否转rtsp协议
     */
    private boolean enableRtsp;

    /**
     * 是否转rtmp/flv协议
     */
    private boolean enableRtmp;

    /**
     * 是否转http-ts/ws-ts协议
     */
    private boolean enableTs;

    /**
     * 是否转http-fmp4/ws-fmp4协议
     */
    private boolean enableFmp4;

    /**
     * 该协议是否有人观看才生成
     */
    private boolean hlsDemand;

    /**
     * 该协议是否有人观看才生成
     */
    private boolean rtspDemand;

    /**
     * 该协议是否有人观看才生成
     */
    private boolean rtmpDemand;

    /**
     * 该协议是否有人观看才生成
     */
    private boolean tsDemand;

    /**
     * 该协议是否有人观看才生成
     */
    private boolean fmp4Demand;

    /**
     * 转协议时是否开启音频
     */
    private boolean enableAudio;

    /**
     * 转协议时，无音频是否添加静音aac音频
     */
    private boolean addMuteAudio;

    /**
     * mp4录制文件保存根目录，置空使用默认
     */
    private String mp4SavePath;

    /**
     * mp4录制切片大小，单位秒
     */
    private int mp4MaxSecond;

    /**
     * MP4录制是否当作观看者参与播放人数计数
     */
    private boolean mp4AsPlayer;

    /**
     * hls文件保存保存根目录，置空使用默认
     */
    private String hlsSavePath;

    /**
     * 该流是否开启时间戳覆盖(0:绝对时间戳/1:系统时间戳/2:相对时间戳)
     */
    private int modifyStamp;

    /**
     * 无人观看是否自动关闭流(不触发无人观看hook)
     */
    private boolean autoClose;


}
