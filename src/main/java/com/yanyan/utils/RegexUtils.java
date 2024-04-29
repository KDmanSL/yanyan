package com.yanyan.utils;

import cn.hutool.core.util.StrUtil;

public class RegexUtils {
    public static boolean isEmailInvalid(String email){
        return mismatch(email, RegexPatterns.EMAIL_REGEX);
    }
    public static boolean isPasswordInvalid(String password) {return mismatch(password, RegexPatterns.PASSWORD_REGEX);}
    public static boolean isPostTitleInvalid(String title){
        return mismatch(title, RegexPatterns.POST_TITLE_REGEX);
    }
    public static boolean isPostContentInvalid(String content){
        return mismatch(content, RegexPatterns.POST_CONTENT_REGEX);
    }
    private static boolean mismatch(String str, String regex){
        if (StrUtil.isBlank(str)) {
            return true;
        }
        return !str.matches(regex);
    }
}
