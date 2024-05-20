package com.yanyan.config;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Hutool雪花算法配置类
 */
@Configuration
public class HutoolConfig {
    @Bean
    public Snowflake snowflake() {
        return IdUtil.getSnowflake(1,1);
    }
}
