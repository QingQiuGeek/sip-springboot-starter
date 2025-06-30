package org.nkk.sip.beans.model.device.Dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 设备远程信息
 *
 * @author nkk
 * @date 2024/06/28
 */
@Data
@NoArgsConstructor
public class RemoteInfo {
    private String ip;
    private int port;

    public RemoteInfo(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

}
