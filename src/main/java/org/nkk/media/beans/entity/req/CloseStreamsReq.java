package org.nkk.media.beans.entity.req;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

/**
 * @author luna
 * @version 1.0
 * @date 2023/12/2
 * @description:
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CloseStreamsReq extends MediaReq {

    private int force;

    public Map<String, Object> getMap() {
        Map<String, Object> map = toMap();
        map.put("force", String.valueOf(force));
        return map;
    }

}