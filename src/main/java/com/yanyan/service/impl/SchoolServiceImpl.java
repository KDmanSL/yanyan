package com.yanyan.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanyan.domain.School;
import com.yanyan.dto.Result;
import com.yanyan.mapper.SchoolMapper;
import com.yanyan.service.SchoolService;
import com.yanyan.utils.CacheClient;
import com.yanyan.utils.RedisConstants;
import com.yanyan.utils.RedisData;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@Service
public class SchoolServiceImpl extends ServiceImpl<SchoolMapper, School>
        implements SchoolService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private CacheClient cacheClient;


    @Override
    public Result queryAllCoursesList(Integer current, Integer size) {
        int start = (current - 1) * size;
        int end = current * size - 1;
        //1.从redis查询商铺列表缓存
        List<String> listCache = stringRedisTemplate.opsForList().range(SCHOOL_ALL_LIST_KEY, 0, -1);
        //2.判断是否为空
        if (listCache != null && !listCache.isEmpty()) {
            //不为空，返回列表
            List<School> schoolsList = listCache.stream()
                    .map(str -> (School) JSONUtil.toBean(str, School.class, true))
                    .sorted(Comparator.comparingLong(School::getRanking))
                    .collect(Collectors.toList());
            // 检查分页索引，防止越界
            int listSize = schoolsList.size();
            start = Math.max(start, 0); // 确保开始索引不是负数
            end = Math.min(end, listSize - 1); // 确保结束索引不超出列表大小

            // 当开始索引超过列表大小时，返回空列表
            if (start >= listSize) {
                return Result.fail("超出页面请求范围");
            }

            // 安全地进行分页
            List<School> nowPageList = schoolsList.subList(start, end + 1);
            return Result.ok(nowPageList);
        }
        //3.缓存为空，查询数据库
        List<School> schoolsList = query().orderByAsc("ranking").list();
        if (schoolsList==null) {
            return Result.fail("查询院校列表失败");
        }

        // 将数据写入redis
        List<String> strList = schoolsList.stream().map(JSONUtil::toJsonStr).collect(Collectors.toList());
        stringRedisTemplate.opsForList().rightPushAll(RedisConstants.SCHOOL_ALL_LIST_KEY, strList);
        stringRedisTemplate.expire(SCHOOL_ALL_LIST_KEY, SCHOOL_ALL_LIST_TTL, TimeUnit.MINUTES);
        start = Math.max(start, 0);
        end = Math.min(end, schoolsList.size() - 1);
        if (start >= schoolsList.size()) {
            return Result.fail("超出页面请求范围");
        }
        // 返回当前页数据
        List<School> nowPageList = schoolsList.subList(start, end + 1);
        return Result.ok(nowPageList);
    }


    /**
     * 根据id查询院校信息
     *
     * @param id 院校id
     * @return 院校信息
     */
    @Override
    public School querySchoolById(Long id) {
        School school;
        // 调用封装的redis查询工具，输入参数：redis_key，id，返回类型，数据库查询方法，缓存时间，缓存时间单位
        // queryWithLogicalExpire 逻辑过期解决缓存击穿问题
        school = cacheClient.queryWithLogicalExpire(CACHE_SCHOOL_KEY, id, School.class, this::getById, CACHE_SCHOOL_TTL, TimeUnit.MINUTES);
        return school;
    }

    @Override


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
            redisData.setExpireTime(LocalDateTime.now().plusMinutes(expireSeconds).plusSeconds(random.nextInt(10)));
            redisData.setData(school);
            stringRedisTemplate.opsForValue().set(CACHE_SCHOOL_KEY + school.getId(), JSONUtil.toJsonStr(redisData));
        }
    }

    @Override
    public Result querySchoolByArea(String area, Integer current, Integer size) {
        int start = (current - 1) * size;
        int end = current * size - 1;
        //1.从redis查询商铺列表缓存
        List<String> listCache = stringRedisTemplate.opsForList().range(SCHOOL_ALL_LIST_KEY, 0,-1);
        //2.判断是否为空
        if (listCache != null && !listCache.isEmpty()) {
            //不为空，返回列表
            List<School> schoolsList = listCache.stream()
                    .map(str -> (School) JSONUtil.toBean(str, School.class, true))
                    .filter(school -> school.getLocation().equals(area))
                    .sorted(Comparator.comparingLong(School::getRanking))
                    .collect(Collectors.toList());
            if (schoolsList.isEmpty()) {
                return Result.fail("未找到该地区的院校信息");
            }
            // 检查分页索引，防止越界
            int listSize = schoolsList.size();
            start = Math.max(start, 0); // 确保开始索引不是负数
            end = Math.min(end, listSize - 1); // 确保结束索引不超出列表大小

            // 当开始索引超过列表大小时，返回空列表
            if (start >= listSize) {
                return Result.fail("超出页面请求范围");
            }

            // 安全地进行分页
            List<School> nowPageList = schoolsList.subList(start, end + 1);
            return Result.ok(nowPageList);
        }
        //3.缓存为空，查询数据库
        List<School> schoolsList = query().orderByAsc("ranking").list();
        if (schoolsList==null) {
            return Result.fail("查询院校列表失败");
        }
        // 将数据写入redis
        List<String> strList = schoolsList.stream().map(JSONUtil::toJsonStr).collect(Collectors.toList());
        stringRedisTemplate.opsForList().rightPushAll(RedisConstants.SCHOOL_ALL_LIST_KEY, strList);
        stringRedisTemplate.expire(SCHOOL_ALL_LIST_KEY, SCHOOL_ALL_LIST_TTL, TimeUnit.MINUTES);

        // 返回当前页数据
        List<School> nowPageList = schoolsList.stream()
                .filter(school -> school.getLocation().equals(area))
                .collect(Collectors.toList());
        if (nowPageList.isEmpty()) {
            return Result.fail("未找到该地区的院校信息");
        }
        start = Math.max(start, 0);
        end = Math.min(end, nowPageList.size() - 1);
        if (start >= nowPageList.size()) {
            return Result.fail("超出页面请求范围");
        }
        nowPageList = nowPageList.subList(start, end + 1);
        return Result.ok(nowPageList);
    }

    @Override
    public School querySchoolByName(String name) {
        List<String> listCache = stringRedisTemplate.opsForList().range(SCHOOL_ALL_LIST_KEY, 0,-1);
        if (listCache != null && !listCache.isEmpty()) {
            //不为空，返回列表
            List<School> schoolsList = listCache.stream()
                    .map(str -> (School) JSONUtil.toBean(str, School.class, true))
                    .filter(school -> school.getName().equals(name))
                    .collect(Collectors.toList());
            if (schoolsList.isEmpty()) {
                return null;
            }

            return schoolsList.get(0);
        }
        //3.缓存为空，查询数据库
        List<School> schoolsList = query().orderByAsc("ranking").list();

        // 将数据写入redis
        List<String> strList = schoolsList.stream().map(JSONUtil::toJsonStr).collect(Collectors.toList());
        stringRedisTemplate.opsForList().rightPushAll(RedisConstants.SCHOOL_ALL_LIST_KEY, strList);
        stringRedisTemplate.expire(SCHOOL_ALL_LIST_KEY, SCHOOL_ALL_LIST_TTL, TimeUnit.MINUTES);

        // 返回当前页数据
        List<School> schools = schoolsList.stream()
                .filter(school -> school.getName().equals(name))
                .collect(Collectors.toList());
        if (schools.isEmpty()) {
            return null;
        }

        return schools.get(0);
    }

}




