package org.nkk.sip.beans.model.base;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.sip.ListeningPoint;
import java.io.Serializable;
import org.nkk.sip.core.sdp.media.MediaStreamMode;

/**
 * 发送设备
 *
 * @author nkk
 * @date 2024/07/22
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ToDevice implements Serializable {


    private static final long serialVersionUID = 1222888044975209693L;
    /**
     * 设备id
     */
    private String deviceId;
    /**
     * 通道标识
     */
    private String channelId;

    /**
     * IP
     */
    private String ip;
    /**
     * 端口
     */
    private int port;

    /**
     * 传输方式,默认UDP，也可以是TCP
     */
    private String transport = MediaStreamMode.UDP.getMode();

    /**
     * 是否启用录像
     */
    private boolean enableRecord;
}
