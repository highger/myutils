package com.example.demo.idgenerator;

import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DateTimeFormatters {

    private static Map<String, DateTimeFormatter> formatterMap = new ConcurrentHashMap<>();

    public static DateTimeFormatter get(String pattern) {
        return formatterMap.computeIfAbsent(pattern, DateTimeFormatter::ofPattern);
    }
}
