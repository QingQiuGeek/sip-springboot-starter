package org.nkk.sip.beans.model.device.Resp;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.nkk.sip.beans.constants.SipConstant;
import org.nkk.sip.beans.model.base.DeviceBase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Data
@EqualsAndHashCode(callSuper = true)
@JacksonXmlRootElement(localName = "Response")
public class DeviceStatus extends DeviceBase {

    /**
     * 结果
     */
    private String Result;

    /**
     * 在线
     */
    private String Online;

    /**
     * 状态
     */
    private String Status;

    /**
     * 编码
     */
    private String Encode;

    /**
     * 记录
     */
    private String Record;

    /**
     * 设备时间
     */
    @JsonFormat(pattern = SipConstant.DATETIME_FORMAT, timezone = SipConstant.TIME_ZONE)
    private Date DeviceTime;

    /**
     * 报警状态
     */
    private AlarmStatus alarmstatus;

    @Data
    @JacksonXmlRootElement(localName = "Alarmstatus")
    public static class AlarmStatus {
        @JacksonXmlProperty(isAttribute = true)
        private Integer num = 0;

        @JacksonXmlProperty(localName = "Item")
        @JacksonXmlElementWrapper(useWrapping = false)
        private List<AlarmStatusItem> deviceList = new ArrayList<>();

        @Data
        @JacksonXmlRootElement(localName = "Item")
        public static class AlarmStatusItem {

            @JacksonXmlProperty(localName = "DeviceID")
            private String deviceId;

            private String dutyStatus;
        }
    }

}
