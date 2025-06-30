package org.nkk.sip.beans.model.device.Resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.*;
import org.nkk.sip.beans.constants.SipConstant;
import org.nkk.sip.beans.model.base.DeviceBase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@JacksonXmlRootElement(localName = "Response")
public class DeviceRecordList extends DeviceBase {

    private String name;

    private Integer sumNum;

    private RecordList recordList;


    @JacksonXmlRootElement(localName = "RecordList")
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class RecordList {
        @Builder.Default
        @JacksonXmlProperty(isAttribute = true)
        private Integer num = 0;

        @Builder.Default
        @JacksonXmlProperty(localName = "Item")
        @JacksonXmlElementWrapper(useWrapping = false)
        private List<RecordItem> recordList = new ArrayList<>();


        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        @Data
        @JacksonXmlRootElement(localName = "Item")
        public static class RecordItem {
            /**
             * 目标设备的设备编码(必选)
             */
            @JacksonXmlProperty(localName = "DeviceID")
            private String deviceId;

            private String name;

            private String address;

            @JsonFormat(pattern = SipConstant.DATETIME_FORMAT, timezone = SipConstant.TIME_ZONE)
            private Date startTime;

            @JsonFormat(pattern = SipConstant.DATETIME_FORMAT, timezone = SipConstant.TIME_ZONE)
            private Date endTime;

            @Builder.Default
            private Integer secrecy = 0;

            @Builder.Default
            private String type = "all";

            private Long fileSize;

            private String filePath;
        }
    }

}
