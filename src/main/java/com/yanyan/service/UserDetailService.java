package com.yanyan.service;

import com.yanyan.domain.UserDetail;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yanyan.dto.Result;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.multipart.MultipartFile;

/**
* @author 韶光善良君
* @description 针对表【yy_user_detail(考研信息)】的数据库操作Service
* @createDate 2024-04-15 12:33:57
*/
public interface UserDetailService extends IService<UserDetail> {

    Result queryUserDetail();

    Result setSchoolMajorSessionByUserId(String schoolName, String majorName,Integer session);

    Result setScoreByUserId(String score, MultipartFile multipartFile);

}
