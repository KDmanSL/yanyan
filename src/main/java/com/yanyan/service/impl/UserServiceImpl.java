package com.yanyan.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanyan.domain.School;
import com.yanyan.domain.User;
import com.yanyan.dto.LoginFormDTO;
import com.yanyan.dto.RegisterFormDTO;
import com.yanyan.dto.Result;
import com.yanyan.dto.UserDTO;
import com.yanyan.service.UserService;
import com.yanyan.mapper.UserMapper;
import com.yanyan.utils.RSASecurityUtils;
import com.yanyan.utils.RegexUtils;
import com.yanyan.utils.UserHolder;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.yanyan.utils.RedisConstants.*;

/**
 * @author 韶光善良君
 * @description 针对表【yy_user】的数据库操作Service实现
 * @createDate 2024-04-05 17:24:04
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    RSASecurityUtils rsaSecurityUtils;

    @Override
    public Result sendCode(String email, HttpSession session) {
        // 1.校验邮箱
        if (RegexUtils.isEmailInvalid(email)) {
            // 2.邮箱格式错误
            return Result.fail("邮箱格式错误");
        }
        // 3.符合，生成验证码
        String code = RandomUtil.randomNumbers(6);
        // 4.保存验证码到redis
        stringRedisTemplate.opsForValue().set(LOGIN_CODE_KEY + email, code, LOGIN_CODE_TTL, TimeUnit.MINUTES);
        //5.发送验证码（需要调用第三方插件，这里使用日志替代）
        log.debug("验证码：{}", code);
        return Result.ok(code);
    }

    @Override
    public Result login(LoginFormDTO loginForm, HttpSession session) {
        // 1.校验邮箱
        String email = loginForm.getEmail();
        if (RegexUtils.isEmailInvalid(email)) {
            // 2.邮箱格式错误
            return Result.fail("邮箱格式错误");
        }
        // 3.校验验证码
        String code = loginForm.getCode();
        String cacheCode = stringRedisTemplate.opsForValue().get(LOGIN_CODE_KEY + email);
        if (cacheCode == null || !cacheCode.equals(code)) {
            // 2.邮箱格式错误
            return Result.fail("验证码错误");
        }
        // 4.符合，根据邮箱号查找
        User user = query().eq("email", email).one();
        // 5.判断用户是否存在
        if (user == null) {
            return Result.fail("用户不存在");
        }
        // 5.1密码校验
        // 5.1.1密码解密
        String password = rsaSecurityUtils.decrypt(loginForm.getPassword());
        String password2 = rsaSecurityUtils.decrypt(user.getPassword());
        if (!password2.equals(password)) {
            // 5.2密码错误
            return Result.fail("密码错误");
        }
        // 6.保存user到redis
        // 6.1 生成token
        String token = UUID.randomUUID().toString(true);
        // 6.2 将user转为map存储
        UserDTO userDTO = BeanUtil.copyProperties(user, UserDTO.class);
        Map<String, Object> userMap = BeanUtil.beanToMap(userDTO, new HashMap<>(),
                CopyOptions.create()
                        .setIgnoreNullValue(true)
                        .setFieldValueEditor((fieldName, fieldValue) -> fieldValue.toString()));

        // 6.3 存储
        stringRedisTemplate.opsForHash().putAll(LOGIN_USER_KEY + token, userMap);
        // 6.4 设置token有效期
        stringRedisTemplate.expire(LOGIN_USER_KEY + token, LOGIN_USER_TTL, TimeUnit.MINUTES);

        // 获取当前日期精确到天
        String today = DateUtil.today();
        // 6.5 存储到hyperLog
        stringRedisTemplate.opsForHyperLogLog().add(USER_UV_KEY + today, email);

        // 7.返回token
        return Result.ok(token);
    }

    public Result register(RegisterFormDTO registerForm, HttpSession session) {
        // 1.校验邮箱
        String email = registerForm.getEmail();
        if (RegexUtils.isEmailInvalid(email)) {
            // 2.邮箱格式错误
            return Result.fail("邮箱格式错误");
        }
        // 3.校验验证码
        String code = registerForm.getCode();
        String cacheCode = stringRedisTemplate.opsForValue().get(LOGIN_CODE_KEY + email);
        if (cacheCode == null || !cacheCode.equals(code)) {
            return Result.fail("验证码错误");
        }
        // 4.符合，根据邮箱查询用户
        User user = query().eq("email", email).one();
        // 5.判断用户是否存在
        if (user != null) {
            return Result.fail("用户已存在");
        }
        // 5.1判断密码格式
        String password = registerForm.getPassword();
        String password2 = rsaSecurityUtils.decrypt(registerForm.getPassword());
        if (RegexUtils.isPasswordInvalid(password2)) {
            return Result.fail("密码格式错误");
        }
        // 6.保存用户到数据库
        user = new User();
        user.setEmail(email);
        user.setUsername(registerForm.getUsername());
        user.setPassword(password);
        user.setRole("stu");
        user.setImgUrl("https://img2.imgtp.com/2024/04/15/f1XZ8GcI.png");
        save(user);

        // 6.保存user到redis
        // 6.1 生成token
        String token = UUID.randomUUID().toString(true);
        // 6.2 将user转为map存储
        UserDTO userDTO = BeanUtil.copyProperties(user, UserDTO.class);
        Map<String, Object> userMap = BeanUtil.beanToMap(userDTO, new HashMap<>(),
                CopyOptions.create()
                        .setIgnoreNullValue(true)
                        .setFieldValueEditor((fieldName, fieldValue) -> fieldValue.toString()));

        // 6.3 存储
        stringRedisTemplate.opsForHash().putAll(LOGIN_USER_KEY + token, userMap);
        // 6.4 设置token有效期
        stringRedisTemplate.expire(LOGIN_USER_KEY + token, LOGIN_USER_TTL, TimeUnit.MINUTES);

        // 获取当前日期精确到天
        String today = DateUtil.today();
        // 6.5 存储到hyperLog 存储uv
        stringRedisTemplate.opsForHyperLogLog().add(USER_UV_KEY + today, email);

        // 7.返回token
        return Result.ok(token);
    }

    @Override
    public Result queryUserList(Integer current, Integer size) {
        // 校验权限
        String userRole = UserHolder.getUser().getRole();
        if (!Objects.equals(userRole, "adm")) {
            return Result.fail("权限不足");
        }

        List<User> userList = list();
        Long totalPage = (long) Math.ceil((double) userList.size() / size);
        int start = (current - 1) * size;
        int end = current * size - 1;
        start = Math.max(start, 0);
        end = Math.min(end, userList.size() - 1);
        if (start >= userList.size()) {
            return Result.fail("超出页面请求范围");
        }
        // 返回当前页数据
        List<User> nowPageList = userList.subList(start, end + 1);


        return Result.ok(nowPageList, totalPage);
    }

    @Override
    public Result setUserPermission(Long userId, String role) {
        // 校验权限
        String userRole = UserHolder.getUser().getRole();
        if (!Objects.equals(userRole, "adm")) {
            return Result.fail("权限不足");
        }
        // 校验目标权限合法性
        Set<String> validRoles = Set.of("adm", "stu", "tec");
        if (!validRoles.contains(role)) {
            return Result.fail("权限不存在");
        }
        // 修改目标权限
        User user = new User();
        user.setRole(role);
        update().eq("id", userId).update(user);
        return Result.ok("用户权限修改成功！");
    }

    @Override
    public Result deleteUser(Long userId) {
        // 校验权限
        String userRole = UserHolder.getUser().getRole();
        if (!Objects.equals(userRole, "adm")) {
            return Result.fail("权限不足");
        }

        removeById(userId);

        return Result.ok("删除用户成功");
    }

}




