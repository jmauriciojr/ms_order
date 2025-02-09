package com.ambev.ms_order.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.ambev.ms_order.exception.OrderInProcessException;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
public class RedisLockService {

	private static final String LOCK_ORDER = "lock:order:";
	private final RedisTemplate<String, Object> redisTemplate;

	public RedisLockService(RedisTemplate<String, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	/**
	 * Try to get a lock for the key you entered.
	 * 
	 * @param key     The unique key, for example "lock:order:{externalId}"
	 * @param timeout Lock expiration time, to avoid deadlocks
	 * @return true if the lock has been acquired, false otherwise.
	 */
	private boolean acquireLock(String key, long timeout, TimeUnit unit) {
		Boolean success = redisTemplate.opsForValue().setIfAbsent(key, "LOCKED",
				Duration.ofMillis(unit.toMillis(timeout)));
		return Boolean.TRUE.equals(success);
	}

	public void releaseLock(String key) {
		redisTemplate.delete(key);
	}
	
	
	public String createLockKey(String key) {
		return  LOCK_ORDER + key;
	}
	
	public void validateLock(String lockKey) {
		if (!acquireLock(lockKey, 5000, TimeUnit.MILLISECONDS)) {
            throw new OrderInProcessException("The request is already being processed by another instance.");
        }
	}
}