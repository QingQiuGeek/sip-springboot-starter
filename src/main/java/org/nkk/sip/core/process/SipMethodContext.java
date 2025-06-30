package org.nkk.sip.core.process;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.nkk.sip.beans.enums.SipEnum;
import org.nkk.sip.core.process.method.AbstractSipRequestProcessor;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Sip上下文
 *
 * @author nkk
 * @date 2024/01/15
 */
@UtilityClass
@Slf4j
public class SipMethodContext
{

    /**
     * 事件
     *
     * @author nkk
     * @date 2024/01/15
     */
    public static class Method {
        private static final Map<SipEnum.Method, AbstractSipRequestProcessor> REGISTER_MAP = new ConcurrentHashMap<>();

        /**
         * 注册策略
         *
         * @param methodEnum    事件类型
         * @param requestProcessor 服务业务
         */
        public static void registerStrategy(SipEnum.Method methodEnum, AbstractSipRequestProcessor requestProcessor) {
            REGISTER_MAP.putIfAbsent(methodEnum, requestProcessor);
        }


        /**
         * 执行
         *
         * @param event 事件类型
         * @return {@link AbstractSipRequestProcessor}
         */
        public static AbstractSipRequestProcessor execute(String method){
            SipEnum.Method methodEnum = SipEnum.Method.resolve(method);
            AbstractSipRequestProcessor abSipRequestProcessor = REGISTER_MAP.get(methodEnum);
            if(Objects.isNull(abSipRequestProcessor)){
                log.error("{} 处理失败",method);
            }
            return abSipRequestProcessor;
        }
    }

}
