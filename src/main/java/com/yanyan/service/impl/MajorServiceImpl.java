package com.yanyan.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanyan.domain.Major;
import com.yanyan.dto.Result;
import com.yanyan.service.MajorService;
import com.yanyan.mapper.MajorMapper;
import com.yanyan.utils.CacheClient;
import com.yanyan.utils.RedisData;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static com.yanyan.utils.RedisConstants.CACHE_MAJOR_KEY;
import static com.yanyan.utils.RedisConstants.CACHE_MAJOR_TTL;

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
    public Result queryMajorById(Long id) {
        Major major;
        major = cacheClient.queryWithLogicalExpire(CACHE_MAJOR_KEY, id, Major.class, this::getById, CACHE_MAJOR_TTL, TimeUnit.SECONDS);
        if (major == null) {
            return Result.fail("未找到对应的专业信息");
        }
        return Result.ok(major);
    }
}




