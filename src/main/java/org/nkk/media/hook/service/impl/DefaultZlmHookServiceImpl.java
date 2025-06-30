package org.nkk.media.hook.service.impl;


import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.nkk.common.utils.SipContextHolder;
import org.nkk.media.beans.constant.MediaEnum;
import org.nkk.media.beans.entity.ServerNodeConfig;
import org.nkk.media.hook.param.*;
import org.nkk.media.hook.service.ZlmHookService;
import org.nkk.sip.beans.model.base.ToDevice;
import org.nkk.sip.beans.model.invite.InviteStream;
import org.nkk.sip.config.SipConfig;
import org.nkk.sip.config.SipConfig.StreamConf;
import org.nkk.sip.core.sdp.GB28181SDPBuilder;
import org.nkk.sip.core.service.SipMessageTemplate;
import org.nkk.sip.core.session.impl.InviteManager;
import org.nkk.sip.core.session.impl.ProxyManager;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author luna
 * @date 2023/12/5
 */

@Slf4j
public class DefaultZlmHookServiceImpl implements ZlmHookService {

    @Resource
    private SipMessageTemplate sipMessageTemplate;

    @Resource
    private InviteManager inviteManager;

    @Resource
    private ProxyManager proxyManager;

    private final SipConfig.StreamConf streamConf;

    public DefaultZlmHookServiceImpl(StreamConf streamConf) {
        SipConfig sipConfig = SipContextHolder.getBean(SipConfig.class);
        this.streamConf = sipConfig.getStream();
    }

    @Override
    public void onServerKeepLive(OnServerKeepaliveHookParam param) {
        log.debug("「zlm_hook」心跳");
    }

    @Override
    public HookResult onPlay(OnPlayHookParam param) {
        return HookResult.SUCCESS();
    }

    @Override
    public HookResultForOnPublish onPublish(OnPublishHookParam param) {
        log.info("「zlm_hook」推流鉴权事件,\n {}",JSONObject.toJSONString(param));
        //这里睡500ms确保执行processAuth()鉴权时流已经拉过来！！经测试百路流并发，会因为推流和鉴权的先后顺序导致拉流失败
        //正常流程是先推流后鉴权，如果鉴权时流还没拉过来那么就会这个设备就会拉流失败
        //睡500ms只是临时解决，严格来说不能完全解决百路并发问题，随着并发量增加，还会出现因为推流和鉴权的先后顺序导致拉流失败的问题
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
      // 国标点播
        if (StringUtils.equalsIgnoreCase(MediaEnum.App.rtp.name(), param.getApp())) {
            InviteStream data = inviteManager.getData(param.getStream());
            if(Objects.isNull(data)){
                return HookResultForOnPublish.OK_EMPTY();
            }

            HookResultForOnPublish success = HookResultForOnPublish.ofCustom()
                    .setEnableRtmp(streamConf.isEnableRtmp())
                    .setEnableFmp4(streamConf.isEnableFmp4())
                    .setEnableHls(streamConf.isEnableHls())
                    .setEnableRtsp(streamConf.isEnableRtsp())
                    .setEnableTs(streamConf.isEnableTs())
                    .setEnableHlsFmp4(streamConf.isEnableHlsFmp4())
                    .setEnableRecord(data.isEnableMp4Record())
                    .setRecordSplitTime(data.getMp4MaxSecond())
                    .build();
            // 进行鉴权
            inviteManager.processAuth(param);
            if (data.getAction().equalsIgnoreCase(GB28181SDPBuilder.Action.PLAY_BACK.getAction())) {
                success.setModifyStamp(2);
            }
            return success;
        }
        return HookResultForOnPublish.OK_EMPTY();

    }


    @Override
    public void onStreamChanged(OnStreamChangedHookParam param) {
        if (StringUtils.equalsIgnoreCase(MediaEnum.App.rtp.name(), param.getApp())) {
            if (param.isRegist()) {
                log.info("「zlm_hook」国标流注册: {}",param.getApp() + StrUtil.DASHED + param.getSchema() + "://" + param.getStream());
                inviteManager.processOk(param.getStream());
            } else {
                log.info("「zlm_hook」国标流注销: {}",param.getApp() + StrUtil.DASHED + param.getSchema() + "://" + param.getStream());
                inviteManager.releaseData(InviteManager.getKey(param.getStream()));
            }
        }
        // 代理无人观看
        else if (StringUtils.equalsIgnoreCase(MediaEnum.App.proxy.name(), param.getApp())) {
            if (param.isRegist()) {
                log.info("「zlm_hook」代理流注册: {}",param.getApp() + StrUtil.DASHED + param.getSchema() + "://" + param.getStream());
                proxyManager.processOk(param.getStream(), param);
            } else {
                log.info("「zlm_hook」代理流注销: {}",param.getApp() + StrUtil.DASHED + param.getSchema() + "://" + param.getStream());
                // 代理流设置的无限重试，注销后自动拉起。不能移除缓存数据
                // proxyManager.delData(param.getStream());
            }
        }

    }

    @Override
    public HookResultForStreamNoneReader onStreamNoneReader(OnStreamNoneReaderHookParam param) {
        InviteStream data = inviteManager.getData(param.getStream());
        if (StringUtils.equalsIgnoreCase(MediaEnum.App.rtp.name(), param.getApp())) {
            if (Objects.nonNull(data) && data.getAction().equalsIgnoreCase(GB28181SDPBuilder.Action.PLAY.getAction())) {
                log.info("「zlm_hook」实时流无人观看:{}",param.getApp() + StrUtil.DASHED + param.getSchema() + "://" + param.getStream());
            }else if (Objects.nonNull(data) && data.getAction().equalsIgnoreCase(GB28181SDPBuilder.Action.PLAY_BACK.getAction())) {
                log.info("「zlm_hook」回放流无人观看:{}",param.getApp() + StrUtil.DASHED + param.getSchema() + "://" + param.getStream());
            }
        } else if (StringUtils.equalsIgnoreCase(MediaEnum.App.proxy.name(), param.getApp())) {
            log.info("「zlm_hook」代理流无人观看:{}",param.getApp() + StrUtil.DASHED + param.getSchema() + "://" + param.getStream());
        }
        if(streamConf.isAutoClose()){
            ToDevice toDevice = data.getToDevice();
            log.info("发起关闭: {}",param.getApp() + StrUtil.DASHED + param.getSchema() + "://" + param.getStream());
            sipMessageTemplate.closeRtp(toDevice, data.getStreamId());
            return HookResultForStreamNoneReader.CLOSE();
        }
        return HookResultForStreamNoneReader.SUCCESS();
    }

    @Override
    public void onStreamNotFound(OnStreamNotFoundHookParam param) {
        log.info("on-streamNotFound");
        // 清除缓存
        inviteManager.delData(param.getStream());
    }

    @Override
    public void onServerStarted(ServerNodeConfig param) {
        log.info("on-serverStarted");
    }

    @Override
    public void onSendRtpStopped(OnSendRtpStoppedHookParam param) {
        log.info("on-sendRtpStopped");
    }

    @Override
    public void onRtpServerTimeout(OnRtpServerTimeoutHookParam param) {
        log.info("on-rtpServerTimeout");
    }

    @Override
    public HookResultForOnHttpAccess onHttpAccess(OnHttpAccessParam param) {
        log.info("onHttpAccess");
        return HookResultForOnHttpAccess.SUCCESS();
    }

    @Override
    public HookResultForOnRtspRealm onRtspRealm(OnRtspRealmHookParam param) {
        log.info("onRtspRealm");
        return HookResultForOnRtspRealm.SUCCESS();
    }

    @Override
    public HookResultForOnRtspAuth onRtspAuth(OnRtspAuthHookParam param) {
        log.info("onRtspAuth");
        return HookResultForOnRtspAuth.SUCCESS();
    }

    @Override
    public void onFlowReport(OnFlowReportHookParam param) {
        log.info("onFlowReport");
    }

    @Override
    public void onServerExited(HookParam param) {
        log.info("onServerExited");
        inviteManager.removeAll();
        proxyManager.removeAll();
    }

    @Override
    public void onRecordMp4(OnRecordMp4HookParam param) {
        log.info("「zlm_hook」录制文件生成,{}", param.getStream());
    }


}
