package org.nkk.media.beans.entity;

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
public class LoadMp4FileReq extends MediaReq {

    /**
     * mp4文件绝对路径
     */
    private String filePath;

    public Map<String, Object> getMap() {
        Map<String, Object> map = toMap();
        map.put("file_path", filePath);
        return map;
    }
}
