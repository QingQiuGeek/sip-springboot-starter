package org.nkk.media.hook.param;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * zlm hook事件中的on_send_rtp_stopped事件的参数
 * 
 * @author luna
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OnSendRtpStoppedHookParam extends HookParam {
    private String app;
    private String stream;

}
