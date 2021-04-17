package com.changgou.token;

import org.junit.Test;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;

/*****
 * @Author: www.itheima
 * @Date: 2019/7/7 13:48
 * @Description: com.changgou.token
 *  使用公钥解密令牌数据
 ****/
public class ParseJwtTest {

    /***
     * 校验令牌
     */
    @Test
    public void testParseToken(){
        //令牌
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJyb2xlcyI6IlJPTEVfVklQLFJPTEVfVVNFUiIsIm5hbWUiOiJpdGhlaW1hIiwiaWQiOiIxIn0.R2Yb-BZHA_gejYTc5r-GgYI5N-5kAnS-3mmCUgU23FyRkJVW8Zuxz_WpjEvKvrxh1Nh04lwoErWSPcO6goThlLVLmCYGD164caAv1wPey7eQFSRPmcfaVsHaR5EKjpXvQ64qtUDDMtDjfejd3q4cx7TKj0vRi_yQhLN4cEfnGKHZc1ySMaAfRLzWF68yyTGb42YiSMfu-lA8dfMaUDPpBYw4ewNxTaXXKl_qylmgGk32KR_pZqjaziZqh6aZd9qcoYg6FXcZkD0wxMHYuqFylbHdLaPZuFyMUUtz9z63EroPtH4guQsGVN2r68iD-9rvKRHITR8FYxyuGrylB3UGGA";

        //公钥
        String publickey = "-----BEGIN PUBLIC KEY-----MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEApeP7I97Dwj5jUKpwyNk1z9iKvKksuuaM4A5OGgSImf0kBlCSe7OFdvj79T3V/d+tCpJr2TnLP3be5HzxuzLIj70aytoaVrcG2Atb//6j44QeO+XwygA4dub1eWN4bkmCjh1hA1XUoqsJdIeh4HMAEAWRXhpms51/qvchlCE2Cp0cGUbaLYG6KAaWNkk7KtbbhnJvHUkgOwqd7giAimUJbyCcBtHZutW14efpVYvq/tXeI9AXMJhDhW18tQgqCrZyWyZ+6pIURQmfZ17CzwDfs2VM3fmiXWckzcP4FpacuK9HIxIDnqF4C4VYpxNIAxPd1y1FqypGu6VzqxbSPYIPnwIDAQAB-----END PUBLIC KEY-----";

        //校验Jwt
        Jwt jwt = JwtHelper.decodeAndVerify(token, new RsaVerifier(publickey));

        //获取Jwt原始内容
        String claims = jwt.getClaims();
        System.out.println(claims);
        //jwt令牌
        String encoded = jwt.getEncoded();
        System.out.println(encoded);
    }
}
