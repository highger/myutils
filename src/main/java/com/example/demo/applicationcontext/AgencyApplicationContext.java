package com.example.demo.applicationcontext;

import lombok.RequiredArgsConstructor;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AgencyApplicationContext {

    private final ApplicationContext applicationContext;

    public <T> T get(ChannelCodeEnum channelCodeEnum, Class<T> clazz) {
        Optional<T> agencyBean = offer(channelCodeEnum, clazz);
        return agencyBean.orElseThrow(() -> {
            String err = String.format("没有找到该类型以及对应资方的beans! channelCodeEnum = %s", channelCodeEnum);
            return new NoSuchBeanDefinitionException(clazz, err);
        });
    }

    public <T> Optional<T> offer(ChannelCodeEnum channelCodeEnum, Class<T> clazz) {
        for (T beanMayProxied : applicationContext.getBeansOfType(clazz).values()) {
            Class<?> realBean = AopUtils.getTargetClass(beanMayProxied);
            AgencyQualifier agencyQualifier = realBean.getAnnotation(AgencyQualifier.class);
            if (agencyQualifier != null && Arrays.asList(agencyQualifier.value()).contains(channelCodeEnum)) {
                return Optional.of(beanMayProxied);
            }
        }
        return Optional.empty();
    }


}
