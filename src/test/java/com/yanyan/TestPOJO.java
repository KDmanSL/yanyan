package com.yanyan;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.HexUtil;
import cn.hutool.crypto.asymmetric.AsymmetricCrypto;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import com.yanyan.utils.RSASecurityUtils;

import java.util.Base64;

import static com.yanyan.utils.SystemConstants.PASSWORD_ENCRYPTION_ALGORITHM;
import static com.yanyan.utils.SystemConstants.PRIVATE_KEY;
import static com.yanyan.utils.SystemConstants.PUBLIC_KEY;


public class TestPOJO {

    public static void main(String[] args) throws Exception {
        testRSA2();
    }

    static void testRSA2(){
        //已知私钥及密文如何加解密
        String PRIVATE_KEY = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAIL7pbQ+5KKGYRhw7jE31hmA"
                + "f8Q60ybd+xZuRmuO5kOFBRqXGxKTQ9TfQI+aMW+0lw/kibKzaD/EKV91107xE384qOy6IcuBfaR5lv39OcoqNZ"
                + "5l+Dah5ABGnVkBP9fKOFhPgghBknTRo0/rZFGI6Q1UHXb+4atP++LNFlDymJcPAgMBAAECgYBammGb1alndta"
                + "xBmTtLLdveoBmp14p04D8mhkiC33iFKBcLUvvxGg2Vpuc+cbagyu/NZG+R/WDrlgEDUp6861M5BeFN0L9O4hz"
                + "GAEn8xyTE96f8sh4VlRmBOvVdwZqRO+ilkOM96+KL88A9RKdp8V2tna7TM6oI3LHDyf/JBoXaQJBAMcVN7fKlYP"
                + "Skzfh/yZzW2fmC0ZNg/qaW8Oa/wfDxlWjgnS0p/EKWZ8BxjR/d199L3i/KMaGdfpaWbYZLvYENqUCQQCobjsuCW"
                + "nlZhcWajjzpsSuy8/bICVEpUax1fUZ58Mq69CQXfaZemD9Ar4omzuEAAs2/uee3kt3AvCBaeq05NyjAkBme8SwB0iK"
                + "kLcaeGuJlq7CQIkjSrobIqUEf+CzVZPe+AorG+isS+Cw2w/2bHu+G0p5xSYvdH59P0+ZT0N+f9LFAkA6v3Ae56OrI"
                + "wfMhrJksfeKbIaMjNLS9b8JynIaXg9iCiyOHmgkMl5gAbPoH/ULXqSKwzBw5mJ2GW1gBlyaSfV3AkA/RJC+adIjsRGg"
                + "JOkiRjSmPpGv3FOhl9fsBPjupZBEIuoMWOC8GXK/73DHxwmfNmN7C9+sIi4RBcjEeQ5F5FHZ";

        RSA rs2 = new RSA(PRIVATE_KEY, null);
        String a = "2707F9FD4288CEF302C972058712F24A5F3EC62C5A14AD2FC59DAB93503AA0FA17113A020EE4EA35EB53F"+
                "75F36564BA1DABAA20F3B90FD39315C30E68FE8A1803B36C29029B23EB612C06ACF3A34BE815074F5EB5AA3A"+
                "C0C8832EC42DA725B4E1C38EF4EA1B85904F8B10B2D62EA782B813229F9090E6F7394E42E6F44494BB8";
        byte[] aByte = HexUtil.decodeHex(a);
        byte[] decrypt3 = rs2.decrypt(aByte,KeyType.PrivateKey);
        byte[] encode1 = Base64.getEncoder().encode(decrypt3);
        String txt = new String(Base64.getDecoder().decode(encode1));
        System.out.println("解密后的文本信息：" + txt);    //虎头闯杭州,多抬头看天,切勿只管种地
    }

//    static void testRSA() throws Exception {
//        RSASecurityUtils rsaSecurityUtils = new RSASecurityUtils();
//        AsymmetricCrypto asymmetricCrypto = new AsymmetricCrypto(PASSWORD_ENCRYPTION_ALGORITHM, rsaSecurityUtils.getRSAPrivateKeyBybase64(PRIVATE_KEY),
//                rsaSecurityUtils.getRSAPublidKeyBybase64(PUBLIC_KEY));
//
//        String s1 = asymmetricCrypto.encryptBase64("1111", KeyType.PublicKey);
//        System.out.println(s1);
//
//        String s = asymmetricCrypto.decryptStr(s1, KeyType.PrivateKey);
//        System.out.println(s);
//
//    }
}
