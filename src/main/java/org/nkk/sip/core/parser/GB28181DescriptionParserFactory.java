package org.nkk.sip.core.parser;

import gov.nist.javax.sdp.parser.Lexer;
import gov.nist.javax.sdp.parser.ParserFactory;
import gov.nist.javax.sdp.parser.SDPParser;
import org.nkk.sip.core.sdp.ssrc.FormatField;
import org.nkk.sip.core.sdp.ssrc.SsrcField;
import org.nkk.sip.core.sdp.ssrc.parser.FormatFieldParser;
import org.nkk.sip.core.sdp.ssrc.parser.SsrcFieldParser;

import java.text.ParseException;

public class GB28181DescriptionParserFactory {
    public static SDPParser createParser(String field) throws ParseException {
        String fieldName = Lexer.getFieldName(field);
        if(fieldName.equalsIgnoreCase(SsrcField.SSRC_FIELD_NAME)){
            return new SsrcFieldParser(field);
        }
        if(fieldName.equalsIgnoreCase(FormatField.FORMAT_FIELD_NAME)){
            return new FormatFieldParser(field);
        }
        return ParserFactory.createParser(field);
    }
}
