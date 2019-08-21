package com.example.demo.excel.common;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *   Created by mgy on 2019/8/21
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface ExcelColumn {

    /**
     * 表头
     */
    String value() default "";

    /**
     * 描述在表头后的括号里
     */
    String desc() default "";

    /**
     * 该列允许的值,该列若直接用枚举则无需使用此字段
     */
    String[] allowedSet() default {};

    String format() default "yyyy-MM-dd";
}
