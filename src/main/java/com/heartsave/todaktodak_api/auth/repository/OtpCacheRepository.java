package com.heartsave.todaktodak_api.auth.repository;

import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class OtpCacheRepository {
  private final RedisTemplate<String, String> redisTemplate;

  public OtpCacheRepository(
      @Qualifier("otpRedisTemplate") RedisTemplate<String, String> redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  public String get(String key) {
    return redisTemplate.opsForValue().get(key);
  }

  public void set(String key, String value, Duration duration) {
    redisTemplate.opsForValue().set("OTP:" + key, value, duration);
  }

  public void delete(String key) {
    redisTemplate.delete(key);
  }
}
