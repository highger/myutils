package com.example.demo.validate;

import com.google.common.base.Strings;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by mgy on 2019/9/14
 */
public class ValidateUtils {
    private ValidateUtils() {
    }

    public static final String FAILED_TIPS = "参数验证失败!";

    private static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    public static <T> Set<ConstraintViolation<T>> validate(T object) {
        return validator.validate(object);
    }

    public static <T> List<T> filterInvalid(List<T> list) {
        List<T> validList = new ArrayList<>();
        for (T item : list) {
            Set<ConstraintViolation<T>> constraintViolations = validate(item);
            if (constraintViolations.isEmpty()) {
                validList.add(item);
            }
        }
        return validList;
    }

    public static <T> List<T> filterValid(List<T> list) {
        List<T> invalidList = new ArrayList<>();
        for (T item : list) {
            Set<ConstraintViolation<T>> constraintViolations = validate(item);
            if (!constraintViolations.isEmpty()) {
                invalidList.add(item);
            }
        }
        return invalidList;
    }

    /**
     * @return {@code pair.left} 是通过校验的有效对象 {@code pair.right} 是未通过校验的有效对象
     */
    public static <T> Pair<List<T>, List<T>> filter(List<T> list) {
        List<T> validList = new ArrayList<>();
        List<T> invalidList = new ArrayList<>();
        for (T item : list) {
            Set<ConstraintViolation<T>> constraintViolations = validate(item);
            if (constraintViolations.isEmpty()) {
                validList.add(item);
            } else {
                invalidList.add(item);
            }
        }
        return new ImmutablePair<>(validList, invalidList);
    }

    public static <T> String buildErrorMsg(Set<ConstraintViolation<T>> constraintViolationSet) {
        return buildErrorMsg("", constraintViolationSet);
    }

    public static <T> String buildErrorMsg(String prefix, Set<ConstraintViolation<T>> constraintViolationSet) {
        if (constraintViolationSet.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder(Strings.nullToEmpty(prefix)).append("->").append("[");
        for (ConstraintViolation<T> constraintViolation : constraintViolationSet) {
            sb.append(constraintViolation.getPropertyPath())
                .append(" ")
                .append(constraintViolation.getMessage())
                .append(",");
        }
        sb.deleteCharAt(sb.lastIndexOf(",")).append("]");
        return sb.toString();
    }

    public static <T> String buildErrorMsgWithTips(Set<ConstraintViolation<T>> constraintViolationSet) {
        return buildErrorMsg(FAILED_TIPS, constraintViolationSet);
    }

}
