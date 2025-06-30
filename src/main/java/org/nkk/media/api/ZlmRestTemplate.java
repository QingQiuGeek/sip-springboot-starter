package org.nkk.media.api;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.nkk.media.beans.constant.ApiConstants;
import org.nkk.media.beans.entity.*;
import org.nkk.media.beans.entity.req.MediaReq;
import org.nkk.media.beans.entity.req.RecordReq;
import org.nkk.media.beans.entity.req.SnapshotReq;
import org.nkk.media.beans.entity.rtp.*;
import org.nkk.sip.config.SipConfig;
import org.springframework.util.Assert;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


/**
 * ZLM rest接口模板
 *
 * @author nkk
 * @date 2024/07/10
 */
@Slf4j
public class ZlmRestTemplate {

    private final SipConfig.ZlmMedia zlmMedia;


    public ZlmRestTemplate(SipConfig sipConfig) {
        this.zlmMedia = sipConfig.getMedia();
    }

    /**
     * 获取版本信息
     */
    public ServerResponse<Version> getVersion() {
        String s = doApi(ApiConstants.GET_VERSION, new HashMap<>());
        return JSON.parseObject(s, new TypeReference<ServerResponse<Version>>() {
        });
    }


    /**
     * 获取所有API列表
     *
     * @return {@link ServerResponse}<{@link List}<{@link String}>>
     */
    public ServerResponse<List<String>> getApiList() {
        return getApiList(new HashMap<>());
    }

    /**
     * 获取流媒体服务器列表
     */
    public ServerResponse<List<String>> getApiList(Map<String, Object> params) {
        String s = doApi(ApiConstants.GET_API_LIST, params);
        return JSON.parseObject(s, new TypeReference<ServerResponse<List<String>>>() {
        });
    }

    /**
     * 获取网络线程负载
     */
    public ServerResponse<List<ThreadLoad>> getThreadsLoad() {
        String s = doApi(ApiConstants.GET_THREADS_LOAD, new HashMap<>());
        return JSON.parseObject(s, new TypeReference<ServerResponse<List<ThreadLoad>>>() {
        });
    }

    /**
     * 获取主要对象个数
     */
    public ServerResponse<ImportantObjectNum> getStatistic() {
        String s = doApi(ApiConstants.GET_STATISTIC, new HashMap<>());
        return JSON.parseObject(s, new TypeReference<ServerResponse<ImportantObjectNum>>() {
        });
    }

    /**
     * 获取后台线程负载
     */
    public ServerResponse<List<ThreadLoad>> getWorkThreadsLoad() {
        String s = doApi(ApiConstants.GET_WORK_THREADS_LOAD, new HashMap<>());
        return JSON.parseObject(s, new TypeReference<ServerResponse<List<ThreadLoad>>>() {
        });
    }

    /**
     * 获取服务器配置
     */
    public ServerResponse<List<ServerNodeConfig>> getServerConfig() {
        String s = doApi(ApiConstants.GET_SERVER_CONFIG, new HashMap<>());
        return JSON.parseObject(s, new TypeReference<ServerResponse<List<ServerNodeConfig>>>() {
        });
    }

    /**
     * 设置服务器配置
     */
    public ServerResponse<String> setServerConfig(Map<String, Object> params) {
        String s = doApi(ApiConstants.SET_SERVER_CONFIG, params);
        return JSON.parseObject(s, new TypeReference<ServerResponse<String>>() {
        });
    }

    /**
     * 重启服务器
     * 重启服务器,只有Daemon方式才能重启，否则是直接关闭！
     */
    public ServerResponse<Object> restartServer(Map<String, Object> params) {
        String s = doApi(ApiConstants.RESTART_SERVER, params);
        return JSON.parseObject(s, new TypeReference<ServerResponse<Object>>() {
        });
    }

    /**
     * 获取媒体列表
     *
     * @param mediaReq 媒体点播
     * @return {@link ServerResponse}<{@link List}<{@link MediaData}>>
     */
    public ServerResponse<List<MediaData>> getMediaList(MediaReq mediaReq) {
        String s = doApi(ApiConstants.GET_MEDIA_LIST, mediaReq.toMap());
        return JSON.parseObject(s, new TypeReference<ServerResponse<List<MediaData>>>() {
        });
    }


    /**
     * 关断单个流
     *
     * @param mediaReq 媒体点播
     * @return {@link ServerResponse}<{@link String}>
     */
    public ServerResponse<String> closeStream(MediaReq mediaReq) {
        String s = doApi(ApiConstants.CLOSE_STREAM, mediaReq.toMap());
        return JSON.parseObject(s, new TypeReference<ServerResponse<String>>() {
        });
    }


    /**
     * TODO 批量关断流
     */
    public ServerResponse<?> closeStreams(Map<String, Object> params) {
        String s = doApi(ApiConstants.CLOSE_STREAMS, params);
        return JSON.parseObject(s, new TypeReference<ServerResponse>() {
        });
    }

    /**
     * 获取所有TcpSession列表(获取所有tcp客户端相关信息)
     *
     * @param localPort 筛选本机端口，例如筛选rtsp链接：554
     * @param peerIp    筛选客户端ip
     * @return
     */
    public ServerResponse<List<TcpLink>> getAllSession(String localPort, String peerIp) {
        Map<String, Object> params = new HashMap<>();
        params.put("local_port", localPort);
        params.put("peer_ip", peerIp);
        return getAllSession(params);
    }

    /**
     * 获取Session列表
     */
    public ServerResponse<List<TcpLink>> getAllSession(Map<String, Object> params) {
        String s = doApi(ApiConstants.GET_ALL_SESSION, params);
        return JSON.parseObject(s, new TypeReference<ServerResponse<List<TcpLink>>>() {
        });
    }

    /**
     * 断开tcp连接
     */
    public ServerResponse<String> kickSession(String sessionId) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", sessionId);
        String s = doApi(ApiConstants.KICK_SESSION, params);
        return JSON.parseObject(s, new TypeReference<ServerResponse<String>>() {
        });
    }

    /**
     * 批量断开tcp连接
     */
    public ServerResponse<String> kickSessions(Map<String, Object> params) {
        String s = doApi(ApiConstants.KICK_SESSIONS, params);
        return JSON.parseObject(s, new TypeReference<ServerResponse<String>>() {
        });
    }

    /**
     * 添加代理拉流
     *
     * @param streamProxyItem
     * @return
     */
    public ServerResponse<StreamKey> addStreamProxy(StreamProxyItem streamProxyItem) {
        String s = doApi(ApiConstants.ADD_STREAM_PROXY, streamProxyItem.toMap());
        return JSON.parseObject(s, new TypeReference<ServerResponse<StreamKey>>() {
        });
    }


    /**
     * 关闭拉流代理
     */
    public ServerResponse<StreamKey.StringDelFlag> delStreamProxy(String key) {
        Map<String, Object> params = MapUtil.newHashMap();
        params.put("key", key);
        String s = doApi(ApiConstants.DEL_STREAM_PROXY, params);
        return JSON.parseObject(s, new TypeReference<ServerResponse<StreamKey.StringDelFlag>>() {
        });
    }

    /**
     * 添加rtsp/rtmp推流
     *
     * @param streamPusherItem 推流项目
     * @return {@link ServerResponse}<{@link StreamKey}>
     */
    public ServerResponse<StreamKey> addStreamPusherProxy(StreamPusherItem streamPusherItem) {
        String s = doApi(ApiConstants.ADD_STREAM_PUSHER_PROXY, streamPusherItem.toMap());
        return JSON.parseObject(s, new TypeReference<ServerResponse<StreamKey>>() {
        });
    }


    /**
     * 关闭推流
     */
    public ServerResponse<StreamKey.StringDelFlag> delStreamPusherProxy(String key) {
        Map<String, Object> params = MapUtil.newHashMap();
        params.put("key", key);
        String s = doApi(ApiConstants.DEL_STREAM_PUSHER_PROXY, params);
        return JSON.parseObject(s, new TypeReference<ServerResponse<StreamKey.StringDelFlag>>() {
        });
    }

    /**
     * 添加FFmpeg拉流代理
     *
     * @param streamFfmpegItem 流ffmpeg项
     * @return {@link ServerResponse}<{@link StreamKey}>
     */
    public ServerResponse<StreamKey> addFFmpegSource(StreamFfmpegItem streamFfmpegItem) {
        String s = doApi(ApiConstants.ADD_FFMPEG_SOURCE, streamFfmpegItem.toMap());
        return JSON.parseObject(s, new TypeReference<ServerResponse<StreamKey>>() {
        });
    }


    /**
     * 关闭FFmpeg拉流代理
     */
    public ServerResponse<StreamKey.StringDelFlag> delFFmpegSource(String key) {
        Map<String, Object> params = MapUtil.newHashMap();
        params.put("key", key);
        String s = doApi(ApiConstants.DEL_FFMPEG_SOURCE, params);
        return JSON.parseObject(s, new TypeReference<ServerResponse<StreamKey.StringDelFlag>>() {
        });
    }

    /**
     * 流是否在线
     */
    public MediaOnlineStatus isMediaOnline(MediaReq mediaReq) {
        String s = doApi(ApiConstants.IS_MEDIA_ONLINE, mediaReq.toMap());
        return JSON.parseObject(s, new TypeReference<MediaOnlineStatus>() {
        });
    }


    /**
     * 获取媒体流播放器列表
     */
    public ServerResponse<MediaPlayer> getMediaPlayerList(MediaReq mediaReq) {
        String s = doApi(ApiConstants.GET_MEDIA_PLAYER_LIST, mediaReq.toMap());
        return JSON.parseObject(s, new TypeReference<ServerResponse<MediaPlayer>>() {
        });
    }


    /**
     * 广播webrtc datachannel消息
     */
    public ServerResponse<?> broadcastMessage(Map<String, Object> params) {
        String s = doApi(ApiConstants.BROADCAST_MESSAGE, params);
        return JSON.parseObject(s, new TypeReference<ServerResponse>() {
        });
    }

    /**
     * 获取流信息
     */
    public ServerResponse<MediaInfo> getMediaInfo(MediaReq mediaReq) {
        String s = doApi(ApiConstants.GET_MEDIA_INFO, mediaReq.toMap());
        ServerResponse<MediaInfo> response = JSON.parseObject(s, new TypeReference<ServerResponse<MediaInfo>>() {
        });
        if (response.getCode() != 0) {
            return response;
        }
        MediaInfo mediaInfo = JSON.parseObject(s, new TypeReference<MediaInfo>() {
        });
        response.setData(mediaInfo);
        return response;
    }


    /**
     * 获取流信息
     */
    public ServerResponse<Mp4RecordFile> getMp4RecordFile(RecordReq recordReq) {
        String s = doApi(ApiConstants.GET_MP4_RECORD_FILE, recordReq.toMap());
        return JSON.parseObject(s, new TypeReference<ServerResponse<Mp4RecordFile>>() {
        });
    }

    /**
     * 删除录像文件夹
     */
    public DeleteRecordDirectory deleteRecordDirectory(Map<String, Object> params) {
        String s = doApi(ApiConstants.DELETE_RECORD_DIRECTORY, params);
        return JSON.parseObject(s, new TypeReference<DeleteRecordDirectory>() {
        });
    }


    /**
     * 开始录制
     */
    public ServerResponse<String> startRecord(RecordReq recordReq) {
        String s = doApi(ApiConstants.START_RECORD, recordReq.toMap());
        return JSON.parseObject(s, new TypeReference<ServerResponse<String>>() {
        });
    }

    /**
     * 设置录像速度
     *
     * @param recordReq 记录要求
     * @return {@link ServerResponse}<{@link String}>
     */
    public ServerResponse<String> setRecordSpeed(RecordReq recordReq) {
        String s = doApi(ApiConstants.SET_RECORD_SPEED, recordReq.toMap());
        return JSON.parseObject(s, new TypeReference<ServerResponse<String>>() {
        });
    }


    /**
     * 设置录像流播放位置
     */
    public ServerResponse<String> seekRecordStamp(RecordReq recordReq) {
        String s = doApi(ApiConstants.SEEK_RECORD_STAMP, recordReq.toMap());
        return JSON.parseObject(s, new TypeReference<ServerResponse<String>>() {
        });
    }


    /**
     * 停止录制
     */
    public ServerResponse<String> stopRecord(RecordReq recordReq) {
        String s = doApi(ApiConstants.STOP_RECORD, recordReq.toMap());
        return JSON.parseObject(s, new TypeReference<ServerResponse<String>>() {
        });
    }


    /**
     * 是否正在录制
     */
    public ServerResponse<String> isRecording(RecordReq recordReq) {
        String s = doApi(ApiConstants.IS_RECORDING, recordReq.toMap());
        return JSON.parseObject(s, new TypeReference<ServerResponse<String>>() {
        });
    }

    /**
     * 获取截图
     */
    public String getSnap(SnapshotReq snapshotReq) {
        Path path = Paths.get(snapshotReq.getSavePath());
        boolean exists = Files.exists(path);
        if (!exists) {
            try {
                Files.createFile(path);
            } catch (IOException e) {
                return null;
            }
        }
        return doApiImg(ApiConstants.GET_SNAP, snapshotReq.toMap(), path.toFile());
    }

    /**
     * 查询文件概览
     */
    public ServerResponse<?> getMp4RecordSummary(Map<String, Object> params) {
        String s = doApi(ApiConstants.GET_MP4_RECORD_SUMMARY, params);
        return JSON.parseObject(s, new TypeReference<ServerResponse>() {
        });
    }

    /**
     * 获取rtp推流信息
     *
     * @param streamId RTP的ssrc，16进制字符串或者是流的id(openRtpServer接口指定)
     */
    public RtpInfoResult getRtpInfo(String streamId) {
        Map<String, Object> params = new HashMap<>();
        params.put("stream_id", streamId);
        String s = doApi(ApiConstants.GET_RTP_INFO, params);
        return JSON.parseObject(s, new TypeReference<RtpInfoResult>() {
        });
    }


    /**
     * 创建RTP服务器
     */
    public OpenRtpServerResult openRtpServer(OpenRtpServerReq req) {
        String s = doApi(ApiConstants.OPEN_RTP_SERVER, req.toMap());
        return JSON.parseObject(s, new TypeReference<OpenRtpServerResult>() {
        });
    }


    /**
     * 创建多路复用RTP服务器
     */
    public OpenRtpServerResult openRtpServerMultiplex(OpenRtpServerReq req) {
        String s = doApi(ApiConstants.OPEN_RTP_SERVER_MULTIPLEX, req.toMap());
        return JSON.parseObject(s, new TypeReference<OpenRtpServerResult>() {
        });
    }


    /**
     * 连接RTP服务器
     */
    public OpenRtpServerResult connectRtpServer(ConnectRtpServerReq req) {
        String s = doApi(ApiConstants.CONNECT_RTP_SERVER, req.toMap());
        return JSON.parseObject(s, new TypeReference<OpenRtpServerResult>() {
        });
    }

    /**
     * 关闭RTP服务器
     */
    public CloseRtpServerResult closeRtpServer(String streamId) {
        Map<String, Object> params = new HashMap<>();
        params.put("stream_id", streamId);
        String s = doApi(ApiConstants.CLOSE_RTP_SERVER, params);
        return JSON.parseObject(s, new TypeReference<CloseRtpServerResult>() {
        });
    }

    /**
     * 更新RTP服务器过滤SSRC
     */
    public ServerResponse<String> updateRtpServerSSRC(String streamId, String ssrc) {
        Map<String, Object> params = new HashMap<>();
        params.put("stream_id", streamId);
        params.put("ssrc", ssrc);
        String s = doApi(ApiConstants.UPDATE_RTP_SERVER_SSRC, params);
        return JSON.parseObject(s, new TypeReference<ServerResponse<String>>() {
        });
    }

    /**
     * 暂停RTP超时检查
     */
    public ServerResponse<String> pauseRtpCheck(String streamId) {
        Map<String, Object> params = new HashMap<>();
        params.put("stream_id", streamId);
        String s = doApi(ApiConstants.PAUSE_RTP_CHECK, params);
        return JSON.parseObject(s, new TypeReference<ServerResponse<String>>() {
        });
    }

    /**
     * 恢复RTP超时检查
     */
    public ServerResponse<String> resumeRtpCheck(String streamId) {
        Map<String, Object> params = new HashMap<>();
        params.put("stream_id", streamId);
        String s = doApi(ApiConstants.RESUME_RTP_CHECK, params);
        return JSON.parseObject(s, new TypeReference<ServerResponse<String>>() {
        });
    }

    /**
     * 获取RTP服务器列表
     */
    public ServerResponse<List<RtpServer>> listRtpServer(String host, String secret) {
        Map<String, Object> params = new HashMap<>();
        String s = doApi(ApiConstants.LIST_RTP_SERVER, params);
        return JSON.parseObject(s, new TypeReference<ServerResponse<List<RtpServer>>>() {
        });
    }

    /**
     * 开始发送rtp
     */
    public StartSendRtpResult startSendRtp(StartSendRtpReq req) {
        String s = doApi(ApiConstants.START_SEND_RTP, req.toMap());
        return JSON.parseObject(s, new TypeReference<StartSendRtpResult>() {
        });
    }


    /**
     * 开始tcp passive被动发送rtp
     */
    public StartSendRtpResult startSendRtpPassive(StartSendRtpReq req) {
        String s = doApi(ApiConstants.START_SEND_RTP_PASSIVE, req.toMap());
        return JSON.parseObject(s, new TypeReference<StartSendRtpResult>() {
        });
    }


    /**
     * 停止 发送rtp
     */
    public ServerResponse<String> stopSendRtp(CloseSendRtpReq req) {
        String s = doApi(ApiConstants.STOP_SEND_RTP, req.toMap());
        return JSON.parseObject(s, new TypeReference<ServerResponse<String>>() {
        });
    }

    /**
     * 获取拉流代理信息
     */
    public ServerResponse<?> getProxyInfo(Map<String, Object> params) {
        String s = doApi(ApiConstants.GET_PROXY_INFO, params);
        return JSON.parseObject(s, new TypeReference<ServerResponse>() {
        });
    }

    /**
     * 获取推流代理信息
     */
    public ServerResponse<?> getProxyPusherInfo(Map<String, Object> params) {
        String s = doApi(ApiConstants.GET_PROXY_PUSHER_INFO, params);
        return JSON.parseObject(s, new TypeReference<ServerResponse>() {
        });
    }

    /**
     * 多文件推流
     */
    public ServerResponse<?> startMultiMp4Publish(Map<String, Object> params) {
        String s = doApi(ApiConstants.START_MULTI_MP4_PUBLISH, params);
        return JSON.parseObject(s, new TypeReference<ServerResponse>() {
        });
    }

    /**
     * 获取存储信息
     */
    public ServerResponse<?> getStorageSpace(Map<String, Object> params) {
        String s = doApi(ApiConstants.GET_STORAGE_SPACE, params);
        return JSON.parseObject(s, new TypeReference<ServerResponse>() {
        });
    }

    /**
     * 关闭多文件推流
     */
    public ServerResponse<?> stopMultiMp4Publish(Map<String, Object> params) {
        String s = doApi(ApiConstants.STOP_MULTI_MP4_PUBLISH, params);
        return JSON.parseObject(s, new TypeReference<ServerResponse>() {
        });
    }

    /**
     * 点播mp4文件
     */
    public ServerResponse<?> loadMP4File(Map<String, Object> params) {
        String s = doApi(ApiConstants.LOAD_MP4_FILE, params);
        return JSON.parseObject(s, new TypeReference<ServerResponse>() {
        });
    }

    public String doApi(String path, Map<String, Object> params) {
        Assert.notNull(path, "api is null");

        params = Optional.ofNullable(params).orElse(new HashMap<>());
        params.put("secret", zlmMedia.getSecret());

        return HttpRequest.post(StrUtil.concat(true, zlmMedia.getHost(), path))
                .form(params)
                .execute()
                .body();
    }

    public String doApiImg(String path, Map<String, Object> params, File file) {
        Assert.notNull(path, "api is null");

        params = Optional.ofNullable(params).orElse(new HashMap<>());
        params.put("secret", zlmMedia.getSecret());

        HttpResponse response = HttpUtil.createPost(StrUtil.concat(true, zlmMedia.getHost(), path))
                .form(params)
                .execute();
        byte[] bytes = response.bodyBytes();
        if (bytes == null) {
            return null;
        }
        File distFile = FileUtil.writeBytes(bytes, file);
        return distFile.getAbsolutePath();
    }


}
