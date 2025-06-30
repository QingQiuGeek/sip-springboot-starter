package org.nkk.sip.beans.annotations;

import org.nkk.sip.beans.enums.SipEnum;

import java.lang.annotation.*;

/**
 * sip事件
 *
 * @author nkk
 * @date 2024/01/10
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SipEvent {

    /**
     * 事件
     *
     * @return {@link SipEnum.Method}
     */
    SipEnum.Method value();

    /**
     * cmd
     */
    SipEnum.Cmd cmd() default SipEnum.Cmd.NONE;
}
