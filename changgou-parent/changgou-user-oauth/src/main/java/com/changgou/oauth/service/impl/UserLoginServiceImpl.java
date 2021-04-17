package com.changgou.oauth.service.impl;

import com.changgou.oauth.service.UserLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.Map;

/*
 * @Date 2021/3/5 19:47
 * @param null
 * @return
 * @Description //
 **/
@Service
public class UserLoginServiceImpl implements UserLoginService {

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public Map<String, String> login(String grant_type, String username, String password, String clientId, String secret) {
        String url = "http://localhost:9001/oauth/token";

        MultiValueMap<String, String> body = new LinkedMultiValueMap<String,String>();
        body.add("grant_type",grant_type);
        body.add("username",username);
        body.add("password",password);
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<String,String>();
        headers.add("Authorization","Basic "+Base64.getEncoder().encodeToString(new String(clientId+":"+secret).getBytes()));
        //参数1 指定请求体对象
        //参数2 指定请求头对象
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<MultiValueMap<String, String>>(body,headers);
        //参数1 指定要发送请求的url路径
        //参数2 指定要发送的请求的类型 POST
        //参数3 指定请求的实体对象（封装了 Header body 认证信息）
        //参数4 指定响应的数据类型  map
        ResponseEntity<Map> exchange = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Map.class);
        Map body1 = exchange.getBody();
        return body1;
    }

    public static void main(String[] args) {
        byte[] decode = Base64.getDecoder().decode(new String("Y2hhbmdnb3U6Y2hhbmdnb3U=").getBytes());
        System.out.println(new String(decode));

        String s = Base64.getEncoder().encodeToString(new String("changgou:changgou").getBytes());
        System.out.println(s);
    }
}
