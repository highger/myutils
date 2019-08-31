package com.example.demo.templatepattern;

/**
 * 这个模版方法demo，主要用于输出数据，已对异常做了处理，并对任务执行时间做了统计。
 * 其实这里在项目中，还可以基于注解结合策略模式，实现模版的选择，可更加简化我们项目中的代码。
 * 新增需求只需要实现定制化修改即可，增强了代码的可维护性，加速我们系统开发的进度。
 * Created by mgy on 2019/8/31
 */
public class JobTest {
    public static void main(String[] args) {

        StringJob job = new StringJob();
        job.execute();
    }
}
