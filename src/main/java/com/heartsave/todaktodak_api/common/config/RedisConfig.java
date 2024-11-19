package com.heartsave.todaktodak_api.common.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@Configuration
@EnableRedisRepositories
@RequiredArgsConstructor
public class RedisConfig {
  @Value("${spring.data.redis.host}")
  private String REDIS_HOST;

  @Value("${spring.data.redis.port}")
  private Integer REDIS_PORT;

  @Bean
  public RedisConnectionFactory redisConnectionFactory() {
    return new LettuceConnectionFactory(REDIS_HOST, REDIS_PORT);
  }
}
