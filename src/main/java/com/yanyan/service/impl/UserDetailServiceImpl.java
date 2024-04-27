package com.yanyan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanyan.domain.School;
import com.yanyan.domain.UserDetail;
import com.yanyan.dto.Result;
import com.yanyan.dto.UserDetailDTO;
import com.yanyan.service.MajorService;
import com.yanyan.service.SchoolService;
import com.yanyan.service.UserDetailService;
import com.yanyan.mapper.UserDetailMapper;
import com.yanyan.utils.CacheClient;
import com.yanyan.utils.UserHolder;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

import static com.yanyan.utils.RedisConstants.CACHE_SCHOOL_KEY;
import static com.yanyan.utils.RedisConstants.CACHE_SCHOOL_TTL;

/**
* @author 韶光善良君
* @description 针对表【yy_user_detail(考研信息)】的数据库操作Service实现
* @createDate 2024-04-15 12:33:57
*/
@Service
public class UserDetailServiceImpl extends ServiceImpl<UserDetailMapper, UserDetail>
    implements UserDetailService{
    // TODO 根据用户id获取用户详细内容  设置用户的考研目标院校目标专业以及考研具体年数
    @Autowired
    private SchoolService schoolService;
    @Autowired
    private MajorService majorService;

    @Override
    public Result queryUserDetail() {
        Long userId = UserHolder.getUser().getId();
        UserDetail userDetail = query().eq("userId", userId).one();
        String schoolName = null;
        String majorName = null;
        if (userDetail.getSchoolid() != null) {
            schoolName = schoolService.querySchoolById(userDetail.getSchoolid()).getName();
        }
        if (userDetail.getMajorid() != null) {
            majorName = majorService.queryMajorById(userDetail.getMajorid()).getName();
        }

        UserDetailDTO userDetailDTO = new UserDetailDTO();
        userDetailDTO.setUserId(userId);
        userDetailDTO.setSchoolName(schoolName);
        userDetailDTO.setMajorName(majorName);
        userDetailDTO.setScore(userDetail.getScore());
        userDetailDTO.setSession(userDetail.getSession());

        return Result.ok(userDetailDTO);
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




