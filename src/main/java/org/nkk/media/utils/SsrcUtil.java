package org.nkk.media.utils;

import java.util.concurrent.ConcurrentLinkedDeque;
import lombok.experimental.UtilityClass;
import org.nkk.common.utils.CacheKeyUtil;
import org.nkk.common.utils.SipContextHolder;
import org.nkk.sip.config.SipConfig;
import org.nkk.sip.core.session.cache.SipCacheManager;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class SsrcUtil {

    private static final String PREFIX = "SSRC";

    private static final int MAX_STREAM_COUNT = 3000;

    private static final SipCacheManager<String> fifoCache;


    private static String cacheKey = "";

    static {
        SipConfig bean = SipContextHolder.getBean(SipConfig.class);
        SipConfig.SipServerConf server = bean.getServer();
        cacheKey = CacheKeyUtil.getKey(PREFIX, server.getId(), bean.getMedia().getMediaId());
        fifoCache = SipContextHolder.getBean(SipCacheManager.class);
    }

    /**
     * 初始化 ssrc
     */
    public static void initSsrc() {
        Boolean existsData = fifoCache.existsKey(cacheKey);
        if (existsData) {
            return;
        }

        SipConfig bean = SipContextHolder.getBean(SipConfig.class);
        SipConfig.SipServerConf server = bean.getServer();
        String ssrcPrefix = server.getDomain().length() >= 8 ? server.getDomain().substring(3, 8) : server.getDomain();

        List<String> list = new ArrayList<>();
        for (int i = 1; i <= MAX_STREAM_COUNT; i++) {
            list.add(String.format("%s%04d", ssrcPrefix, i));
        }
        fifoCache.rightPushAll(cacheKey, list);
    }

    /**
     * 清空
     */
    private static void clear() {
        fifoCache.clear(cacheKey);
    }


    /**
     * 得到sn
     *
     * @return {@link String}
     */
    private static String getSN() {
        int size = fifoCache.size(cacheKey);
        if (size <= 0) {
            throw new RuntimeException("ssrc已经用完");
        }
        return fifoCache.leftPop(cacheKey);
    }

    /**
     * 获取视频预览的SSRC值,第一位固定为0
     *
     * @return ssrc
     */
    public String getPlaySsrc() {
        return "0" + getSN();
    }

    /**
     * 获取录像回放的SSRC值,第一位固定为1
     */
    public String getPlayBackSsrc() {
        return "1" + getSN();
    }

    /**
     * 释放ssrc，主要用完的ssrc一定要释放，否则会耗尽
     *
     * @param ssrc 需要重置的ssrc
     */
    public void releaseSsrc(String ssrc) {
        if (ssrc == null) {
            return;
        }
        String sn = ssrc.substring(1);
        fifoCache.leftPush(cacheKey, sn);
    }

}
