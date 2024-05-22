package com.yanyan.utils;

import com.yanyan.dto.UserDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

public class PermissionInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        UserDTO user = UserHolder.getUser();
//        // 当用户未登录的时候放行，因为在此之前存在登录拦截器，对一些不敏感页面不需要拦截
//        if (user == null) {
//            return true;
//        }

        String role = UserHolder.getUser().getRole();
        return !role.equals("stu");
    }
}
