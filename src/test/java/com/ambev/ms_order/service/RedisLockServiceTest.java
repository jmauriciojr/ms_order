package com.ambev.ms_order.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import com.ambev.ms_order.exception.OrderInProcessException;

@ExtendWith(MockitoExtension.class)
class RedisLockServiceTest {

	@Mock
	private RedisTemplate<String, Object> redisTemplate;

	@Mock
	private ValueOperations<String, Object> valueOperations;

	@InjectMocks
	private RedisLockService redisLockService;

	@BeforeEach
	void setUp() {
		lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
	}

	@Test
	void validateLock_withUnlockedKey_acquiresLock() {
		when(valueOperations.setIfAbsent(anyString(), anyString(), any(Duration.class))).thenReturn(true);

		redisLockService.validateLock("lock:order:123");

		verify(valueOperations).setIfAbsent("lock:order:123", "LOCKED", Duration.ofMillis(5000));
	}

	@Test
	void validateLock_withLockedKey_throwsOrderInProcessException() {
		when(valueOperations.setIfAbsent(anyString(), anyString(), any(Duration.class))).thenReturn(false);

		assertThrows(OrderInProcessException.class, () -> redisLockService.validateLock("lock:order:123"));
	}

	@Test
	void releaseLock_withValidKey_deletesKey() {
		redisLockService.releaseLock("lock:order:123");

		verify(redisTemplate).delete("lock:order:123");
	}

	@Test
	void createLockKey_withValidKey_returnsFormattedKey() {
		String result = redisLockService.createLockKey("123");

		assertEquals("lock:order:123", result);
		
		result = redisLockService.createLockKey("");

		assertEquals("lock:order:", result);
	}

}