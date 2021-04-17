package com.changgou.oauth.controller;

import com.changgou.oauth.service.UserLoginService;
import com.changgou.oauth.util.CookieUtil;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/*
 * @Date 2021/3/5 18:47
 * @param null
 * @return
 * @Description //
 **/
@RestController
@RequestMapping("/user")
public class UserLogin {
    private static final String grant_type="password";
    private static final String clientId="changgou";
    private static final String secret="changgou";
    @Autowired
    private UserLoginService userLoginService;

    //接收用户的请求 传递 用户名 和密码 登录 颁发令牌（封装之后的）
    @RequestMapping("/login")
    public Result login(String username,String password){
        //1.判断是否为空 为空 返回错误
        if(StringUtils.isEmpty(username) || StringUtils.isEmpty(password)){
            return new Result(false, StatusCode.LOGINERROR,"用户名和密码不能为空");
        }
        //2.使用密码模式 在当前的工程 作为client 模拟 POSTMAN 发送请求给 认证服务器(当前工程) 申请令牌
                //2.1 封装参数 grant_type
                //2.2 封装参数 username
                //2.3 封装参数 password
                //2.4 封装参数 clientId 和secret
                //2.5 通过使用RestTemplate 模拟浏览器发送请求
        Map<String,String> map= userLoginService.login(grant_type,username,password,clientId,secret);
        //3.获取到令牌信息 再次进行封装 返回
        saveCookie(map.get("access_token"));
        return new Result(true,StatusCode.OK,"生成令牌成功",map.get("access_token"));
    }

    private void saveCookie(String token){
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        CookieUtil.addCookie(response,cookieDomain,"/","Authorization",token,cookieMaxAge,false);
    }

    //Cookie存储的域名
    @Value("${auth.cookieDomain}")
    private String cookieDomain;

    //Cookie生命周期
    @Value("${auth.cookieMaxAge}")
    private int cookieMaxAge;
}
