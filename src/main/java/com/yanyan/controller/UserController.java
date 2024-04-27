package com.yanyan.controller;

import com.yanyan.dto.LoginFormDTO;
import com.yanyan.dto.RegisterFormDTO;
import com.yanyan.dto.Result;
import com.yanyan.service.UserDetailService;
import com.yanyan.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@CrossOrigin
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;
    @Resource
    private UserDetailService userDetailService;

    /**
     * 发送邮箱验证码
     * @param email 邮箱
     */
    @PostMapping("/code")
    public Result sendCode(@RequestParam("email") String email, HttpSession session) {
        // 发送短信验证码并保存验证码
        return userService.sendCode(email, session);
    }

    /**
     * 登录功能
     * @param loginForm 登录参数，包含邮箱、验证码；或者邮箱号、密码
     */
    @PostMapping("/login")
    public Result login(@RequestBody LoginFormDTO loginForm, HttpSession session){
        return userService.login(loginForm, session);
    }

    /**
     * 注册功能
     * @param registerFormDTO 注册参数，包含邮箱、验证码；或者邮箱号、密码
     */
    @PostMapping("/register")
    public Result register(@RequestBody RegisterFormDTO registerFormDTO, HttpSession session){
        return userService.register(registerFormDTO,session);
    }

    /**
     * 获取用户详情
     * @return 用户详情
     */
    @PostMapping("/detail")
    public Result getUserDetail(){
        return userDetailService.queryUserDetail();
    }

}
