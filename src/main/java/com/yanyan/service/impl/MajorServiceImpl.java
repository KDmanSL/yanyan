package com.yanyan.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanyan.domain.Major;
import com.yanyan.domain.School;
import com.yanyan.dto.Result;
import com.yanyan.service.MajorService;
import com.yanyan.mapper.MajorMapper;
import com.yanyan.utils.CacheClient;
import com.yanyan.utils.RedisConstants;
import com.yanyan.utils.RedisData;
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
* @description 针对表【yy_major(专业表)】的数据库操作Service实现
* @createDate 2024-04-05 17:23:43
*/
@Service
public class MajorServiceImpl extends ServiceImpl<MajorMapper, Major>
    implements MajorService{
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private CacheClient cacheClient;

    public void saveMajor2Redis(Long expireSeconds) throws InterruptedException {
        List<Major> majors = list();
        for (Major major : majors) {
            RedisData redisData = new RedisData();
            // 添加过期时间，随机添加1-10的随机数，防止雪崩
            Random random = new Random();
            redisData.setExpireTime(LocalDateTime.now().plusMinutes(expireSeconds).plusSeconds(random.nextInt(10)));
            redisData.setData(major);
            stringRedisTemplate.opsForValue().set(CACHE_MAJOR_KEY + major.getId(), JSONUtil.toJsonStr(redisData));
        }
    }

    @Override
    public Result queryAllMajorsList() throws InterruptedException {
        List<String> listCache = stringRedisTemplate.opsForList().range(MAJOR_ALL_LIST_KEY, 0,-1);
        if(listCache != null && !listCache.isEmpty()){
            List<Major> majorList = listCache.stream()
                    .map(str -> (Major) JSONUtil.toBean(str, Major.class, true))
                    .sorted(Comparator.comparingLong(Major::getId))
                    .collect(Collectors.toList());
            return Result.ok(majorList);
        }
        //3.缓存为空，查询数据库
        List<Major> majorsList = list();

        // 将数据写入redis
        List<String> strList = majorsList.stream().map(JSONUtil::toJsonStr).collect(Collectors.toList());
        stringRedisTemplate.opsForList().rightPushAll(RedisConstants.MAJOR_ALL_LIST_KEY, strList);
        stringRedisTemplate.expire(MAJOR_ALL_LIST_KEY, MAJOR_ALL_LIST_TTL, TimeUnit.MINUTES);

        return Result.ok(majorsList);
    }

    @Override
    public Major queryMajorById(Long id) {
        Major major;
        major = cacheClient.queryWithLogicalExpire(CACHE_MAJOR_KEY, id, Major.class, this::getById, CACHE_MAJOR_TTL, TimeUnit.SECONDS);
        return major;
    }

    @Override
    public Major queryMajorByName(String name) {
        List<String> listCache = stringRedisTemplate.opsForList().range(MAJOR_ALL_LIST_KEY, 0,-1);
        if (listCache != null && !listCache.isEmpty()) {
            //不为空，返回列表
            List<Major> majorsList = listCache.stream()
                    .map(str -> (Major) JSONUtil.toBean(str, Major.class, true))
                    .filter(major -> major.getName().equals(name))
                    .collect(Collectors.toList());
            if (majorsList.isEmpty()) {
                return null;
            }

            return majorsList.get(0);
        }
        //3.缓存为空，查询数据库
        List<Major> majorsList = list();

        // 将数据写入redis
        List<String> strList = majorsList.stream().map(JSONUtil::toJsonStr).collect(Collectors.toList());
        stringRedisTemplate.opsForList().rightPushAll(RedisConstants.MAJOR_ALL_LIST_KEY, strList);
        stringRedisTemplate.expire(MAJOR_ALL_LIST_KEY, MAJOR_ALL_LIST_TTL, TimeUnit.MINUTES);

        // 返回当前页数据
        List<Major> majors = majorsList.stream()
                .filter(major -> major.getName().equals(name))
                .collect(Collectors.toList());
        if (majors.isEmpty()) {
            return null;
        }

        return majors.get(0);
    }
}




