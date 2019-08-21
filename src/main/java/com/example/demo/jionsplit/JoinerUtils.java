package com.example.demo.jionsplit;

import com.google.common.base.Joiner;

/**
 * Created by mgy on 2019/8/21
 */
public class JoinerUtils {

    private JoinerUtils() {
    }

    public static final Joiner COMMA = Joiner.on(",").skipNulls();

    public static final Joiner LINE_BREAK = Joiner.on("\n").skipNulls();

}
