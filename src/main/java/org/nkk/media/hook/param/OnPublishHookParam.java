package org.nkk.media.hook.param;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * zlm hook事件中的on_publish事件的参数
 *
 * @author luna
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OnPublishHookParam extends HookParam {

    /**
     * TCP链接唯一ID
     */
    private String id;
    /**
     * 流应用名
     */
    private String app;
    /**
     * 流ID
     */
    private String stream;
    /**
     * 推流器ip
     */
    private String ip;
    /**
     * 推流url参数
     */
    private String params;
    /**
     * 推流器端口号
     */
    private int port;
    /**
     * 	推流的协议，可能是rtsp、rtmp
     */
    private String schema;
    /**
     * 流虚拟主机
     */
    private String vhost;



}
