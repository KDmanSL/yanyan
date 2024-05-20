package com.yanyan.controller;

import com.yanyan.domain.UserFavorites;
import com.yanyan.dto.LoginFormDTO;
import com.yanyan.dto.RegisterFormDTO;
import com.yanyan.dto.Result;
import com.yanyan.service.BaiduAIService;
import com.yanyan.service.UserDetailService;
import com.yanyan.service.UserFavoritesService;
import com.yanyan.service.UserService;
import com.yanyan.utils.PicBedConstants;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@CrossOrigin
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;
    @Resource
    private UserDetailService userDetailService;

    @Resource
    private UserFavoritesService userFavoritesService;


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

    /**
     * 设置用户详情
     * @param schoolName,majorName,session 院校名称 专业名称 届数
     * @return 结果信息
     */
    @PostMapping("/detail/set")
    public Result setUserDetail(@RequestParam("schoolName") String schoolName,
                                @RequestParam("majorName") String majorName,
                                @RequestParam(value = "session" ,required = false) Integer session){
        return userDetailService.setSchoolMajorSessionByUserId(schoolName,majorName,session);
    }

    /**
     * 获取用户收藏的课程
     * @return 用户收藏的课程
     */
    @GetMapping("/favorites")
    public Result getUserFavorites(@RequestParam(value = "current", defaultValue = "1") Integer current,
                                   @RequestParam(value = "size", defaultValue = "10") Integer size){
        return userFavoritesService.queryUserFavoritesByUserId(current, size);
    }

    /**
     * 设置用户分数（上传图片认证）
     * @param multipartFile 图片文件
     * @param score 分数
     * @return 图片识别结果
     */
    @PostMapping("/score")
    public Result setUserScore(
            @RequestParam("score") String score,
            @RequestParam("file") MultipartFile multipartFile){
        return userDetailService.setScoreByUserId(score, multipartFile);
    }

/*
  ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
  管理员相关
 */
    /**
     * 获取用户列表
     * @param current 当前页
     * @param size 每页大小
     * @return 用户列表
     */
    @GetMapping("/list")
    public Result getUserList(@RequestParam(value = "current", defaultValue = "1") Integer current,
                              @RequestParam(value = "size", defaultValue = "10") Integer size){
        return userService.queryUserList(current, size);
    }

    /**
     * 修改用户权限
     *
     * @param userId 用户id
     * @param role 权限
     * @return 结果信息
     */
    @PostMapping("/permission/set")
    public Result setUserPermission(@RequestParam("userId") Long userId,
                                    @RequestParam("role") String role){
        return userService.setUserPermission(userId,role);
    }

    /**
     * 删除指定用户
     *
     * @param userId 用户id
     * @return 结果信息
     */
    @PostMapping("/delete")
    public Result deleteUser(@RequestParam("userId") Long userId){
        return userService.deleteUser(userId);
    }

    /**
     * 修改用户头像
     *
     * @param imgUrl 图片文件
     * @return 结果信息
     */
    @PostMapping("/img/set")
    public Result setUserImg(@RequestParam("imgUrl") String imgUrl){
        return userService.updateUserImg(imgUrl);
    }

    /**
     * 获取可用的头像列表
     */
    @GetMapping("/img/list")
    public Result getUserImgList(){
        List<String> picList=PicBedConstants.HEAD_PIC_LIST;
        return Result.ok(picList);
    }

    /**
     * 获取用户排名
     * @return 用户排名
     */
    @GetMapping("/rank")
    public Result getUserRank(){
        return userDetailService.getUserRank();
    }
}
