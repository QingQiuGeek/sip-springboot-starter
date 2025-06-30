package org.nkk.media.hook.service;


import org.nkk.media.beans.entity.ServerNodeConfig;
import org.nkk.media.hook.param.*;


/**
 * ZLM钩子服务
 *
 * @author nkk
 * @date 2024/07/10
 */
public interface ZlmHookService {

    /**
     * 在服务器上保持在线
     *
     * @param param 参数
     */
    void onServerKeepLive(OnServerKeepaliveHookParam param);

    /**
     * 播放
     *
     * @param param 参数
     * @return {@link HookResult}
     */
    HookResult onPlay(OnPlayHookParam param);

    /**
     * 在发布
     *
     * @param param 参数
     * @return {@link HookResultForOnPublish}
     */
    HookResultForOnPublish onPublish(OnPublishHookParam param);

    /**
     * 流改变了
     *
     * @param param 参数
     */
    void onStreamChanged(OnStreamChangedHookParam param);

    /**
     * 流上无读取器
     *
     * @param param 参数
     * @return {@link HookResultForStreamNoneReader}
     */
    HookResultForStreamNoneReader onStreamNoneReader(OnStreamNoneReaderHookParam param);

    /**
     * 未找到流
     *
     * @param param 参数
     */
    void onStreamNotFound(OnStreamNotFoundHookParam param);

    /**
     * 服务器启动
     *
     * @param param 参数
     */
    void onServerStarted(ServerNodeConfig param);

    /**
     * On send RTP停止
     *
     * @param param 参数
     */
    void onSendRtpStopped(OnSendRtpStoppedHookParam param);

    /**
     * RTP服务器超时
     *
     * @param param 参数
     */
    void onRtpServerTimeout(OnRtpServerTimeoutHookParam param);

    /**
     * 关于HTTP访问
     *
     * @param param 参数
     * @return {@link HookResultForOnHttpAccess}
     */
    HookResultForOnHttpAccess onHttpAccess(OnHttpAccessParam param);

    /**
     * 在RTSP领域
     *
     * @param param 参数
     * @return {@link HookResultForOnRtspRealm}
     */
    HookResultForOnRtspRealm onRtspRealm(OnRtspRealmHookParam param);

    /**
     * 在RTSP授权上
     *
     * @param param 参数
     * @return {@link HookResultForOnRtspAuth}
     */
    HookResultForOnRtspAuth onRtspAuth(OnRtspAuthHookParam param);

    /**
     * 关于流量报告
     *
     * @param param 参数
     */
    void onFlowReport(OnFlowReportHookParam param);

    /**
     * 服务器已退出
     *
     * @param param 参数
     */
    void onServerExited(HookParam param);

    /**
     * mp4录制完成
     *
     * @param param 参数
     */
    void onRecordMp4(OnRecordMp4HookParam param);
}
