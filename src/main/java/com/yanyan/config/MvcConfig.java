package com.yanyan.config;


import com.yanyan.utils.LoginInterceptor;
import com.yanyan.utils.PermissionInterceptor;
import com.yanyan.utils.RefreshTokenInterceptor;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class MvcConfig implements WebMvcConfigurer {
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor())
                .excludePathPatterns(
//                        "/**"
                        "/user/login",
                        "/user/code",
                        "/user/register"
                ).order(1);
        registry.addInterceptor(new RefreshTokenInterceptor(stringRedisTemplate)).order(0);
        registry.addInterceptor(new PermissionInterceptor()).addPathPatterns(
                "/user/permission/set",
                "/user/delete",
                "/user/list",
                "/user/uv"
        ).order(2); // 添加权限拦截器
    }
}
