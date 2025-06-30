package org.nkk.sip.core.sdp.ssrc.parser;

import gov.nist.javax.sdp.fields.SDPField;
import gov.nist.javax.sdp.parser.Lexer;
import gov.nist.javax.sdp.parser.SDPParser;
import org.nkk.sip.core.sdp.ssrc.SsrcField;

import java.text.ParseException;

public class SsrcFieldParser extends SDPParser {
    public SsrcFieldParser(String ssrcField) {
        this.lexer = new Lexer("charLexer", ssrcField);
    }

    public SsrcField ssrcField() throws ParseException {
        try {
            this.lexer.match('y');
            this.lexer.SPorHT();
            this.lexer.match('=');
            this.lexer.SPorHT();

            SsrcField ssrcField = new SsrcField();
            String rest = lexer.getRest().trim();
            ssrcField.setSsrc(rest);
            return ssrcField;
        } catch (Exception e) {
            throw lexer.createParseException();
        }
    }

    public SDPField parse() throws ParseException {
        return this.ssrcField();
    }
}
