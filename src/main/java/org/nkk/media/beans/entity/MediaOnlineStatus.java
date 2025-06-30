package org.nkk.media.beans.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author luna
 * @version 1.0
 * @date 2023/12/2
 * @description:
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MediaOnlineStatus extends ServerResponse<String> {
    private String online;

}
