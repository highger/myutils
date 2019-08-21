package com.example.demo.excel.common;

import com.google.common.collect.Sets;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.*;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import java.util.Set;

/**
 * @author ccc  2018/11/16
 */
public class ReflectionUtil {
    private ReflectionUtil() {
    }

    public static String getFieldFullPath(Field field) {
        return field.getDeclaringClass().getCanonicalName() + "#" + field.getName();
    }

    public static Object getFieldValue(Object src, String name) {
        try {
            return PropertyUtils.getProperty(src, name);
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            String errMsg = String.format("无法获取域的值, fieldName=%s, class=%s", name, src.getClass());
            throw new UnknowReflectionException(errMsg, e);
        }
    }

    /**
     * 兼容旧的 setter
     */
    public static void setFieldValueMyBatis(Object src, String name, Object value) {
        try {
            String writeMethodName = "set" + StringUtils.capitalize(name);
            Optional<Method> writeMethod = Arrays.stream(src.getClass().getDeclaredMethods())
                    .filter(method -> method.getName().equals(writeMethodName))
                    .findAny();
            if (writeMethod.isPresent()) {
                writeMethod.get().setAccessible(true);
                writeMethod.get().invoke(src, value);
            } else {
                setProperty(src, name, value);
            }
        } catch (Exception e) {
            throw new UnknowReflectionException("class = " + src.getClass() + ", method = " + name + ", value = " + value, e);
        }
    }

    public static Object getFieldValue(Object src, Field field) {
        int modifiers = field.getModifiers();
        if (Modifier.isPublic(modifiers)) {
            return getPublicFieldValue(src, field);
        }
        field.setAccessible(true);
        try {
            return field.get(src);
        } catch (IllegalAccessException e) {
            String errMsg = String.format("无法获取域的值, field=%s, src=%s", field, src);
            throw new UnknowReflectionException(errMsg, e);
        }
    }

    private static Object getPublicFieldValue(Object src, Field field) {
        try {
            return field.get(src);
        } catch (IllegalAccessException e) {
            throw new UnknowReflectionException(e);
        }
    }

    public static void setProperty(Object target, String name, Object value) {
        try {
            PropertyUtils.setProperty(target, name, value);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            String errMsg = String.format("无法设置域的值, fieldName=%s, class=%s", name, target.getClass());
            throw new UnknowReflectionException(errMsg, e);
        }
    }

    public static Class getFirstSuperTypeArgument(Class<?> clazz) {
        return (Class<?>) ((ParameterizedType) clazz.getGenericSuperclass()).getActualTypeArguments()[0];
    }

    public static Class<?> getFirstTypeArgument(Type type) {
        if (!(type instanceof ParameterizedType)) {
            throw new IllegalArgumentException(type + "不是泛型类型!");
        }
        return (Class<?>) ((ParameterizedType) type).getActualTypeArguments()[0];
    }

    private static final Set<Class<?>> BASIC_TYPES = Sets.newHashSet(
            char.class,
            Character.class,
            Short.class,
            short.class,
            Integer.class,
            int.class,
            long.class,
            Long.class,
            float.class,
            Float.class,
            double.class,
            Double.class,
            byte.class,
            Byte.class,
            boolean.class,
            Boolean.class,
            BigDecimal.class,
            BigInteger.class,
            String.class,
            Date.class,
            LocalDate.class,
            LocalDateTime.class,
            ZonedDateTime.class,
            OffsetDateTime.class,
            OffsetTime.class,
            Instant.class
    );

    public static boolean isBasicType(Class<?> clazz) {
        return BASIC_TYPES.contains(clazz);
    }

    public static boolean isNotBasicType(Class<?> clazz) {
        return !isBasicType(clazz);
    }

    public static <T> T newInstance(Class<T> clazz) {
        try {
            Constructor<T> constructor = clazz.getConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (Exception e) {
            throw new UnknowReflectionException("无法实例化!", e);
        }
    }

    public static void setFieldValueDirectly(Object obj, Field field, Object value) {
        field.setAccessible(true);
        try {
            field.set(obj, value);
        } catch (IllegalAccessException e) {
            throw new UnknowReflectionException(e);
        }
    }

    public static void setFieldValue(Object obj, Field field, Object value) {
        try {
            setProperty(obj, field.getName(), value);
        } catch (Exception e) {
            setFieldValueDirectly(obj, field, value);
        }
    }

    public static boolean underPackage(Class<?> clazz, Package pck) {
        return underPackage(clazz, pck.getName());
    }

    public static boolean underPackage(Class<?> clazz, String packageStr) {
        return clazz.getPackage().getName().contains(packageStr);
    }
}
