package org.nkk.media.beans.entity.rtp;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.nkk.media.beans.entity.req.MediaReq;

import java.util.Map;

/**
 * @author luna
 * @version 1.0
 * @date 2023/12/3
 * @description:
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CloseSendRtpReq extends MediaReq {

    /**
     * 停止GB28181 ps-rtp推流
     */
    private String ssrc;

    public Map<String, Object> getMap() {
        Map<String, Object> map = toMap();
        map.put("ssrc", ssrc);
        return map;
    }
}
