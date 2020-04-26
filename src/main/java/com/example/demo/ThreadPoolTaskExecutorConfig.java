package com.example.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * 创建自定义线程池
 * Created by mgy on 2019/12/5
 */
@Configuration
@EnableAsync
@Slf4j
public class ThreadPoolTaskExecutorConfig {

    @Value("${executor.corePoolSize:5}")
    private Integer corePoolSize;

    @Value("${executor.maxPoolSize:10}")
    private Integer maxPoolSize;

    @Value("${executor.queueCapacity:10000}")
    private Integer queueCapacity;

    @Bean
    public ThreadPoolTaskExecutor taskExecutor() {
        log.info("init thread pool, corePoolSize = {}, maxPoolSize = {}, queueCapacity = {}",
                corePoolSize, maxPoolSize, queueCapacity);
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ExceptionHandlingExecutor();
        threadPoolTaskExecutor.setCorePoolSize(corePoolSize);
        threadPoolTaskExecutor.setMaxPoolSize(maxPoolSize);
        threadPoolTaskExecutor.setQueueCapacity(queueCapacity);
        threadPoolTaskExecutor.setThreadNamePrefix("my-utils-");
        return threadPoolTaskExecutor;
    }

    private static class ExceptionHandlingExecutor extends ThreadPoolTaskExecutor {

        private static final long serialVersionUID = 8738938260260004244L;

        @Override
        public void execute(Runnable task) {
            super.execute(createWrappedRunnable(task));
        }

        @Override
        public void execute(Runnable task, long startTimeout) {
            super.execute(createWrappedRunnable(task), startTimeout);
        }

        @Override
        public Future<?> submit(Runnable task) {
            return super.submit(createWrappedRunnable(task));
        }

        @Override
        public <T> Future<T> submit(final Callable<T> task) {
            return super.submit(createCallable(task));
        }

        private <T> Callable<T> createCallable(final Callable<T> task) {
            return () -> {
                try {
                    return task.call();
                } catch (Exception e) {
                    handle(e);
                    throw e;
                }
            };
        }

        private Runnable createWrappedRunnable(final Runnable task) {
            return () -> {
                try {
                    task.run();
                } catch (RuntimeException e) {
                    handle(e);
                    throw e;
                }
            };
        }

        private void handle(Exception e) {
            log.error("thread pool task exception occurred!", e);
        }
    }
}
