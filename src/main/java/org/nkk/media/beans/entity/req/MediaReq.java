package org.nkk.media.beans.entity.req;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson2.JSON;
import lombok.Data;
import lombok.SneakyThrows;
import org.nkk.media.beans.constant.MediaEnum;

import java.util.Map;

/**
 * @author luna
 * @version 1.0
 * @date 2023/12/2
 * @description:
 */
@Data
public class MediaReq {

    /**
     * 筛选协议，例如 rtsp或rtmp
     */
    public String schema = "rtmp";
    /**
     * 筛选虚拟主机，例如__defaultVhost__
     */
    public String vhost = "__defaultVhost__";
    /**
     * 筛选应用名，例如 live
     */
    public String app;
    /**
     * 筛选流id，例如 test
     */
    public String stream;

    /**
     * 获取RTP实例
     *
     * @param stream 流
     * @param clazz  clazz
     * @return {@link T}
     */
    public static <T extends MediaReq> T getRtpInstance(String stream, Class<T> clazz) {
        return getInstance(MediaEnum.App.rtp.name(), stream, clazz);
    }

    /**
     * 获取代理实例
     *
     * @param stream 流
     * @param clazz  clazz
     * @return {@link T}
     */
    public static <T extends MediaReq> T getProxyInstance(String stream, Class<T> clazz) {
        return getInstance(MediaEnum.App.proxy.name(), stream, clazz);
    }

    /**
     * 获取实时实例
     *
     * @param stream 流
     * @param clazz  clazz
     * @return {@link T}
     */
    public static <T extends MediaReq> T getLiveInstance(String stream, Class<T> clazz) {
        return getInstance(MediaEnum.App.live.name(), stream, clazz);
    }
    /**
     * 获得实例
     *
     * @param app    应用程序
     * @param stream 流
     * @return {@link MediaReq}
     */
    public static <T extends MediaReq> T getInstance(MediaEnum.App app, String stream, Class<T> clazz) {
        return getInstance(app.name(), stream, clazz);
    }

    /**
     * 获得实例
     *
     * @param app    应用程序
     * @param stream 流
     * @return {@link MediaReq}
     */
    @SneakyThrows
    public static <T extends MediaReq> T getInstance(String app, String stream, Class<T> clazz) {
        T t = clazz.newInstance();
        t.setSchema("rtmp");
        t.setVhost("__defaultVhost__");
        t.setApp(app);
        t.setStream(stream);
        return t;
    }


    /**
     * 转换成map
     *
     * @return {@link Map}<{@link String}, {@link Object}>
     */
    public Map<String, Object> toMap() {
        return BeanUtil.beanToMap(this, true, true);
    }

}
