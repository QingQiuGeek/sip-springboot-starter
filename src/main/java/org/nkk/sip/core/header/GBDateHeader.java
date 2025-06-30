package org.nkk.sip.core.header;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import gov.nist.javax.sip.header.SIPHeader;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.sip.header.Header;
import java.util.Calendar;

@EqualsAndHashCode(callSuper = true)
public class GBDateHeader extends SIPHeader implements Header{
    public static final String NAME = "Date";

    @Getter
    @Setter
    private Calendar date;

    public GBDateHeader() {
        super(NAME);
    }

    public GBDateHeader(Calendar date) {
        super(NAME);
        this.date = date;
    }

    @Override
    protected StringBuilder encodeBody(StringBuilder buffer) {
        return buffer.append(DateUtil.format(date.getTime(), DatePattern.UTC_SIMPLE_MS_FORMAT));
    }
}
