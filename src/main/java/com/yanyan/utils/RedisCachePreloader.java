package com.yanyan.utils;

import com.yanyan.service.BaiduAIService;
import com.yanyan.service.CourseService;
import com.yanyan.service.MajorService;
import com.yanyan.service.SchoolService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import static com.yanyan.utils.RedisConstants.*;
/**
 * 缓存预热类 进行缓存预热
 */
@Slf4j
@Component
public class RedisCachePreloader {
    @Autowired
    private MajorService majorService;
    @Autowired
    private SchoolService schoolService;
    @Autowired
    private BaiduAIService baiduAIService;
    @PostConstruct
    public void preloadCache(){
        try {
            schoolService.saveSchools2Redis(CACHE_SCHOOL_TTL);
            majorService.saveMajor2Redis(CACHE_MAJOR_TTL);
        }catch (Exception e){
            log.error("缓存预热失败:",e);
        }
        try{
            baiduAIService.createGroupMQList();
        }catch (Exception e){
            log.error("缓存流已存在:",e);
        }
    }
}
