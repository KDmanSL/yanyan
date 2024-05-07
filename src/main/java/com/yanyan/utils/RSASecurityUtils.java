package com.yanyan.utils;

import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Base64;

import static com.yanyan.utils.SystemConstants.PRIVATE_KEY;
import static com.yanyan.utils.SystemConstants.PUBLIC_KEY;
@Slf4j
@Component
public class RSASecurityUtils {
    private static final RSA rsa = new RSA(PRIVATE_KEY, PUBLIC_KEY);
    // RSA解密
    public String decrypt(String data) {
        byte[] encryptedData = Base64.getDecoder().decode(data);
        byte[] decrypt3 = rsa.decrypt(encryptedData, KeyType.PrivateKey);
        String txt = new String(decrypt3);
        //System.out.println("解密后的文本信息：" + txt);
        return txt;
    }

    // RSA加密
    public String encrypt(String data) {
        byte[] encrypt = rsa.encrypt(data.getBytes(), KeyType.PublicKey);
        String encode = Base64.getEncoder().encodeToString(encrypt);
        //System.out.println("加密后的文本信息：" + encode);
        return encode;
    }

}
