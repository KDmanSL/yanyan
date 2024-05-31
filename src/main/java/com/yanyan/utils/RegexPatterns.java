package com.yanyan.utils;


public abstract class RegexPatterns {
    /**
     * 邮箱正则
     */
    public static final String EMAIL_REGEX = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";
    /**
     * 密码正则。4~32位的字母、数字、下划线
     */
    public static final String PASSWORD_REGEX = "^\\w{4,32}$";
    /**
     * 验证码正则, 6位数字或字母
     */
    public static final String VERIFY_CODE_REGEX = "^[a-zA-Z\\d]{6}$";
    /**
     * 帖子标题正则，1-20位的字符
     */
    public static final String POST_TITLE_REGEX = "^.{1,50}$";
    /**
     * 帖子内容正则，1-1000位的字符
     */
    public static final String POST_CONTENT_REGEX = "^.{1,1000}$";

}
