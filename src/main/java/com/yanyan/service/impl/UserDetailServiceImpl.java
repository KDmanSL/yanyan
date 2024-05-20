package com.yanyan.service.impl;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanyan.domain.Major;
import com.yanyan.domain.UserDetail;
import com.yanyan.dto.Result;
import com.yanyan.dto.UserDetailDTO;
import com.yanyan.service.*;
import com.yanyan.mapper.UserDetailMapper;
import com.yanyan.utils.UserHolder;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

/**
 * @author 韶光善良君
 * @description 针对表【yy_user_detail(考研信息)】的数据库操作Service实现
 * @createDate 2024-04-15 12:33:57
 */
@Service
public class UserDetailServiceImpl extends ServiceImpl<UserDetailMapper, UserDetail>
        implements UserDetailService {

    @Autowired
    private SchoolService schoolService;
    @Autowired
    private MajorService majorService;
    @Resource
    private SchoolMajorService schoolMajorService;
    @Resource
    private BaiduAIService baiduAIService;

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

        if (Objects.equals(schoolName, "") || Objects.equals(majorName, "") || session == null) {
            return Result.fail("请输入完整信息");
        }
        List<Major> majorList = schoolMajorService.queryMajorNameBySchoolName(schoolName);
        if (majorList.isEmpty()) {
            return Result.fail("学校不存在");
        }
        List<Major> majorList2 = majorList.stream().filter(major -> major.getName().equals(majorName)).toList();
        if (majorList2.isEmpty()) {
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
    public Result setScoreByUserId(String score, MultipartFile multipartFile) {
        // 先校验用户是否已经设置要考研的学校
        Long userId = UserHolder.getUser().getId();
        UserDetail userDetail = query().eq("userId", userId).one();
        if (userDetail.getSchoolid() == null) {
            return Result.fail("请先设置考研学校专业");
        }
        // 用户已经设置学校 获取学校名称 用于检测
        String schoolName = schoolService.querySchoolById(userDetail.getSchoolid()).getName();

        // 图像识别
        String ocrResult = baiduAIService.actionOcr(multipartFile);
        if (Objects.equals(ocrResult, "") || ocrResult == null) {
            return Result.fail("图片解析失败");
        }
        JSONObject jsonResult = JSONUtil.parseObj(ocrResult);
        JSONArray wordsResult = jsonResult.getJSONArray("words_result");

        boolean foundSchool = false;
        boolean foundScore = false;  // 用于标记是否找到了“总分”字样
        for (Object item : wordsResult) {

            JSONObject wordItem = (JSONObject) item;
            String words = wordItem.getStr("words");

            if (words.contains(score)) {
                foundScore = true;  // 标记已找到该分数
            }
            if (words.contains(schoolName)) {
                foundSchool = true;
            }
        }
        if (!foundSchool) {
            return Result.fail("图片无法核实学校，请咨询管理员");
        }
        if (!foundScore) {
            return Result.fail("图片无法核实成绩信息，请咨询管理员");
        }
        // 全部通过 将分数存储到用户的数据库中
        UserDetail userDetail2 = new UserDetail();
        userDetail2.setScore(Double.parseDouble(score));
        update().eq("userId", userId).update(userDetail2);

        return Result.ok("添加成绩成功");
    }

    @Override
    public Result getUserRank() {
        Long userId;
        try {
            userId= UserHolder.getUser().getId();
        }catch (Exception e){
            return Result.fail("请先登录");
        }
        UserDetail userDetail = query().eq("userId", userId).one();
        if(userDetail.getSchoolid()==null||userDetail.getMajorid()==null||userDetail.getSession()==null){
            return Result.fail("请先设置学校专业和届别");
        }
        if (userDetail.getScore() == null) {
            return Result.fail("请先设置分数");
        }
        Integer session = userDetail.getSession();
        Double score = userDetail.getScore();
        // 查询同一届用户的分数并按分数降序排列
        List<UserDetail> userList = query().eq("session", session).orderByDesc("score").list();

        // 计算排名
        int rank = 1;
        for (UserDetail user : userList) {
            if (user.getScore() > score) {
                rank++;
            } else {
                break;
            }
        }
        return Result.ok(rank);
    }
}



