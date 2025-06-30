package org.nkk.media.hook.custom;


import org.nkk.media.hook.param.HookResultForOnPublish;

/**
 * 回复播放配置参数建造器
 *
 * @author nkk
 * @date 2024/07/18
 */
public class HookPushBuilder {
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
     * 是否转换成hls-mpegts协议
     */
    private boolean enableHls;

    /**
     * 是否转换成hls-fmp4协议
     */
    private boolean enableHlsFmp4;

    /**
     * 无人观看是否自动关闭流(不触发无人观看hook)
     */
    private boolean autoClose;

    /**
     * 是否允许mp4录制
     */
    private boolean enableRecord;

    /**
     * 录制分割时间 (单位秒)
     */
    private int recordSplitTime;

    /**
     * 转协议时是否开启音频
     */
    private boolean enableAudio;

    public HookPushBuilder setEnableRtsp(boolean enableRtsp) {
        this.enableRtsp = enableRtsp;
        return this;
    }

    public HookPushBuilder setEnableRtmp(boolean enableRtmp) {
        this.enableRtmp = enableRtmp;
        return this;
    }

    public HookPushBuilder setEnableTs(boolean enableTs) {
        this.enableTs = enableTs;
        return this;
    }

    public HookPushBuilder setEnableFmp4(boolean enableFmp4) {
        this.enableFmp4 = enableFmp4;
        return this;
    }

    public HookPushBuilder setEnableHls(boolean enableHls) {
        this.enableHls = enableHls;
        return this;
    }

    public HookPushBuilder setEnableHlsFmp4(boolean enableHlsFmp4) {
        this.enableHlsFmp4 = enableHlsFmp4;
        return this;
    }

    public HookPushBuilder setAutoClose(boolean autoClose) {
        this.autoClose = autoClose;
        return this;
    }

    public HookPushBuilder setEnableRecord(boolean enableRecord) {
        this.enableRecord = enableRecord;
        return this;
    }

    public HookPushBuilder setRecordSplitTime(int recordSplitTime) {
        this.recordSplitTime = recordSplitTime;
        return this;
    }

    public HookPushBuilder setEnableAudio(boolean enableAudio) {
        this.enableAudio = enableAudio;
        return this;
    }

    public HookResultForOnPublish build(){
        HookResultForOnPublish result = new HookResultForOnPublish(0, "success");
        result.setEnableRtmp(this.enableRtmp);
        result.setEnableHlsFmp4(this.enableHlsFmp4);
        result.setEnableHls(this.enableHls);
        result.setEnableFmp4(this.enableFmp4);
        result.setEnableAudio(this.enableAudio);
        result.setEnableTs(this.enableTs);
        result.setEnableMp4(this.enableRecord);
        result.setEnableRtsp(this.enableRtsp);
        result.setAutoClose(this.autoClose);
        result.setMp4MaxSecond(this.recordSplitTime);
        return result;
    }

}
