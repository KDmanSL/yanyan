package com.yanyan.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.UpdateChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanyan.domain.Major;
import com.yanyan.domain.School;
import com.yanyan.domain.UserDetail;
import com.yanyan.dto.Result;
import com.yanyan.dto.UserDetailDTO;
import com.yanyan.service.MajorService;
import com.yanyan.service.SchoolMajorService;
import com.yanyan.service.SchoolService;
import com.yanyan.service.UserDetailService;
import com.yanyan.mapper.UserDetailMapper;
import com.yanyan.utils.CacheClient;
import com.yanyan.utils.UserHolder;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
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

    @Autowired
    private SchoolService schoolService;
    @Autowired
    private MajorService majorService;
    @Resource
    private SchoolMajorService schoolMajorService;
    @Override
    public Result queryUserDetail() {
        Long userId = UserHolder.getUser().getId();
        String username = UserHolder.getUser().getUsername();
        String imgUrl = UserHolder.getUser().getImgUrl();

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
        userDetailDTO.setUserName(username);
        userDetailDTO.setImgUrl(imgUrl);
        userDetailDTO.setSchoolName(schoolName);
        userDetailDTO.setMajorName(majorName);
        userDetailDTO.setScore(userDetail.getScore());
        userDetailDTO.setSession(userDetail.getSession());

        return Result.ok(userDetailDTO);
    }

    @Override
    public Result setSchoolMajorSessionByUserId(String schoolName, String majorName, Integer session) {

        if(Objects.equals(schoolName, "") || Objects.equals(majorName, "") || session == null){
            return Result.fail("请输入完整信息");
        }
        List<Major> majorList = schoolMajorService.queryMajorNameBySchoolName(schoolName);
        if(majorList.isEmpty()){
            return Result.fail("学校不存在");
        }
        List<Major> majorList2 = majorList.stream().filter(major -> major.getName().equals(majorName)).toList();
        if(majorList2.isEmpty()){
            return Result.fail("该院校没有该专业");
        }

        Long userId = UserHolder.getUser().getId();
        Long schoolId = schoolService.querySchoolByName(schoolName).getId();
        Long majorId = majorService.queryMajorByName(majorName).getId();
        UserDetail userDetail = new UserDetail();
        userDetail.setUserid(userId);
        userDetail.setSchoolid(schoolId);
        userDetail.setMajorid(majorId);
        userDetail.setSession(session);
        update().eq("userId", userId).update(userDetail);
        return Result.ok("用户信息更改成功");
    }

    @Override
    public Result setScoreByUserId(Double score) {
        // TODO 设置用户分数
        return null;
    }
}




