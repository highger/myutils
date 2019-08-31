package com.example.demo.templatepattern;

import com.example.demo.TimerUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by mgy on 2019/8/31
 */
@Slf4j(topic = "job")
public abstract class CommonTemplateJob<T> implements LifeCycleJob<T> {
    List<T> list;
    TimerUtil timerUtil;

    @Override
    public void onStart() {
        timerUtil=TimerUtil.start();
        list = initData();
    }

    @Override
    public List<T> fetchData() {
        if (list.size() < 3) {
            return list;
        }
        return list.subList(0, 3);
    }

    @Override
    public void printData(List<T> data) {
        data.forEach(System.out::println);
        list.removeAll(data);
    }


    @Override
    public void onFinish() {
        log.info("任务执行完成！");
    }

    @Override
    public void onException(Exception e) {
        log.error("任务执行异常");
    }

    @Override
    public void onFinally() {
        log.info("任务时间统计，totalTime：{}", timerUtil.toString(TimeUnit.MILLISECONDS));
    }

    protected abstract List<T> initData();
}
