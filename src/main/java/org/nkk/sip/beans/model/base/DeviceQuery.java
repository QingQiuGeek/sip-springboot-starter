package org.nkk.sip.beans.model.base;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.nkk.sip.beans.enums.SipEnum;

/**
 * 设备查询基础数据
 *
 * @author nkk
 * @date 2024/07/02
 */
@JacksonXmlRootElement(localName = "Query")
@JsonRootName("Query")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class DeviceQuery {

    @Builder.Default
    private String cmdType = SipEnum.Cmd.Catalog.name();

    @JacksonXmlProperty(localName = "SN")
    private String sn;

    /**
     * 目标设备的设备编码(必选)
     */
    @JacksonXmlProperty(localName = "DeviceID")
    private String deviceId;
}
