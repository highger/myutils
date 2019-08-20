package com.example.demo.idgenerator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;


@Component
public class TransferNoGeneratorImpl implements TransferNoGenerator {

    private final RedisTemplate<String, String> redisTemplate;

    @Autowired
    public TransferNoGeneratorImpl(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private static final int SUFFIX_LENGTH = 6;

    private static final int BOUND = (int) Math.pow(10, SUFFIX_LENGTH);

    private static final String TIMESTAMP_FORMAT = "yyMMddHHmmss";

    private static final DateTimeFormatter FORMATTER = DateTimeFormatters.get(TIMESTAMP_FORMAT);

    @Override
    public String next() {
        String transferNo;
        while (true) {
            transferNo = generateTransferNo();
            if (redisTemplate.opsForValue().setIfAbsent(transferNo, "")) {
                redisTemplate.expire(transferNo, 1, TimeUnit.SECONDS);
                return transferNo;
            }
        }
    }

    private String generateTransferNo() {
        String transferNo;
        LocalDateTime now = LocalDateTime.now();
        int i = ThreadLocalRandom.current().nextInt(BOUND);
        String suffix = String.format("%06d", i);
        transferNo = FORMATTER.format(now) + suffix;
        return transferNo;
    }

}
