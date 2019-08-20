package com.example.demo;

import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 少用自动 copy 字段的工具, 一是 IDE 索引不到字段的 read and write, 二是可能会复制你意料之外的字段
 *  @Description: <p>
 * bean copy帮助类
 * </p>
 *
 * @see org.mapstruct.Mapper
 * 手动调用 setter 或者使用
 */
public class BeanCopierUtils {

    public static Map<String, BeanCopier> beanCopierMap = new HashMap<>();

    /**
     * bean 对象copy
     *
     * @param source
     * @param targetClass
     */
    public static <T> T copyProperties(Object source, Class<? extends T> targetClass) {

        String beanKey = generateKey(source.getClass(), targetClass);
        BeanCopier copier;
        if (!beanCopierMap.containsKey(beanKey)) {
            copier = BeanCopier.create(source.getClass(), targetClass, false);
            beanCopierMap.put(beanKey, copier);
        } else {
            copier = beanCopierMap.get(beanKey);
        }

        T targetObject;
        try {
            targetObject = targetClass.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        copier.copy(source, targetObject, null);

        return targetObject;

    }


    /**
     * bean 对象copy
     *
     * @param source
     * @param target
     */
    public static void copyProperties(Object source, Object target) {

        String beanKey = generateKey(source.getClass(), target.getClass());
        BeanCopier copier = null;
        if (!beanCopierMap.containsKey(beanKey)) {
            copier = BeanCopier.create(source.getClass(), target.getClass(), false);
            beanCopierMap.put(beanKey, copier);
        } else {
            copier = beanCopierMap.get(beanKey);
        }
        copier.copy(source, target, null);

    }

    /**
     * @param source
     * @param target
     * @param targetClass
     * @throws Exception
     */
    public static void copyListProperties(List<?> source, List target, Class<?> targetClass) throws Exception {

        if (CollectionUtils.isEmpty(source) || target == null) {
            return;
        }

        for (Object obj : source) {

            Object targetObject = targetClass.newInstance();
            copyProperties(obj, targetObject);

            target.add(targetObject);
        }
    }

    /**
     * @param source
     * @param targetClass
     * @throws Exception
     */
    public static List copyListProperties(List<?> source, Class<?> targetClass) {

        if (CollectionUtils.isEmpty(source)) {
            return null;
        }

        List target = new ArrayList();
        for (Object obj : source) {

            Object targetObject = null;
            try {
                targetObject = targetClass.newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            copyProperties(obj, targetObject);

            target.add(targetObject);
        }

        return target;
    }

    /**
     * 将对象非驼峰的属性copy给对应对象驼峰的属性
     *
     * @param source
     * @param targetClass
     * @return
     * @throws Exception
     */
    public static Object copyHumpFieldProperties(Object source, Class<?> targetClass)
        throws Exception {

        if (source == null) {
            return null;
        }

        Class<?> cla = source.getClass();
        Field[] fields = cla.getDeclaredFields();
        if (fields == null || fields.length <= 0) {
            return null;
        }

        Object targetObj = targetClass.newInstance();

        for (Field field : fields) {

            Object value = null;

            try {
                value = MethodUtils.invokeMethod(source, "get" + StringUtils.capitalize(field.getName()), null);
            } catch (Exception e) {
                try {
                    value = MethodUtils.invokeMethod(source, "is" + StringUtils.capitalize(field.getName()), null);
                } catch (Exception e1) {
                    throw new RuntimeException("方法" + "is" + StringUtils.capitalize(field.getName()) + "或"
                        + "get" + StringUtils.capitalize(field.getName()) + "不存在", e1);
                }
            }

            if (value == null) {
                continue;
            }

            try {
                MethodUtils.invokeMethod(targetObj, "set" + StringUtils.capitalize(getHumpFieldName(field.getName())), value);
            } catch (Exception e) {

            }

        }

        return targetObj;

    }

    /**
     * 获取驼峰属性
     *
     * @param fieldName
     * @return
     */
    private static String getHumpFieldName(String fieldName) {
        String[] column_nameArr = StringUtils.split(fieldName, "_");
        int length = column_nameArr.length;
        StringBuffer buffer = new StringBuffer(column_nameArr[0]);
        if (length > 1) {
            for (int i = 1; i < column_nameArr.length; i++) {
                buffer.append(StringUtils.capitalize(column_nameArr[i]));
            }
        }
        return buffer.toString();
    }

    private static String generateKey(Class<?> class1, Class<?> class2) {
        return class1.toString() + class2.toString();
    }


}
