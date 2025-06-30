package org.nkk.sip.beans.constants;

import javax.sip.ListeningPoint;

/**
 * 国标协议常量
 */
public class SipConstant {

    /**
     * 时区
     */
    public static final String TIME_ZONE = "Asia/Shanghai";

    /**
     * 字符集
     */
    public static final String CHARSET = "GB2312";


    /**
     * 地图坐标系统
     */
    public static final String GEO_COORD_SYS = "WGS84";


    /**
     * datetime格式
     */
    public static final String DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";


    /**
     * 传输协议
     *
     * @author nkk
     * @date 2024/06/28
     */
    public static class TransPort {

        /**
         * UDP
         */
        public static final String UDP = ListeningPoint.UDP;


        /**
         * TCP
         */
        public static final String TCP = ListeningPoint.TCP;
    }
}
