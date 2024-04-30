package com.yanyan.utils;

public class RedisConstants {

    public static final String LOGIN_CODE_KEY = "login:code:";
    public static final Long LOGIN_CODE_TTL = 2L;
    public static final String LOGIN_USER_KEY = "login:token:";
    public static final Long LOGIN_USER_TTL = 36000L;
    public static final String COURSE_ALL_LIST_KEY = "course:all:list";
    public static final Long COURSE_ALL_LIST_TTL = 30L;
    public static final String SCHOOL_ALL_LIST_KEY = "cache:all:school:";
    public static final Long SCHOOL_ALL_LIST_TTL = 30L;
    public static final String MAJOR_ALL_LIST_KEY = "cache:all:major:";
    public static final Long MAJOR_ALL_LIST_TTL = 30L;
    public static final String CACHE_SCHOOL_KEY = "cache:school:";
    public static final Long CACHE_SCHOOL_TTL = 30L;
    public static final String CACHE_MAJOR_KEY = "cache:major:";
    public static final Long CACHE_MAJOR_TTL = 30L;
    public static final String CACHE_COURSE_KEY = "cache:course:";
    public static final Long CACHE_COURSE_TTL = 30L;
    public static final String POST_ALL_LIST_KEY = "post:all:list";
    public static final Long POST_ALL_LIST_TTL = 30L;
    public static final Long CACHE_NULL_TTL = 2L;
    public static final String SECKILL_STOCK_KEY = "seckill:stock:";
    public static final String BLOG_LIKED_KEY = "blog:liked:";
    public static final String FEED_KEY = "feed:";
    public static final String USER_SIGN_KEY = "sign:";
}
