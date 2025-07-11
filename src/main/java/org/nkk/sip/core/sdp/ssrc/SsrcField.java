package org.nkk.sip.core.sdp.ssrc;

import gov.nist.core.Separators;
import gov.nist.javax.sdp.fields.SDPField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class SsrcField extends SDPField {
    public static final String SSRC_FIELD_NAME = "y";
    private static final String SSRC_FIELD = SSRC_FIELD_NAME + "=";
    public SsrcField() {
        super(SSRC_FIELD);
    }

    private String ssrc;

    @Override
    public String encode() {
        return SSRC_FIELD + ssrc + Separators.NEWLINE;
    }

    public String toString(){
        return encode();
    }
}
