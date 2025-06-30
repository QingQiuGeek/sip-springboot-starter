package org.nkk.sip.core.builder;

import cn.hutool.core.date.DateUtil;
import gov.nist.javax.sip.header.Authorization;
import gov.nist.javax.sip.header.SIPDateHeader;
import gov.nist.javax.sip.message.SIPRequest;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.nkk.sip.beans.model.base.ToDevice;
import org.nkk.sip.core.cmd.CallMessage;
import org.nkk.sip.core.date.GbSipDate;
import org.nkk.sip.core.header.GBDateHeader;
import org.nkk.sip.core.header.XGBVerHeader;
import org.nkk.sip.core.header.impl.XGBVerHeaderImpl;
import org.nkk.sip.utils.DigestAuthenticationHelper;
import org.nkk.sip.utils.SipUtil;

import javax.sip.header.ContentTypeHeader;
import javax.sip.header.ExpiresHeader;
import javax.sip.header.WWWAuthenticateHeader;
import javax.sip.message.Message;
import javax.sip.message.Request;
import javax.sip.message.Response;
import java.util.Calendar;
import java.util.Locale;

/**
 * 使用造者模式重新构建请求和响应对象
 * <p>所有的请求、响应对象都由{@link SipBuilder}作为创建入口</p>
 *
 */
@Slf4j
@UtilityClass
public class SipBuilder {

    @Setter
    public static XGBVerHeader GB_VERSION = XGBVerHeaderImpl.GB28181_2016;

    /**
     *
     * 构建请求消息
     *
     * @param toDevice  设备信息
     * @return {@link RequestBuilder}
     */
    @SneakyThrows
    public static RequestBuilder buildRequest(ToDevice toDevice) {
        return new RequestBuilder(toDevice);
    }


    /**
     * 构建sip消息传输对象并发送消息出去
     *
     * @param message 请求
     * @return {@link CallMessage}
     */
    @SneakyThrows
    public static CallMessage callMessage(Message message) {
        return new CallMessage(message);
    }


    /**
     * 建立响应
     *
     * @param statusCode 状态码
     * @param request    请求
     * @return {@link ResponseBuilder}
     */
    @SneakyThrows
    public static ResponseBuilder buildResponse(int statusCode, Request request) {
        Response response = SipUtil.getMessageFactory().createResponse(statusCode, request);
        return new ResponseBuilder(response);
    }

    /**
     * 构建响应消息
     *
     * @return {@link Response}
     */
    @SneakyThrows
    public static ResponseBuilder buildResponse(String response) {
        Response res = SipUtil.getMessageFactory().createResponse(response);
        return new ResponseBuilder(res);
    }

    /**
     * 构建响应消息
     *
     * @return {@link Response}
     */
    @SneakyThrows
    public static ResponseBuilder buildResponse(int statusCode, Request request,
                                                ContentTypeHeader contentType, Object content) {
        Response res = SipUtil.getMessageFactory().createResponse(statusCode, request, contentType, content);
        return new ResponseBuilder(res);
    }



    /**
     * 构建响应消息
     *
     * @return {@link Response}
     */
    @SneakyThrows
    public static ResponseBuilder buildResponse(int statusCode, Request request,
                                                ContentTypeHeader contentType, byte[] content) {
        Response res = SipUtil.getMessageFactory().createResponse(statusCode, request, contentType, content);
        return new ResponseBuilder(res);
    }

    /**
     * 构建成功的Response
     *
     * @param request 请求
     * @return {@link ResponseBuilder}
     */
    @SneakyThrows
    public static ResponseBuilder buildOKResponse(Request request) {
        SIPRequest req = (SIPRequest) request;
        if (req.getToHeader().getTag() == null) {
            req.getToHeader().setTag(SipUtil.getNewTag());
        }
        return buildResponse(Response.OK,req);
    }


    /**
     * 建立未经授权响应
     *
     * @param request  请求
     * @param domain   domain
     * @param password 验证密码
     * @return {@link ResponseBuilder}
     */
    @SneakyThrows
    public static ResponseBuilder buildUnauthorizedOfResponse(Request request, String domain, String password) {
        SIPRequest sipRequest = (SIPRequest) request;
        Authorization authorization = sipRequest.getAuthorization();
        if (authorization == null) {
            WWWAuthenticateHeader wwwAuthenticateHeader = DigestAuthenticationHelper.generateChallenge(domain);
            return buildResponse(Response.UNAUTHORIZED, request)
                    .addHeader(sipRequest.getContactHeader())
                    .addHeader(wwwAuthenticateHeader);
        }
        boolean passed = DigestAuthenticationHelper.doAuthenticatePlainTextPassword(request, password);
        if (!passed) {
            sipRequest.removeHeader(Authorization.NAME);
            return buildUnauthorizedOfResponse(request, domain, password);
        }

        return buildResponse(Response.OK, request)
                .addHeader(sipRequest.getContactHeader())
                .addHeader(sipRequest.getExpires())
                .addHeader(new GBDateHeader(DateUtil.calendar()));
    }

    /**
     * 建立注册OK响应
     *
     * @param request 状态码
     * @return {@link ResponseBuilder}
     */
    @SneakyThrows
    public static ResponseBuilder buildRegisterOfResponse(Request request) {
        SIPRequest sipRequest = (SIPRequest) request;
        ExpiresHeader expires = sipRequest.getExpires();
        if (expires == null) {
            return buildResponse(Response.BAD_REQUEST, request);
        }

        // 添加date头
        SIPDateHeader dateHeader = new SIPDateHeader();
        // GB28181 日期
        GbSipDate gbSipDate = new GbSipDate(Calendar.getInstance(Locale.ENGLISH).getTimeInMillis());
        dateHeader.setDate(gbSipDate);

        return buildResponse(Response.OK, request)
                .addHeader(dateHeader)
                .addHeader(sipRequest.getContactHeader())
                .addHeader(expires);
    }


}
