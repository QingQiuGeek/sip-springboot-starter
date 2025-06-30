package org.nkk.sip.beans.model.device.Req;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.nkk.sip.beans.constants.SipConstant;
import org.nkk.sip.beans.enums.SipEnum;

import java.util.Date;

@JacksonXmlRootElement(localName = "Query")
@JsonRootName("Query")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class DeviceRecordInfoQuery  {

    @JsonProperty(index = 0)
    @Builder.Default
    private String cmdType = SipEnum.Cmd.RecordInfo.name();

    @JsonProperty(index = 1)
    @JacksonXmlProperty(localName = "SN")
    private String sn;

    /**
     * 目标设备的设备编码(必选)
     */
    @JacksonXmlProperty(localName = "DeviceID")
    @JsonProperty(index = 2)
    private String deviceId;

    @JsonFormat(pattern = SipConstant.DATETIME_FORMAT, timezone = SipConstant.TIME_ZONE)
    @JsonProperty(index = 3)
    private Date startTime;

    @JsonFormat(pattern = SipConstant.DATETIME_FORMAT, timezone = SipConstant.TIME_ZONE)
    @JsonProperty(index = 4)
    private Date endTime;

    @Builder.Default
    @JsonProperty(index = 5)
    private Integer secrecy = 0;

    @Builder.Default
    @JsonProperty(index = 6)
    private String type = "all";

    @JacksonXmlProperty(localName = "RecorderID")
    @JsonProperty(index = 7)
    private String recorderId;

    @JsonProperty(index = 8)
    private String filePath;

    @JsonProperty(index = 9)
    private String address;
    /**
     * 录像模糊查询属性(可选) 缺省为 0; <p/>
     * 0: 不进行模糊查询,此时根据SIP消息中To头域
     * URI 中的ID值确定查询录像位置,若 ID 值为本域系统 ID 则进行中心历史记录检索
     * 若为前端设备 ID 则进行前端设备历史记录检索; <p/>
     *
     * 1: 进行模糊查询,此时设备所在域应同时进行中心检索和前端检索并将结果统一返回
     */
    @JsonProperty(index = 10)
    private Integer indistinctQuery;
}
