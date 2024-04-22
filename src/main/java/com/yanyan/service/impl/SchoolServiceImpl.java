package com.yanyan.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanyan.domain.School;
import com.yanyan.dto.Result;
import com.yanyan.service.SchoolService;
import com.yanyan.mapper.SchoolMapper;
import com.yanyan.utils.CacheClient;
import com.yanyan.utils.RedisConstants;
import com.yanyan.utils.RedisData;
import com.yanyan.utils.SystemConstants;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.yanyan.utils.RedisConstants.*;

/**
 * @author 韶光善良君
 * @description 针对表【yy_school(院校信息)】的数据库操作Service实现
 * @createDate 2024-04-15 12:33:47
 */
@Service
public class SchoolServiceImpl extends ServiceImpl<SchoolMapper, School>
        implements SchoolService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private CacheClient cacheClient;

    @Override
    public Result queryAllCoursesList(Integer current) {
        //1.从redis查询商铺列表缓存
        List<String> listCache = stringRedisTemplate.opsForList().range(SCHOOL_ALL_LIST_KEY + ":" + current, 0, -1);
        //2.判断是否为空
        if (listCache != null && !listCache.isEmpty()) {
            //不为空，返回列表
            List<School> schoolsList = listCache.stream()
                    .map(str -> (School) JSONUtil.toBean(str, School.class, true))
                    .sorted(Comparator.comparingLong(School::getRanking))
                    .collect(Collectors.toList());
            return Result.ok(schoolsList);
        }
        // 分页
        Page<School> schoolsList = query().orderByAsc("ranking").page(new Page<>(current,SystemConstants.DEFAULT_PAGE_SIZE));
        if (schoolsList == null) {
            return Result.fail("查询类型列表失败");
        }
        List<String> strList = schoolsList.getRecords().stream().map(JSONUtil::toJsonStr).collect(Collectors.toList());
        stringRedisTemplate.opsForList().leftPushAll(RedisConstants.SCHOOL_ALL_LIST_KEY+ ":" + current, strList);
        stringRedisTemplate.expire(SCHOOL_ALL_LIST_KEY, SCHOOL_ALL_LIST_TTL, TimeUnit.MINUTES);
        return Result.ok(schoolsList);
    }

    @Override
    public Result querySchoolById(Long id) {
        School school;
        // 调用封装的redis查询工具，输入参数：redis_key，id，返回类型，数据库查询方法，缓存时间，缓存时间单位
        // queryWithLogicalExpire 逻辑过期解决缓存击穿问题
        school = cacheClient.queryWithLogicalExpire(CACHE_SCHOOL_KEY, id, School.class, this::getById, CACHE_SCHOOL_TTL, TimeUnit.MINUTES);
        if (school == null) {
            return Result.fail("未找到对应的院校信息");
        }
        return Result.ok(school);
    }

    /**
     * 缓存预热
     *
     * @param expireSeconds 过期时间
     * @throws InterruptedException 线程中断异常
     */
    public void saveSchools2Redis(Long expireSeconds) throws InterruptedException {
        List<School> schools = list();
        for (School school : schools) {
            RedisData redisData = new RedisData();
            // 添加过期时间，随机添加1-10的随机数，防止雪崩
            Random random = new Random();
            redisData.setExpireTime(LocalDateTime.now().plusSeconds(expireSeconds).plusSeconds(random.nextInt(10)));
            redisData.setData(school);
            stringRedisTemplate.opsForValue().set(CACHE_SCHOOL_KEY + school.getId(), JSONUtil.toJsonStr(redisData));
        }
    }
}




