package com.example.demo;

import com.google.common.base.Preconditions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RPermitExpirableSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 产品锁, 防止某些操作冲突, 例如打标,推送等操作同时进行
 *
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class FundsProductSemaphoreHelper {

    private final RedissonClient redissonClient;

    /**
     * 记录 permitId, 方便操作, 这样释放锁的时候不用传 permitId
     */
    private Map<Integer, String> permitIdMap = new ConcurrentHashMap<>();

    /**
     * 获取多个产品 id 的锁
     */
    public boolean tryAcquire(Collection<Integer> fundsProductIds) {
        boolean allLocked = true;
        List<Integer> lockedIds = new ArrayList<>();
        for (Integer fundsProductId : fundsProductIds) {
            if (tryAcquire(fundsProductId)) {
                lockedIds.add(fundsProductId);
            } else {
                allLocked = false;
                release(lockedIds);
                break;
            }
        }
        return allLocked;
    }

    /**
     * 同时释放多个产品的锁
     */
    public void release(Collection<Integer> fundsProductIds) {
        fundsProductIds.forEach(this::release);
    }

    public boolean tryAcquire(Integer fundsProductId) {
        Preconditions.checkArgument(fundsProductId != null, "无法锁定产品, 产品 id 不能为空!");
        RPermitExpirableSemaphore lock = redissonClient.getPermitExpirableSemaphore(fundsProductId + "");
        // 设置 permit 为 1, 也就是同时只能有一个人操作产品
        lock.trySetPermits(1);
        try {
            /*
             * 设置一定的等待时间, 太短可能会出现获取到 permit 时已经超时了, 导致一直无法获取成功,
             * leaseTime 防止特殊原因导致锁无法正确释放, 则在超过 leaseTime 之后自动释放锁
             * */
            String permitId = lock.tryAcquire(1, 10 * 60, TimeUnit.SECONDS);
            if (permitId == null) {
                return false;
            } else {
                permitIdMap.put(fundsProductId, permitId);
                return true;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("中断异常! fundsProductId = " + fundsProductId, e);
        }
    }

    public void release(Integer fundsProductId) {
        Preconditions.checkArgument(fundsProductId != null, "无法解锁产品, 产品 id 不能为空!");
        RPermitExpirableSemaphore semaphore = redissonClient.getPermitExpirableSemaphore(fundsProductId + "");
        if (semaphore != null) {
            String permitId = permitIdMap.remove(fundsProductId);
            if (permitId == null) {
                throw new IllegalArgumentException("无法解锁产品! fundsProductId = " + fundsProductId);
            }
            semaphore.release(permitId);
        }
    }
}
