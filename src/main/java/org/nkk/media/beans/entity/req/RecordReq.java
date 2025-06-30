package org.nkk.media.beans.entity.req;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author luna
 * @version 1.0
 * @date 2023/12/3
 * @description:
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class RecordReq extends MediaReq {

    /**
     * 流的录像日期，格式为2020-02-01,如果不是完整的日期，那么是搜索录像文件夹列表，否则搜索对应日期下的mp4文件列表
     */
    private String period;

    /**
     * 自定义搜索路径，与startRecord方法中的customized_path一样，默认为配置文件的路径
     */
    private String customizedPath;

    /**
     * mp4录像切片时间大小,单位秒，置0则采用配置项
     */
    private int maxSecond;

    /**
     * 0为hls，1为mp4
     */
    private int type;

    /**
     * 要设置的录像倍速 eg.2.0
     */
    private String speed;

    /**
     * 要设置的录像播放位置
     */
    private String stamp;


}
