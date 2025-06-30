package org.nkk.sip.core.service;

import org.nkk.sip.beans.model.device.Dto.GbDevice;


/**
 * Sip回调处理类
 */
public interface SipEventProcess {

    /**
     * [设备上线]
     * <p>保存到数据库(或缓存)、更新设备在线状态</p>
     */
    void online(GbDevice device);

    /**
     * [设备下线]
     * <p>更新设备在线状态</p>
     */
    void offline(GbDevice device);

    /**
     * [心跳维护]
     * <p>维护设备状态</p>
     */
    void keepalive(GbDevice device);
}
