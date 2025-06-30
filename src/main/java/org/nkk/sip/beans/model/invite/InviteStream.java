package org.nkk.sip.beans.model.invite;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.nkk.media.hook.custom.MediaPlayInfo;
import org.nkk.sip.beans.model.base.ToDevice;
import org.nkk.sip.beans.model.device.Dto.SipTransactionInfo;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InviteStream implements Serializable {

    private static final long serialVersionUID = -184318720648629956L;

    /**
     * 订阅的key
     */
    private String subscribeKey;
    /**
     * zlm IP
     */
    private String ip;
    /**
     * zlm分配端口
     */
    private int port;

    /**
     * 流方式
     */
    private String action;

    /**
     * 模式
     */
    private String schema;

    /**
     * ssrc
     */
    private String ssrc;

    /**
     * 流id
     */
    private String streamId;


    /**
     * 开启mp4录制
     */
    private boolean enableMp4Record;

    /**
     * Mp4最大秒
     */
    private int mp4MaxSecond;


    /**
     * 邀请请求数据
     */
    private SipTransactionInfo transactionInfo;

    /**
     * 播放信息
     */
    private MediaPlayInfo playInfo;

    /**
     * 设备
     */
    private ToDevice toDevice;


}
