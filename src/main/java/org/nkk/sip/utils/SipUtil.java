package org.nkk.sip.utils;

import cn.hutool.core.lang.id.NanoId;
import gov.nist.javax.sip.address.AddressImpl;
import gov.nist.javax.sip.address.SipUri;
import gov.nist.javax.sip.header.Subject;
import gov.nist.javax.sip.message.MessageFactoryImpl;
import gov.nist.javax.sip.message.SIPRequest;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.nkk.common.utils.SipContextHolder;
import org.nkk.sip.beans.constants.SipConstant;
import org.nkk.sip.beans.model.device.Dto.RemoteInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

import javax.sip.ListeningPoint;
import javax.sip.SipFactory;
import javax.sip.SipProvider;
import javax.sip.address.Address;
import javax.sip.address.AddressFactory;
import javax.sip.address.SipURI;
import javax.sip.header.*;
import javax.sip.message.Message;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;
import javax.sip.message.Response;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

/**
 * SIP的工具类
 *
 * @author panlinlin
 * @version 1.0.0
 */
@UtilityClass
public class SipUtil {

    private final static Logger logger = LoggerFactory.getLogger(SipUtil.class);


    /*=================正在使用的=================*/
    private static final char[] DEFAULT_ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyz".toCharArray();

    public static UserAgentHeader userAgentHeader = SipUtil.createUserAgentHeader();
    @Setter
    public static String DEFAULT_CHARSET = SipConstant.CHARSET;

    /**
     * 从标签生成
     *
     * @return {@link String}
     */
    public static String generateFromTag() {
        return nanoId();
    }

    /**
     * 纳米id
     *
     * @return {@link String}
     */
    public static String nanoId() {
        return nanoId(32);
    }

    /**
     * 纳米id
     *
     * @param size 大小
     * @return {@link String}
     */
    public static String nanoId(int size) {
        return NanoId.randomNanoId(null, DEFAULT_ALPHABET, size);
    }

    /**
     * 通过标签生成
     *
     * @return {@link String}
     */
    public static String generateViaTag() {
        return "z9hG4bK" + nanoId(10);
    }

    /**
     * 获取sip工厂对象
     *
     * @return {@link SipFactory} 工厂对象
     */
    private static SipFactory getSipFactory() {
        return SipFactory.getInstance();
    }


    @SneakyThrows
    public static MessageFactory getMessageFactory() {
        MessageFactoryImpl messageFactory = (MessageFactoryImpl) getSipFactory().createMessageFactory();
        messageFactory.setDefaultContentEncodingCharset(DEFAULT_CHARSET);
        messageFactory.setDefaultUserAgentHeader(userAgentHeader);
        return messageFactory;
    }

    @SneakyThrows
    public static MessageFactory getMessageFactory(String charset) {
        MessageFactoryImpl messageFactory = (MessageFactoryImpl) getSipFactory().createMessageFactory();
        messageFactory.setDefaultContentEncodingCharset(charset);
        messageFactory.setDefaultUserAgentHeader(userAgentHeader);
        return messageFactory;
    }


    @SneakyThrows
    public static AddressFactory getAddressFactory() {
        return getSipFactory().createAddressFactory();
    }

    @SneakyThrows
    public static HeaderFactory getHeaderFactory() {
        return getSipFactory().createHeaderFactory();
    }

    @SneakyThrows
    public static SipURI createSipURI(String id, String address) {
        return getAddressFactory().createSipURI(id, address);
    }

    @SneakyThrows
    public static Address createAddress(SipURI uri) {
        return getAddressFactory().createAddress(uri);
    }

    @SneakyThrows
    public static ToHeader createToHeader(Address toAddress, String toTag) {
        return getHeaderFactory().createToHeader(toAddress, toTag);
    }

    @SneakyThrows
    public static ToHeader createToHeader(Address toAddress) {
        return createToHeader(toAddress, null);
    }

    @SneakyThrows
    public static FromHeader createFromHeader(Address fromAddress, String fromTag) {
        return getHeaderFactory().createFromHeader(fromAddress, fromTag);
    }

    @SneakyThrows
    public static CallIdHeader createCallIdHeader(String callId) {
        return getHeaderFactory().createCallIdHeader(callId);
    }

    @SneakyThrows
    public static CSeqHeader createCSeqHeader(long cSeq, String method) {
        return getHeaderFactory().createCSeqHeader(cSeq, method);
    }

    @SneakyThrows
    public static ViaHeader createViaHeader(String ip, int port, String transport, String viaTag) {
        ViaHeader viaHeader = getHeaderFactory().createViaHeader(ip, port, transport, viaTag);
        viaHeader.setRPort();
        return viaHeader;
    }

    @SneakyThrows
    public static List<ViaHeader> createViaHeaders(String ip, int port, String transport, String viaTag) {
        return Collections.singletonList(createViaHeader(ip, port, transport, viaTag));
    }

    @SneakyThrows
    public static MaxForwardsHeader createMaxForwardsHeader(int maxForwards) {
        return getHeaderFactory().createMaxForwardsHeader(maxForwards);
    }

    @SneakyThrows
    public static ContentTypeHeader createContentTypeHeader(String contentType, String subType) {
        return getHeaderFactory().createContentTypeHeader(contentType, subType);
    }

    public static String createHostAddress(String ip, int port) {
        return StringUtils.joinWith(":", ip, port);
    }


    /**
     * 进入页眉
     *
     * @param message 消息
     * @return {@link ToHeader}
     */
    @SneakyThrows
    public static ToHeader getToHeader(Message message) {
        return (ToHeader) message.getHeader(ToHeader.NAME);
    }

    /**
     * 从header获取
     *
     * @param message 消息
     * @return {@link FromHeader}
     */
    @SneakyThrows
    public static FromHeader getFromHeader(Message message) {
        return (FromHeader) message.getHeader(FromHeader.NAME);
    }

    /**
     * 从ToHeader获取用户id
     *
     * @param response 响应
     * @return {@link String}
     */
    public static String getUserIdFromToHeader(Response response) {
        ToHeader toHeader = (ToHeader) response.getHeader(ToHeader.NAME);
        return getUserIdFromHeader(toHeader);
    }

    /**
     * 从ToHeader获取用户id
     *
     * @param request 请求
     * @return {@link String}
     */
    public static String getUserIdFromToHeader(Request request) {
        ToHeader toHeader = (ToHeader) request.getHeader(ToHeader.NAME);
        return getUserIdFromHeader(toHeader);
    }

    /**
     * 从FromHeader获取用户id
     *
     * @param request 请求
     * @return {@link String}
     */
    public static String getUserIdFromFromHeader(Request request) {
        FromHeader fromHeader = (FromHeader) request.getHeader(FromHeader.NAME);
        return getUserIdFromHeader(fromHeader);
    }

    /**
     * 从FromHeader获取用户id
     *
     * @param response 响应
     * @return {@link String}
     */
    public static String getUserIdFromFromHeader(Response response) {
        FromHeader fromHeader = (FromHeader) response.getHeader(FromHeader.NAME);
        return getUserIdFromHeader(fromHeader);
    }

    public static String getUserIdFromHeader(HeaderAddress headerAddress) {
        AddressImpl address = (AddressImpl) headerAddress.getAddress();
        SipUri uri = (SipUri) address.getURI();
        return uri.getUser();
    }


    /**
     * 从header获取地址
     *
     * @param request 请求
     * @return {@link Address}
     */
    @SneakyThrows
    public static Address getAddressFromFromHeader(Request request) {
        FromHeader fromHeader = (FromHeader) request.getHeader(FromHeader.NAME);
        return fromHeader.getAddress();
    }

    /**
     * 生成sn
     *
     * @return {@link String}
     */
    public static String generateSn() {
        return String.valueOf((int) ((Math.random() * 9 + 1) * 100000));
    }


    /**
     * 获取sip提供商
     *
     * @param transport 传输方式
     * @return {@link SipProvider}
     */
    public static SipProvider getSipProvider(String transport) {
        return StringUtils.equalsIgnoreCase(transport, ListeningPoint.TCP) ?
                SipContextHolder.getBean("tcpSipProvider") : SipContextHolder.getBean("udpSipProvider");
    }


    /**
     * 获取新呼叫标识头
     *
     * @param transport 运输
     * @return {@link CallIdHeader}
     */
    public static CallIdHeader getNewCallIdHeader(String transport) {
        return getSipProvider(transport).getNewCallId();
    }

    /**
     * 获取新电话号码
     *
     * @param transport 运输
     * @return {@link String}
     */
    public static String getNewCallId(String transport) {
        return getNewCallIdHeader(transport).getCallId();
    }


    /**
     * 或得消息序列
     *
     * @return long
     */
    public static long getMessageSeq() {
        long timestamp = System.currentTimeMillis();
        return (timestamp & 0x3FFF) % Integer.MAX_VALUE;
    }


    /**
     * 创建用户代理头
     *
     * @return {@link UserAgentHeader}
     */
    @SneakyThrows
    public static UserAgentHeader createUserAgentHeader() {
        List<String> agentParam = new ArrayList<>();
        agentParam.add("Nkk ");
        agentParam.add("v0.0.1");
        return SipFactory.getInstance().createHeaderFactory().createUserAgentHeader(agentParam);
    }


    /**
     * 生成订阅Key
     *
     * @return {@link String}
     */
    public static String genSubscribeKey(String prefix, String... ids) {
        final String SEPARATOR = ":";
        return StringUtils.joinWith(SEPARATOR, (Object[]) ArrayUtils.addFirst(ids, prefix));
    }

    /*==================正在使用的 end =================*/


    /**
     * 从subject读取channelId
     */
    public static String getChannelIdFromRequest(Request request) {
        Header subject = request.getHeader("subject");
        if (subject == null) {
            // 如果缺失subject
            return null;
        }
        return ((Subject) subject).getSubject().split(":")[0];
    }


    /**
     * 从标签获取新
     *
     * @return {@link String}
     */
    public static String getNewFromTag() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 获取新标签
     *
     * @return {@link String}
     */
    public static String getNewTag() {
        return String.valueOf(System.currentTimeMillis());
    }


    /**
     * 云台指令码计算
     *
     * @param leftRight 镜头左移右移 0:停止 1:左移 2:右移
     * @param upDown    镜头上移下移 0:停止 1:上移 2:下移
     * @param inOut     镜头放大缩小 0:停止 1:缩小 2:放大
     * @param moveSpeed 镜头移动速度 默认 0XFF (0-255)
     * @param zoomSpeed 镜头缩放速度 默认 0X1 (0-255)
     */
    public static String cmdString(int leftRight, int upDown, int inOut, int moveSpeed, int zoomSpeed) {
        int cmdCode = 0;
        if (leftRight == 2) {
            cmdCode |= 0x01;        // 右移
        } else if (leftRight == 1) {
            cmdCode |= 0x02;        // 左移
        }
        if (upDown == 2) {
            cmdCode |= 0x04;        // 下移
        } else if (upDown == 1) {
            cmdCode |= 0x08;        // 上移
        }
        if (inOut == 2) {
            cmdCode |= 0x10;    // 放大
        } else if (inOut == 1) {
            cmdCode |= 0x20;    // 缩小
        }
        StringBuilder builder = new StringBuilder("A50F01");
        String strTmp;
        strTmp = String.format("%02X", cmdCode);
        builder.append(strTmp, 0, 2);
        strTmp = String.format("%02X", moveSpeed);
        builder.append(strTmp, 0, 2);
        builder.append(strTmp, 0, 2);

        //优化zoom低倍速下的变倍速率
        if ((zoomSpeed > 0) && (zoomSpeed < 16)) {
            zoomSpeed = 16;
        }
        strTmp = String.format("%X", zoomSpeed);
        builder.append(strTmp, 0, 1).append("0");
        //计算校验码
        int checkCode = (0XA5 + 0X0F + 0X01 + cmdCode + moveSpeed + moveSpeed + (zoomSpeed /*<< 4*/ & 0XF0)) % 0X100;
        strTmp = String.format("%02X", checkCode);
        builder.append(strTmp, 0, 2);
        return builder.toString();
    }

    /**
     * 从请求中获取设备ip地址和端口号
     *
     * @param request                       请求
     * @param sipUseSourceIpAsRemoteAddress false 从via中获取地址， true 直接获取远程地址
     * @return 地址信息
     */
    public static RemoteInfo getRemoteAddressFromRequest(SIPRequest request, boolean sipUseSourceIpAsRemoteAddress) {

        String remoteAddress;
        int remotePort;
        if (sipUseSourceIpAsRemoteAddress) {
            remoteAddress = request.getPeerPacketSourceAddress().getHostAddress();
            remotePort = request.getPeerPacketSourcePort();
        } else {
            // 判断RPort是否改变，改变则说明路由nat信息变化，修改设备信息
            // 获取到通信地址等信息
            remoteAddress = request.getTopmostViaHeader().getReceived();
            remotePort = request.getTopmostViaHeader().getRPort();
            // 解析本地地址替代
            if (ObjectUtils.isEmpty(remoteAddress) || remotePort == -1) {
                remoteAddress = request.getPeerPacketSourceAddress().getHostAddress();
                remotePort = request.getPeerPacketSourcePort();
            }
        }

        return new RemoteInfo(remoteAddress, remotePort);
    }


    /**
     * 解析时间
     *
     * @param timeStr 时间力量
     * @return {@link String}
     */
    public static String parseTime(String timeStr) {
        if (ObjectUtils.isEmpty(timeStr)) {
            return null;
        }
        LocalDateTime localDateTime;
        try {
            localDateTime = LocalDateTime.parse(timeStr);
        } catch (DateTimeParseException e) {
            try {
                localDateTime = LocalDateTime.parse(timeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).withZone(ZoneId.of("Asia/Shanghai")));
            } catch (DateTimeParseException e2) {
                logger.error("[格式化时间] 无法格式化时间： {}", timeStr);
                return null;
            }
        }
        return localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).withZone(ZoneId.of("Asia/Shanghai")));
    }


    /**
     * 是否是TCP
     *
     * @param request
     * @return 返回true/false
     */
    public boolean isTCP(Request request) {
        return isTCP((ViaHeader) request.getHeader(ViaHeader.NAME));
    }

    /**
     * 是否是TCP
     *
     * @param response
     * @return 返回true/false
     */
    public boolean isTCP(Response response) {
        return isTCP((ViaHeader) response.getHeader(ViaHeader.NAME));
    }

    /**
     * 是否是TCP
     *
     * @param viaHeader
     * @return 返回true/false
     */
    private boolean isTCP(ViaHeader viaHeader) {
        String protocol = viaHeader.getProtocol();
        return protocol.equals("TCP");
    }
}