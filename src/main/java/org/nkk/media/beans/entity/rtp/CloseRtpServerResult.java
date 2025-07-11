package org.nkk.media.beans.entity.rtp;

import lombok.Data;

/**
 * @author luna
 * @version 1.0
 * @date 2023/12/3
 * @description:
 */
@Data
public class CloseRtpServerResult {

    /**
     * 是否找到记录并关闭
     */
    private String hit;

    private String code;

}
