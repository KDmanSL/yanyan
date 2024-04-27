package com.yanyan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanyan.domain.UserDetail;
import com.yanyan.dto.Result;
import com.yanyan.service.UserDetailService;
import com.yanyan.mapper.UserDetailMapper;
import com.yanyan.utils.UserHolder;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

/**
* @author 韶光善良君
* @description 针对表【yy_user_detail(考研信息)】的数据库操作Service实现
* @createDate 2024-04-15 12:33:57
*/
@Service
public class UserDetailServiceImpl extends ServiceImpl<UserDetailMapper, UserDetail>
    implements UserDetailService{
    // TODO 根据用户id获取用户详细内容  设置用户的考研目标院校目标专业以及考研具体年数
    @Override
    public Result queryUserDetail() {
        Long userId = UserHolder.getUser().getId();
        UserDetail userDetail = query().eq("userId",userId).one();
        return Result.ok(userDetail);
    }

    @Override
    public Result setSchoolMajorSessionByUserId(String schoolName, String majorName, Integer grade) {
        return null;
    }

    @Override
    public Result setScoreByUserId(Double score) {
        return null;
    }
}




