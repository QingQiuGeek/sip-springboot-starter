package org.nkk.media.beans.entity.rtp;

import lombok.Data;

/**
 * @author luna
 * @version 1.0
 * @date 2023/12/3
 * @description:
 */
@Data
public class OpenRtpServerResult {

    /**
     * 端口
     */
    private int port;

    /**
     * 状态码
     */
    private int code;

    private String msg;

}
