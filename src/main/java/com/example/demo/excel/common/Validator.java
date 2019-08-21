package com.example.demo.excel.common;

import java.util.Optional;

/**
 * Created by mgy on 2019/8/21
 */
public abstract class Validator<T> {

    public abstract Optional<String> validate(T obj);

    private static final Validator NOP = new Validator() {
        @Override
        public Optional<String> validate(Object obj) {
            return Optional.empty();
        }
    };

    @SuppressWarnings("unchecked")
    static <T> Validator<T> nop() {
        return (Validator<T>) NOP;
    }
}
