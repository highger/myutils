package com.example.demo.templatepattern;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mgy on 2019/8/31
 */
@Component
public class StringTemplate extends CommonTemplate<String> {

    @Override
    protected List<String> initData() {
        List<String> lists=new ArrayList<>();
        lists.add("1");
        lists.add("2");
        lists.add("3");
        lists.add("4");
        lists.add("5");
        return lists;
    }
}
