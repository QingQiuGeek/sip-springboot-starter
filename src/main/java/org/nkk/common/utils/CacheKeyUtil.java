package org.nkk.common.utils;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

@UtilityClass
public class CacheKeyUtil {
    /**
     * 分隔符
     */
    public final static String SEPARATOR = ":";


    /**
     * Sip序列前缀
     */
    public final static String SIP_C_SEQ_PREFIX = "SIP_C_SEQ";

    /**
     * 获取缓存Key
     *
     * @param prefix 前缀
     * @param ids    id
     * @return {@link String}
     */
    public static String getKey(String prefix,String... ids){
        return StringUtils.joinWith(SEPARATOR, (Object[]) ArrayUtils.addFirst(ids,prefix));
    }

}