package org.nkk.media.beans.entity.rtp;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

/**
 * @author luna
 * @version 1.0
 * @date 2023/12/3
 * @description:
 */
@Data
public class StartSendRtpResult {

    private String code;

    @JSONField(name = "local_port")
    private String localPort;
}
