package org.nkk.sip.beans.model.device.Resp;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.nkk.sip.beans.constants.SipConstant;
import org.nkk.sip.beans.model.base.DeviceBase;

import java.util.Date;


@Data
@EqualsAndHashCode(callSuper = true)
@JacksonXmlRootElement(localName = "Response")
public class DeviceInfo extends DeviceBase {

    /**
     * 结果
     */
    private String Result;

    /**
     * 制造商
     */
    private String Manufacturer;

    /**
     * 模型
     */
    private String Model;

    /**
     * 固件
     */
    private String Firmware;

}
