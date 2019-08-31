package com.example.demo.templatepattern;

/**
 * Created by mgy on 2019/8/31
 */
public interface BaseJob extends Job {

    @Override
    default void execute() {
        try {
            onStart();
            run();
            onFinish();
        } catch (Exception e) {
            onException(e);
        } finally {
            onFinally();
        }
    }

    /**
     * 任务开始时执行
     */
    void onStart();

    /**
     * 任务数据处理
     */
    void run();

    /**
     * 任务执行完成时执行
     */
    void onFinish();

    /**
     * 处理任务执行终发生的异常
     */
    default void onException(Exception e) {
        throw new JobExecuteException(e);
    }

    /**
     * 任务最终执行,即使出现异常任务中断也会执行
     */
    default void onFinally() {
    }
}

