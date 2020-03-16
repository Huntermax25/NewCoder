package com.lc.community.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.UUID;

/**
 * 封装一些功能
 */
public class CommunityUtil {

    // 生成随机字符串
    public static String generateUUID(){
        return UUID.randomUUID().toString().replaceAll("-","");
    }

    // MD5加密 +salt
    // 如果为空，则返回null
    // 不为空，返回spring自带的md5加密结果
    public static String md5(String key){
        if(StringUtils.isBlank(key)){
            return null;
        }
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }


}
