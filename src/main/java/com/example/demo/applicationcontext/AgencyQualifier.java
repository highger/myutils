package com.example.demo.applicationcontext;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 指定 某个类是专门服务于某个特定资方的
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AgencyQualifier {
    ChannelCodeEnum[] value() default ChannelCodeEnum.NONE;
}
