package org.nkk.sip;

import cn.hutool.core.util.StrUtil;
import gov.nist.javax.sip.SipProviderImpl;
import gov.nist.javax.sip.SipStackImpl;
import lombok.extern.slf4j.Slf4j;
import org.nkk.common.utils.SipContextHolder;
import org.nkk.sip.beans.constants.SipConstant;
import org.nkk.sip.config.SIPDefaultProperties;
import org.nkk.sip.config.SipConfig;
import org.nkk.sip.core.parser.GbStringMsgParserFactory;
import org.nkk.sip.core.process.SipListenerImpl;
import org.nkk.sip.core.process.method.impl.ByeRequestProcessor;
import org.nkk.sip.core.process.method.impl.InviteRequestProcessor;
import org.nkk.sip.core.process.method.impl.NotifyRequestProcessor;
import org.nkk.sip.core.process.method.impl.RegisterRequestProcessor;
import org.nkk.sip.core.process.method.impl.message.MessageRequestProcessor;
import org.nkk.sip.core.service.SipEventProcess;
import org.nkk.sip.core.service.SipMessageTemplate;
import org.nkk.sip.core.service.impl.DefaultSipEventProcess;
import org.nkk.sip.core.session.cache.SipCacheManager;
import org.nkk.sip.core.session.cache.impl.DefaultSipCacheManger;
import org.nkk.sip.core.session.impl.InviteManager;
import org.nkk.sip.core.session.impl.ProxyManager;
import org.nkk.sip.core.session.impl.SessionManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;

import javax.sip.*;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.TooManyListenersException;

@Slf4j
@EnableConfigurationProperties(SipConfig.class)
@Import({
        SipContextHolder.class,
        SipListenerImpl.class,
        RegisterRequestProcessor.class,
        MessageRequestProcessor.class,
        SipMessageTemplate.class,
        InviteRequestProcessor.class,
        NotifyRequestProcessor.class,
        ByeRequestProcessor.class,
        SessionManager.class,
        InviteManager.class,
        ProxyManager.class
})
public class SipAutoConfiguration {

    @Bean("sipFactory")
    @ConditionalOnWebApplication
    public SipFactory sipFactory() {
        SipFactory instance = SipFactory.getInstance();
        instance.setPathName("gov.nist");
        return instance;
    }

    @Bean("sipStack")
    @DependsOn("sipFactory")
    public SipStackImpl createSipStackImpl(SipConfig sipConfig, SipFactory sipFactory, ApplicationContext context) throws PeerUnavailableException {
        SipConfig.SipServerConf server = sipConfig.getServer();
        if (Objects.isNull(server) || StrUtil.isEmpty(server.getDomain()) || StrUtil.isEmpty(server.getId())) {
            throw new RuntimeException("sip「id」或「domain」不能为空");
        }
        String ip = StrUtil.isEmpty(server.getIp()) ? "0.0.0.0" : server.getIp();
        String level = Optional.ofNullable(sipConfig.getLogs()).orElse("OFF");
        Properties sipProperties = SIPDefaultProperties.getProperties(ip, level);
        SipStackImpl sipStack = (SipStackImpl) sipFactory.createSipStack(sipProperties);
        sipStack.setMessageParserFactory(new GbStringMsgParserFactory());
        sipStack.setStackName("gb_starter");
        return sipStack;
    }


    @Bean("tcpSipProvider")
    @DependsOn("sipStack")
    public SipProviderImpl startTcpListener(SipConfig sipConfig, SipStackImpl sipStack, SipListener sipProcessor) {

        SipConfig.SipServerConf server = sipConfig.getServer();
        String ip = StrUtil.isEmpty(server.getIp()) ? "0.0.0.0" : server.getIp();
        Integer port = server.getPort();

        try {
            ListeningPoint tcpListeningPoint = sipStack.createListeningPoint(ip, port, SipConstant.TransPort.TCP);
            SipProviderImpl tcpSipProvider = (SipProviderImpl) sipStack.createSipProvider(tcpListeningPoint);
            tcpSipProvider.setDialogErrorsAutomaticallyHandled();
            tcpSipProvider.addSipListener(sipProcessor);
            log.info("\033[36;2m「SIP」 TCP://{}:{} 启动成功\033[36;0m", ip, port);
            return tcpSipProvider;
        } catch (TransportNotSupportedException
                 | ObjectInUseException
                 | TooManyListenersException
                 | InvalidArgumentException e) {
            log.error("\033[31;2m[SIP] TCP://{}:{} SIP服务启动失败,请检查端口是否被占用或者ip是否正确\033[31;0m", ip, port);
        }
        return null;
    }


    @Bean("udpSipProvider")
    @DependsOn("sipStack")
    public SipProviderImpl startUdpListener(SipConfig sipConfig, SipStackImpl sipStack, SipListener sipProcessor) {

        SipConfig.SipServerConf server = sipConfig.getServer();
        String ip = StrUtil.isEmpty(server.getIp()) ? "0.0.0.0" : server.getIp();
        Integer port = server.getPort();

        try {
            ListeningPoint udpListeningPoint = sipStack.createListeningPoint(ip, port, SipConstant.TransPort.UDP);
            SipProviderImpl udpSipProvider = (SipProviderImpl) sipStack.createSipProvider(udpListeningPoint);
            udpSipProvider.setDialogErrorsAutomaticallyHandled();
            udpSipProvider.addSipListener(sipProcessor);
            log.info("\033[36;2m「SIP」 UDP://{}:{} 启动成功\033[36;0m", ip, port);
            return udpSipProvider;
        } catch (TransportNotSupportedException
                 | ObjectInUseException
                 | TooManyListenersException
                 | InvalidArgumentException e) {
            log.error("\033[31;2m[SIP] UDP://{}:{} SIP服务启动失败,请检查端口是否被占用或者ip是否正确\033[31;0m", ip, port);
        }
        return null;
    }


    /**
     * Sip事件流程
     *
     * @return {@link SipEventProcess}
     */
    @Bean
    @ConditionalOnMissingBean
    public SipEventProcess sipEventProcess(){
        return new DefaultSipEventProcess();
    }


    /**
     * 配置默认缓存管理器
     *
     * @return {@link SipCacheManager}
     */
    @Bean
    @ConditionalOnMissingBean
    public SipCacheManager<?> sipCacheManager(){
        return new DefaultSipCacheManger<>();
    }
}
