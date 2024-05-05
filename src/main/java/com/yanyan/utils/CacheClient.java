package com.yanyan.utils;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Slf4j
@Component
public class CacheClient {
    private static final Long CACHE_NULL_TTL = 2L;//设置空值过期时间
    private static final String CACHE_LOCK= "cache:lock:";
    private final StringRedisTemplate stringRedisTemplate;
    private final RedissonClient redissonClient;
    public CacheClient(StringRedisTemplate stringRedisTemplate,RedissonClient redissonClient) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.redissonClient = redissonClient;
    }


    //设置普通KEY
    public void set(String key, Object value, Long time, TimeUnit unit) {
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(value),time,unit);
    }


    //设置逻辑过期KEY
    public void setWithLogicalExpire(String key, Object value, Long time, TimeUnit unit) {
        RedisData redisData = new RedisData();
        redisData.setData(value);
        redisData.setExpireTime(LocalDateTime.now().plusSeconds(unit.toSeconds(time)));

        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(redisData));
    }


    /**
     *
     * @param keyPrefix key头前缀
     * @param id 查询id（可以为任何类型）
     * @param type 标明返回值类型
     * @param dbFallback 数据库查询方法（函数）
     * @param time 缓存重建的过期时间
     * @param unit 过期时间单位
     * @return 查询结果
     * @param <R> 返回值类型
     * @param <ID> ID类型
     */
    public <R,ID> R queryWithPassThrough(String keyPrefix, ID id, Class<R> type, Function<ID, R> dbFallback,Long time, TimeUnit unit){
        String key = keyPrefix+id;
        //1.从redis查询缓存
        String json=stringRedisTemplate.opsForValue().get(key);
        //2.判断缓存是否存在
        if (StrUtil.isNotBlank(json)) {
            //3.如果存在，直接返回
            return JSONUtil.toBean(json, type);
        }
        //这里不等于null就说明shopJson为空字符串""返回对应的错误信息
        if(Objects.equals(json, "")){
            return null;
        }
        //4.如果不存在，从数据库查询
        R r = dbFallback.apply(id);
        //5.如果不存在，返回存入空值
        if (r == null) {
            stringRedisTemplate.opsForValue().set(key,"", CACHE_NULL_TTL, TimeUnit.MINUTES);
            return null;
        }
        //6.如果存在，写入redis
        set(key,r,time,unit);
        //7.返回
        return r;
    }
    //线程池
    private static final ExecutorService CACHE_REBUILD_EXECUTOR = Executors.newFixedThreadPool(10);

    /**
     * 查询（逻辑过期预防缓存击穿）
     * @param keyPrefix key前缀
     * @param id 查询id
     * @param type 返回值类型
     * @param dbFallback 数据库查询方法（函数）
     * @param time 逻辑过期的过期时间
     * @param unit 过期时间单位
     * @return 查询结果
     * @param <R> 返回值类型
     * @param <ID> ID类型
     */
    public <R,ID> R queryWithLogicalExpire(String keyPrefix ,ID id, Class<R> type, Function<ID, R> dbFallback,Long time, TimeUnit unit){
        String key = keyPrefix+id;
        //1.从redis查询商铺缓存
        String json=stringRedisTemplate.opsForValue().get(key);
        //2.判断缓存是否存在
        if (StrUtil.isBlank(json)) {
            //3.如果不存在，直接返回空值
            return null;
        }
        //4.存在，需要先把json反序列化对象
        RedisData redisData = JSONUtil.toBean(json, RedisData.class);
        R r= JSONUtil.toBean((JSONObject) redisData.getData(), type);
        LocalDateTime expireTime = redisData.getExpireTime();
        //5.判断是否过期
        if(expireTime.isAfter(LocalDateTime.now())){
            //5.1未过期，返回信息
            return r;
        }

        //6.缓存重建
        //6.1获取互斥锁
        String lockKey = CACHE_LOCK + id;
        RLock lock = redissonClient.getLock(lockKey);
        boolean isLock = lock.tryLock();
        //6.2判断是否获取锁成功
        if (isLock) {
            //6.3获取成功，开启独立线程，实现缓存重建
            CACHE_REBUILD_EXECUTOR.submit(() -> {
                try {
                    //查询数据库
                    R r1 = dbFallback.apply(id);
                    //写入redis
                    this.setWithLogicalExpire(key,r1,time,unit);
                }catch (Exception e){
                    throw new RuntimeException(e);
                }finally {
                    //释放锁
                    lock.unlock();
                }
            });
        }

        //7.返回商铺信息
        return r;
    }

    //内部类补充RedisData类型满足逻辑过期方法中添加逻辑过期时间
    @Data
    public static class RedisData {
        private LocalDateTime expireTime;
        private Object data;
    }

}

