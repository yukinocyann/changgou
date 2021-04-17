package com.changgou.order.config;

import com.alibaba.fastjson.JSON;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author ljh
 * @version 1.0
 * @date 2020/10/10 17:25
 * @description 标题
 * @package com.changgou.order.config
 */
@Component
public class TokenDecode {

    //公钥
    private static final String PUBLIC_KEY = "public.key";

    private String getPubKey() {
        Resource resource = new ClassPathResource(PUBLIC_KEY);
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(resource.getInputStream());
            BufferedReader br = new BufferedReader(inputStreamReader);
            return br.lines().collect(Collectors.joining("\n"));
        } catch (IOException ioe) {
            return null;
        }
    }

    //获取令牌数据对应的明文的数据 将令牌数据解析出来 就是map
    public Map<String, String> getUserInfo() {
        OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) SecurityContextHolder.getContext().getAuthentication().getDetails();
        //获取令牌
        String tokenValue = details.getTokenValue();
        //解析
        //校验Jwt
        Jwt jwt = JwtHelper.decodeAndVerify(tokenValue, new RsaVerifier(getPubKey()));
        //获取Jwt原始内容
        String claims = jwt.getClaims();
        Map<String,String> map = JSON.parseObject(claims, Map.class);
        //todo 当前的登录的用户名
        //String username =map.get("user_name");
        return map;
    }

    //获取用户名
    public String getUsername() {
        return getUserInfo().get("user_name");
    }
}
