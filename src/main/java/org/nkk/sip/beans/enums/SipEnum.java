package org.nkk.sip.beans.enums;

import cn.hutool.core.util.StrUtil;

import java.util.stream.Stream;

/**
 * sip事件枚举
 *
 * @author nkk
 * @date 2024/01/15
 */
public class SipEnum {

    /**
     * 事件 {@link javax.sip.message.Request} 定义了很多类型，目前只实现了以下几种方式
     *
     * @author nkk
     * @date 2024/01/15
     */
    public enum Method {

        // BYE
        BYE,

        // 邀请
        INVITE,

        // 注册
        REGISTER,

        // 消息
        MESSAGE,

        //通知
        NOTIFY;


        /**
         * 根据传入的方法匹配枚举值
         *
         * @param method 方法
         * @return {@link Method}
         */
        public static Method resolve(String method) {
            return Stream.of(Method.values()).filter(item -> StrUtil.equalsIgnoreCase(item.name(), method)).findFirst().orElse(null);
        }
    }


    /**
     * 命令类型
     */
    public enum Cmd {
        NONE,

        /**
         * 设备信息
         */
        DeviceInfo,

        /**
         * keepalive
         */
        Keepalive,

        /**
         * 报警
         */
        Alarm,

        /**
         * 媒体状态
         */
        MediaStatus,

        /**
         * 移动位置
         */
        MobilePosition,

        /**
         * 目录
         */
        Catalog,

        /**
         * 设备状态
         */
        DeviceStatus,

        /**
         * 记录信息
         */
        RecordInfo,

        /**
         * 设备控制
         */
        DeviceControl,

        /**
         * 控制
         */
        Control,

        /**
         * 广播
         */
        Broadcast,

        /**
         * 配置下载
         */
        ConfigDownload,

        /**
         * 设备配置
         */
        DeviceConfig,

        /**
         * 预设查询
         */
        PresetQuery,

        ;

        /**
         * 根据传入的方法匹配枚举值
         *
         * @param cmd cmd
         * @return {@link Cmd}
         */
        public static Cmd resolve(String cmd) {
            return Stream.of(Cmd.values()).filter(item -> StrUtil.equalsIgnoreCase(item.name(), cmd)).findFirst().orElse(null);
        }

    }
}
