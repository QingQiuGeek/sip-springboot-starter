package org.nkk.media.hook.custom;

import lombok.Data;

import java.io.Serializable;

/**
 * ZML播放url
 *
 * @author nkk
 * @date 2024/07/19
 */
@Data
public class MediaPlayInfo implements Serializable {

    private static final long serialVersionUID = 2686798873951798798L;

    /**
     * 流id
     */
    private String streamId;

    /**
     * aop
     */
    private String app;

    /**
     * 快照图片
     */
    private String snap;

    /**
     * 播放地址
     */
    private PlayUrl playUrl;

    /**
     * 代理拉流key
     */
    private String proxyKey;


    @Data
    public static class PlayUrl implements Serializable {

        private static final long serialVersionUID = 175568195301132254L;
        private String fmp4;

        private String hls;

        private String flv;
    }


}
