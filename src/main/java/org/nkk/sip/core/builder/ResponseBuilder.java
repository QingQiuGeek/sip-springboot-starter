package org.nkk.sip.core.builder;

import lombok.Getter;
import lombok.SneakyThrows;

import javax.sip.header.ContentTypeHeader;
import javax.sip.header.ExpiresHeader;
import javax.sip.header.Header;
import javax.sip.message.Response;

import static org.nkk.sip.core.builder.SipBuilder.GB_VERSION;

public class ResponseBuilder {

    @Getter
    private final Response response;

    protected ResponseBuilder(Response response) {
        this.response = response;
        this.response.addHeader(GB_VERSION);
    }

    public ResponseBuilder addHeader(Header header){
        this.response.addHeader(header);
        return this;
    }

    @SneakyThrows
    public ResponseBuilder addReasonPhrase(String phrase){
        this.response.setReasonPhrase(phrase);
        return this;
    }

    @SneakyThrows
    public ResponseBuilder addStatusCode(int statusCode){
        this.response.setStatusCode(statusCode);
        return this;
    }

    @SneakyThrows
    public ResponseBuilder addExpires(ExpiresHeader expires){
        this.response.setExpires(expires);
        return this;
    }

    @SneakyThrows
    public ResponseBuilder addSIPVersion(String version){
        this.response.setSIPVersion(version);
        return this;
    }

    @SneakyThrows
    public ResponseBuilder addContent(Object content, ContentTypeHeader contentTypeHeader){
        this.response.setContent(content,  contentTypeHeader);
        return this;
    }

    public Response build(){
        return this.response;
    }

    /**
     * 执行发送响应数据
     */
    public Response execute() {
        SipBuilder.callMessage(this.response).exec();
        return this.response;
    }
}
