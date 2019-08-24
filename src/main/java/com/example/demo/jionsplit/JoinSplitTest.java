package com.example.demo.jionsplit;

import java.util.List;

/**
 * Created by mgy on 2019/8/21
 */
public class JoinSplitTest {
    public static void main(String[] args) {

        String nameList="张三,李四,王二,麻子";
        List<String> names = SplitterUtils.COMMA.splitToList(nameList);
        System.out.println(names);
        String join = JoinerUtils.COMMA.join(names);
        System.out.println(join);

    }
}
