package org.nkk.sip.core.builder;

import gov.nist.javax.sip.header.SubscriptionState;
import gov.nist.javax.sip.message.SIPRequest;
import gov.nist.javax.sip.message.SIPResponse;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.nkk.common.utils.SipContextHolder;
import org.nkk.sip.beans.model.base.ToDevice;
import org.nkk.sip.beans.model.device.Dto.SipTransactionInfo;
import org.nkk.sip.config.SipConfig;
import org.nkk.sip.core.cmd.CallMessage;
import org.nkk.sip.core.sdp.GB28181Description;
import org.nkk.sip.core.sdp.GB28181SDPBuilder;
import org.nkk.sip.core.sdp.media.MediaStreamMode;
import org.nkk.sip.core.service.FutureEvent;
import org.nkk.sip.core.service.SipPublisher;
import org.nkk.sip.utils.DigestAuthenticationHelper;
import org.nkk.sip.utils.SipUtil;

import javax.sdp.Connection;
import javax.sip.address.Address;
import javax.sip.address.SipURI;
import javax.sip.header.AuthorizationHeader;
import javax.sip.header.Header;
import javax.sip.header.WWWAuthenticateHeader;
import javax.sip.message.Request;
import java.util.Date;
import java.util.Objects;

import static org.nkk.sip.core.builder.SipBuilder.GB_VERSION;

/**
 * SIP请求构建器
 *
 * @author nkk
 * @date 2024/07/08
 */
@SuppressWarnings("Duplicates")
@Slf4j(topic = "sipRequest")
public class RequestBuilder {
    @Setter
    public static int DEFAULT_MAX_FORWARD = 70;

    private final ToDevice toDevice;

    /**
     * sip
     */
    private final SipConfig.SipServerConf sipConf;

    /**
     * 媒体ip
     */
    private final String mediaIp;

    protected RequestBuilder(ToDevice toDevice) {
        this.toDevice = toDevice;
        // 本地
        SipConfig sipConfig = SipContextHolder.getBean(SipConfig.class);
        this.sipConf = sipConfig.getServer();
        this.mediaIp = sipConfig.getMedia().getIp();
    }


    @SneakyThrows
    private Request request(String method) {
        // to
        SipURI toUri = SipUtil.createSipURI(toDevice.getChannelId(), SipUtil.createHostAddress(toDevice.getIp(), toDevice.getPort()));
        Address toAddress = SipUtil.createAddress(toUri);

        // from
        Address fromAddress = SipUtil.createAddress(SipUtil.createSipURI(sipConf.getId(), sipConf.getDomain()));

        Request request = SipUtil.getMessageFactory().createRequest(toUri, method,
                SipUtil.createCallIdHeader(SipUtil.getNewCallId(toDevice.getTransport())),
                SipUtil.createCSeqHeader(SipUtil.getMessageSeq(), method),
                SipUtil.createFromHeader(fromAddress, SipUtil.generateFromTag()),
                SipUtil.createToHeader(toAddress),
                SipUtil.createViaHeaders(sipConf.getIp(), sipConf.getPort(), toDevice.getTransport(), SipUtil.generateViaTag()),
                SipUtil.createMaxForwardsHeader(DEFAULT_MAX_FORWARD));

        // 连接头信息
        String local = SipUtil.createHostAddress(sipConf.getIp(), sipConf.getPort());
        Address localContact = SipUtil.createAddress(SipUtil.createSipURI(sipConf.getId(), local));
        request.addHeader(SipUtil.getHeaderFactory().createContactHeader(localContact));
        return request;
    }


    @SneakyThrows
    private Request request(String method, GB28181Description description) {
        // to
        SipURI toUri = SipUtil.createSipURI(toDevice.getChannelId(), SipUtil.createHostAddress(toDevice.getIp(), toDevice.getPort()));
        Address toAddress = SipUtil.createAddress(toUri);
        // from
        Address fromAddress = SipUtil.createAddress(SipUtil.createSipURI(sipConf.getId(), sipConf.getDomain()));

        Request request = SipUtil.getMessageFactory().createRequest(toUri, method,
                SipUtil.createCallIdHeader(SipUtil.getNewCallId(toDevice.getTransport())),
                SipUtil.createCSeqHeader(SipUtil.getMessageSeq(), method),
                SipUtil.createFromHeader(fromAddress, SipUtil.generateFromTag()),
                SipUtil.createToHeader(toAddress),
                SipUtil.createViaHeaders(sipConf.getIp(), sipConf.getPort(), toDevice.getTransport(), SipUtil.generateViaTag()),
                SipUtil.createMaxForwardsHeader(DEFAULT_MAX_FORWARD), SipContentType.SDP, description.toString());

        // 连接头信息
        String local = SipUtil.createHostAddress(sipConf.getIp(), sipConf.getPort());
        Address localContact = SipUtil.createAddress(SipUtil.createSipURI(sipConf.getId(), local));
        request.addHeader(SipUtil.getHeaderFactory().createContactHeader(localContact));
        return request;
    }

    @SneakyThrows
    private Request request(String method, byte[] content) {
        // to
        SipURI toUri = SipUtil.createSipURI(toDevice.getChannelId(), SipUtil.createHostAddress(toDevice.getIp(), toDevice.getPort()));
        Address toAddress = SipUtil.createAddress(toUri);
        // from
        Address fromAddress = SipUtil.createAddress(SipUtil.createSipURI(sipConf.getId(), sipConf.getDomain()));


        Request request = SipUtil.getMessageFactory().createRequest(toUri, method,
                SipUtil.getNewCallIdHeader(toDevice.getTransport()),
                SipUtil.createCSeqHeader(SipUtil.getMessageSeq(), method),
                SipUtil.createFromHeader(fromAddress, SipUtil.generateFromTag()),
                SipUtil.createToHeader(toAddress),
                SipUtil.createViaHeaders(sipConf.getIp(), sipConf.getPort(), toDevice.getTransport(), SipUtil.generateViaTag()),
                SipUtil.createMaxForwardsHeader(DEFAULT_MAX_FORWARD), SipContentType.XML, content);

        // 连接头信息
        String local = SipUtil.createHostAddress(sipConf.getIp(), sipConf.getPort());
        Address localContact = SipUtil.createAddress(SipUtil.createSipURI(sipConf.getId(), local));
        request.addHeader(SipUtil.getHeaderFactory().createContactHeader(localContact));
        return request;
    }


    public TransportBuilder ofRequest(String method, byte[] content) {
        Request request = this.request(method, content);
        return new TransportBuilder(request);
    }

    public TransportBuilder ofRequest(String method) {
        Request request = this.request(method);
        return new TransportBuilder(request);
    }


    /**
     * 构建结束请求
     *
     * @return {@link TransportBuilder}
     */
    @SneakyThrows
    public TransportBuilder ofByeRequest(SipTransactionInfo transactionInfo) {
        Objects.requireNonNull(transactionInfo, "事务信息为空");
        String method = Request.BYE;

        // to
        SipURI toUri = SipUtil.createSipURI(toDevice.getChannelId(), SipUtil.createHostAddress(toDevice.getIp(), toDevice.getPort()));
        Address toAddress = SipUtil.createAddress(toUri);

        // from
        Address fromAddress = SipUtil.createAddress(SipUtil.createSipURI(sipConf.getId(), sipConf.getDomain()));

        Request request = SipUtil.getMessageFactory().createRequest(toUri, method,
                SipUtil.createCallIdHeader(transactionInfo.getCallId()),
                SipUtil.createCSeqHeader(SipUtil.getMessageSeq(), Request.BYE),
                SipUtil.createFromHeader(fromAddress, transactionInfo.getFromTag()),
                SipUtil.createToHeader(toAddress, SipUtil.getNewTag()),
                SipUtil.createViaHeaders(sipConf.getIp(), sipConf.getPort(), toDevice.getTransport(), transactionInfo.getViaBranch()),
                SipUtil.createMaxForwardsHeader(DEFAULT_MAX_FORWARD));

        // 连接头信息
        String local = SipUtil.createHostAddress(sipConf.getIp(), sipConf.getPort());
        Address localContact = SipUtil.createAddress(SipUtil.createSipURI(sipConf.getId(), local));
        request.addHeader(SipUtil.getHeaderFactory().createContactHeader(localContact));

        return new TransportBuilder(request);

    }

    /**
     * 建立未认证请求
     *
     * @param expires 到期
     * @return {@link TransportBuilder}
     */
    @SneakyThrows
    public TransportBuilder ofNoAuthorizationRequest(int expires) {
        SIPRequest request = (SIPRequest) request(Request.REGISTER);
        Address address = request.getFromHeader().getAddress();
        request.getToHeader().setAddress(address);

        request.addHeader(SipUtil.getHeaderFactory().createExpiresHeader(expires));
        return new TransportBuilder(request);
    }

    @SneakyThrows
    public TransportBuilder ofAuthorizationRequest(int expires, String id, String passwd, long cSeq, WWWAuthenticateHeader wwwAuthenticateHeader) {
        SIPRequest request = (SIPRequest) request(Request.REGISTER);
        Address address = request.getFromHeader().getAddress();
        request.getToHeader().setAddress(address);
        request.getCSeq().setSeqNumber(cSeq);

        request.addHeader(SipUtil.getHeaderFactory().createExpiresHeader(expires));

        AuthorizationHeader authorization = DigestAuthenticationHelper
                .createAuthorization(Request.REGISTER, toDevice.getIp(), toDevice.getPort(), toDevice.getDeviceId(), id, passwd, (int) cSeq, wwwAuthenticateHeader);
        request.addHeader(authorization);

        return new TransportBuilder(request);
    }

    @SneakyThrows
    public TransportBuilder ofSubscribeRequest(String event, byte[] content) {
        SIPRequest request = (SIPRequest) this.request(Request.SUBSCRIBE, content);
        request.addHeader(SipUtil.getHeaderFactory().createEventHeader(event));
        return new TransportBuilder(request);
    }

    @SneakyThrows
    public TransportBuilder ofSubscribeRequest(String event, byte[] content, int expires) {
        SIPRequest request = (SIPRequest) this.request(Request.SUBSCRIBE, content);
        request.addHeader(SipUtil.getHeaderFactory().createEventHeader(event));
        request.addHeader(SipUtil.getHeaderFactory().createExpiresHeader(expires));
        return new TransportBuilder(request);
    }

    @SneakyThrows
    public TransportBuilder ofNotifyRequest(String event, byte[] content, String toTag) {
        SIPRequest request = (SIPRequest) this.request(Request.NOTIFY, content);
        if (StringUtils.isNotBlank(toTag)) {
            request.getToHeader().setTag(toTag);
        }
        request.addHeader(SipUtil.getHeaderFactory().createEventHeader(event));
        request.addHeader(SipUtil.getHeaderFactory().createSubscriptionStateHeader("active"));
        return new TransportBuilder(request);
    }

    @SneakyThrows
    public TransportBuilder ofNotifyRequest(String event, byte[] content, String toTag, int expires) {
        SIPRequest request = (SIPRequest) this.request(Request.NOTIFY, content);
        if (StringUtils.isNotBlank(toTag)) {
            request.getToHeader().setTag(toTag);
        }
        request.addHeader(SipUtil.getHeaderFactory().createEventHeader(event));
        request.addHeader(SipUtil.getHeaderFactory().createSubscriptionStateHeader("active"));

        SubscriptionState subscriptionState = (SubscriptionState) request.getHeader(SubscriptionState.NAME);
        subscriptionState.setExpires(expires);

        return new TransportBuilder(request);
    }

    @SneakyThrows
    public TransportBuilder ofMessageRequest(byte[] content, String toTag) {
        SIPRequest request = (SIPRequest) this.request(Request.MESSAGE, content);
        if (StringUtils.isNotBlank(toTag)) {
            request.getToHeader().setTag(toTag);
        }
        return new TransportBuilder(request);
    }

    @SneakyThrows
    public TransportBuilder ofMessageRequest(byte[] content) {
        return ofMessageRequest(content, "");
    }


    /**
     * 发送播放请求
     *
     * @param rtpPort         流媒体分配的端口
     * @param ssrc
     * @param mediaStreamMode 模式
     * @param receiveId       流编号
     * @return
     */
    @SneakyThrows
    public TransportBuilder ofPlayInviteRequest(int rtpPort, String ssrc, MediaStreamMode mediaStreamMode, String receiveId) {

        GB28181Description description = GB28181SDPBuilder.Receiver.play(toDevice.getDeviceId(), toDevice.getChannelId(), Connection.IP4, mediaIp, rtpPort, ssrc, mediaStreamMode);
        SIPRequest request = (SIPRequest) request(Request.INVITE, description);

        String subject = StringUtils.joinWith(",",
                // 发送者 channelId:流序号
                StringUtils.joinWith(":", toDevice.getChannelId(), ssrc),
                // 接收者 id:流序号号
                StringUtils.joinWith(":", sipConf.getId(), receiveId));
        request.addHeader(SipUtil.getHeaderFactory().createSubjectHeader(subject));
        return new TransportBuilder(request);
    }

    @SneakyThrows
    public TransportBuilder ofPlayBackInviteRequest(int rtpPort, String ssrc, MediaStreamMode mediaStreamMode, String receiveId, Date startTime, Date endTime) {

        GB28181Description description = GB28181SDPBuilder.Receiver.playback(toDevice.getDeviceId(), toDevice.getChannelId(), Connection.IP4, mediaIp, rtpPort, ssrc, mediaStreamMode, startTime, endTime);
        SIPRequest request = (SIPRequest) request(Request.INVITE, description);

        String subject = StringUtils.joinWith(",",
                // 发送者 channelId:流序号
                StringUtils.joinWith(":", toDevice.getChannelId(), ssrc),
                // 接收者 id:流序号号
                StringUtils.joinWith(":", sipConf.getId(), receiveId));
        request.addHeader(SipUtil.getHeaderFactory().createSubjectHeader(subject));


        return new TransportBuilder(request);
    }

    @SneakyThrows
    public TransportBuilder ofAckRequest(SIPResponse sipResponse) {

        String target = SipUtil.createHostAddress(toDevice.getIp(), toDevice.getPort());
        SipURI targetUri = SipUtil.createSipURI(toDevice.getDeviceId(), target);

        String method = Request.ACK;

        Request request = SipUtil.getMessageFactory().createRequest(targetUri, method,
                sipResponse.getCallIdHeader(),
                SipUtil.createCSeqHeader(sipResponse.getCSeqHeader().getSeqNumber(), method),
                sipResponse.getFromHeader(),
                sipResponse.getToHeader(),
                SipUtil.createViaHeaders(sipConf.getIp(), sipConf.getPort(), sipResponse.getTopmostViaHeader().getTransport(), SipUtil.generateViaTag()),
                SipUtil.createMaxForwardsHeader(DEFAULT_MAX_FORWARD));

        return new TransportBuilder(request);
    }

    @SneakyThrows
    public TransportBuilder ofDownloadInviteRequest(String rtpIp, int rtpPort, String ssrc,
                                                    MediaStreamMode mediaStreamMode, String receiveId, Date startTime, Date endTime, Double downloadSpeed) {

        GB28181Description description = GB28181SDPBuilder.Receiver.download(toDevice.getDeviceId(), toDevice.getChannelId(), Connection.IP4, rtpIp, rtpPort, ssrc, mediaStreamMode, startTime, endTime, downloadSpeed);
        SIPRequest request = (SIPRequest) request(Request.INVITE, description);

        // 头信息
        String local = SipUtil.createHostAddress(sipConf.getIp(), sipConf.getPort());
        Address fromAddress = SipUtil.createAddress(SipUtil.createSipURI(sipConf.getId(), local));
        request.addHeader(SipUtil.getHeaderFactory().createContactHeader(fromAddress));
        String subject = StringUtils.joinWith(",",
                // 发送者 channelId:流序号
                StringUtils.joinWith(":", toDevice.getChannelId(), ssrc),
                // 接收者 id:流序号号
                StringUtils.joinWith(":", sipConf.getId(), receiveId));
        request.addHeader(SipUtil.getHeaderFactory().createSubjectHeader(subject));

        return new TransportBuilder(request);
    }


    /**
     * 请求构建器 TransportBuilder
     *
     * @author nkk
     * @date 2024/07/01
     */
    public static class TransportBuilder {

        private final Request request;

        /**
         * 订阅 key
         */
        private String subscribeKey;

        public TransportBuilder(Request request) {
            this.request = request;
            this.request.addHeader(GB_VERSION);
        }

        public TransportBuilder addHeader(Header header) {
            this.request.addHeader(header);
            return this;
        }

        /**
         * 订阅结果，调用{@link #executeAsync()} 获取返回结果
         *
         * @param key 关键
         */
        public TransportBuilder subscribeResult(String key) {
            this.subscribeKey = key;
            return this;
        }

        /**
         * 返回请求对象
         *
         * @return {@link Request}
         */
        public Request build() {
            return this.request;
        }

        /**
         * 执行请求
         * 不在乎结果,只发送请求
         */
        public Request execute() {
            SipBuilder.callMessage(this.request).call(this.subscribeKey).exec();
            return this.request;
        }

        /**
         * 执行异步请求
         * <p>等待结果处理,需要自定义设置订阅key</p>
         */
        public FutureEvent executeAsync() throws IllegalAccessException {
            if (StringUtils.isEmpty(this.subscribeKey)) {
                throw new IllegalAccessException("请设置订阅的Key");
            }
            SipBuilder.callMessage(this.request).call(this.subscribeKey).exec();
            return SipPublisher.subscribe(this.subscribeKey).build();
        }

    }

}
