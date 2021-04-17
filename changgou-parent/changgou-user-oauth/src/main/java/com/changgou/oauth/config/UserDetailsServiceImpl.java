package com.changgou.oauth.config;

import com.changgou.user.feign.UserFeign;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/***
 * 描述
 * @author ljh
 * @packagename com.itheima.config
 * @version 1.0
 * @date 2020/1/10
 */
@Component
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserFeign userFeign;

    /**
     * 根据用户名获取用户名对应的密码和相关的数据（权限 角色 。。。。。） 然后进行校验
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("获取到的用户名是：" + username);
        //todo
        String permission = "ROLE_ADMIN,ROLE_USER";

        //1.获取页面传递过来的用户名
        //2.通过feign调用用户微服务  根据用户名获取用户对象数据
        //2.1 在changgou-service-user-api中创建接口 feign
        //2.2 添加注解Feignclient
        //2.3 添加一个方法 用来根据用户名获取用户数据的
        //2.4 在changgou-service-user微服务中实现接口的方法


        //2.5 认证微服务添加user-api的依赖 和feign的起步依赖
        //2.6 启用feignclients(basePackages="")
        //2.7 注入 调用
        Result<com.changgou.user.pojo.User> result = userFeign.findById(username);
        if (result.getData() == null) {
            //3.判断用户是否存在 如果不存在 返回 null
            return null;
        }



        //4.获取密码数据（加密后的） 交给springsecurity 自动的进行匹配
        String password = result.getData().getPassword();
       /* return new User(username, passwordEncoder.encode("szitheima"),
                AuthorityUtils.commaSeparatedStringToAuthorityList(permission));*/


       /* List<GrantedAuthority> list = new ArrayList<>();
        list.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        list.add(new SimpleGrantedAuthority("ROLE_USER"));*/
        return new User(username, password,
                AuthorityUtils.commaSeparatedStringToAuthorityList(permission));

    }
}
