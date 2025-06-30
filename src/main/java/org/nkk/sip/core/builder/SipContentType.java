package org.nkk.sip.core.builder;

import org.nkk.sip.utils.SipUtil;

import javax.sip.header.ContentTypeHeader;

public class SipContentType {
    public static final ContentTypeHeader XML = SipUtil.createContentTypeHeader("APPLICATION", "MANSCDP+xml");
    public static final ContentTypeHeader SDP = SipUtil.createContentTypeHeader("APPLICATION", "SDP");

    public static final ContentTypeHeader RTSP = SipUtil.createContentTypeHeader("APPLICATION", "MANSRTSP");
}
