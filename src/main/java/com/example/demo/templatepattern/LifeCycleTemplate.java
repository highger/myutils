package com.example.demo.templatepattern;

import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.concurrent.atomic.LongAdder;

/**
 * Created by mgy on 2019/8/31
 */

public interface LifeCycleTemplate<T> extends BaseTemplate {

    @Override
    default void run() {
        LongAdder longAdder = new LongAdder();
        while (true) {
            List<T> dataList = fetchData();
            if (CollectionUtils.isEmpty(dataList)) {
                break;
            }
            printData(dataList);
            longAdder.increment();
            System.out.println("第" + longAdder.sum() + "次循环结束！");
        }
    }

    /**
     * 获取任务需要的数据
     */
    List<T> fetchData();

    /**
     * 处理任务获取的数据
     */
    void printData(List<T> data);
}
