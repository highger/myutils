package com.example.demo.excel.common;

import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by mgy on 2019/8/21
 */

public class DateTimeFormat {

    private static Map<String, DateTimeFormatter> formatterMap = new ConcurrentHashMap<>();

    public static DateTimeFormatter get(String pattern) {
        return formatterMap.computeIfAbsent(pattern, DateTimeFormatter::ofPattern);
    }
}
