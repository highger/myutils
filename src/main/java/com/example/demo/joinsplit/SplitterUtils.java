package com.example.demo.joinsplit;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;

/**
 * splitter,线程安全
 * Created by mgy on 2019/8/21
 */
public class SplitterUtils {

    private SplitterUtils() {
    }

    public static final Splitter COMMA = Splitter.on(",").omitEmptyStrings().trimResults();

    public static final Splitter DIGIT = Splitter.on(CharMatcher.DIGIT.negate())
        .omitEmptyStrings().trimResults(CharMatcher.DIGIT.negate());

    //匹配所有看不见的字符JAVA_DIGIT,如空格
    public static final Splitter INVISIBLE = Splitter.on(CharMatcher.INVISIBLE).omitEmptyStrings().trimResults();

}
