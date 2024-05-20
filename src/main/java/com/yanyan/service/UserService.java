package com.yanyan.service;

import com.yanyan.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yanyan.dto.LoginFormDTO;
import com.yanyan.dto.RegisterFormDTO;
import com.yanyan.dto.Result;
import jakarta.servlet.http.HttpSession;

/**
* @author 韶光善良君
* @description 针对表【yy_user】的数据库操作Service
* @createDate 2024-04-05 17:24:04
*/
public interface UserService extends IService<User> {
    Result sendCode(String email, HttpSession session);

    Result login(LoginFormDTO loginForm, HttpSession session);

    Result register(RegisterFormDTO registerFormDTO, HttpSession session);

    Result queryUserList(Integer current, Integer size);

    Result setUserPermission(Long userId, String role);

    Result deleteUser(Long userId);

    //修改用户头像
    Result updateUserImg(String img_url);
}
