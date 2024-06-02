package com.yanyan.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {
    @Value("${Spring.data.redis.host}")
    private String host;
    @Value("${Spring.data.redis.password}")
    private String password;
    @Value("${Spring.data.redis.database}")
    private int database;
    @Bean
    public RedissonClient redissonClient() {
        // 配置
        Config config = new Config();
        config.useSingleServer().setAddress("redis://"+host+":6379").setPassword(password).setDatabase(database);

        return Redisson.create(config);
    }
}
