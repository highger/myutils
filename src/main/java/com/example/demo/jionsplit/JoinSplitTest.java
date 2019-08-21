package com.example.demo.jionsplit;

import java.util.List;

/**
 * Created by mgy on 2019/8/21
 */
public class JoinSplitTest {
    public static void main(String[] args) {

        String name="q,w,e,r";
        List<String> names = SplitterUtils.COMMA.splitToList(name);
        System.out.println(names);
        String join = JoinerUtils.COMMA.join(names);
        System.out.println(join);

    }
}
