package com.example.demo;

import com.google.common.collect.Maps;
import org.apache.http.annotation.ThreadSafe;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 只用来计时, 无法确定系统当前时间
 */
@ThreadSafe
public class TimerExt {

    private static final LinkedHashMap<TimeUnit, String> TIME_UNIT_AND_DISPLAY_NAME = Maps.newLinkedHashMap();

    static {
        TIME_UNIT_AND_DISPLAY_NAME.put(TimeUnit.DAYS, "天");
        TIME_UNIT_AND_DISPLAY_NAME.put(TimeUnit.HOURS, "小时");
        TIME_UNIT_AND_DISPLAY_NAME.put(TimeUnit.MINUTES, "分钟");
        TIME_UNIT_AND_DISPLAY_NAME.put(TimeUnit.SECONDS, "秒");
        TIME_UNIT_AND_DISPLAY_NAME.put(TimeUnit.MILLISECONDS, "微秒");
        TIME_UNIT_AND_DISPLAY_NAME.put(TimeUnit.MICROSECONDS, "毫秒");
        TIME_UNIT_AND_DISPLAY_NAME.put(TimeUnit.NANOSECONDS, "纳秒");
    }

    private final long start;

    private TimerExt() {
        start = System.nanoTime();
    }

    public static TimerExt start() {
        return new TimerExt();
    }

    private long elapsedNanoTime() {
        return System.nanoTime() - start;
    }

    public long elapsedTime(TimeUnit timeUnit) {
        return timeUnit.convert(elapsedNanoTime(), TimeUnit.NANOSECONDS);
    }

    private String formattedElapsedTime(TimeUnit minUnit) {
        long elapsedNanoTime = elapsedNanoTime();
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<TimeUnit, String> entry : TIME_UNIT_AND_DISPLAY_NAME.entrySet()) {
            TimeUnit timeUnit = entry.getKey();
            String displayName = entry.getValue();
            long convertedTime = timeUnit.convert(elapsedNanoTime, TimeUnit.NANOSECONDS);
            if (convertedTime != 0 || timeUnit == minUnit) {
                elapsedNanoTime -= TimeUnit.NANOSECONDS.convert(convertedTime, timeUnit);
                sb.append(convertedTime)
                        .append(displayName).append(" ");
            }
            if (timeUnit == minUnit) break;
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return formattedElapsedTime(TimeUnit.SECONDS);
    }

    public String toString(TimeUnit minUnit) {
        return formattedElapsedTime(minUnit);
    }
}
