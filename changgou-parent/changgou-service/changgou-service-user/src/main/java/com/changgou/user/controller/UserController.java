package com.changgou.user.controller;

import com.alibaba.fastjson.JSON;
import com.changgou.user.pojo.User;
import com.changgou.user.service.UserService;
import com.github.pagehelper.PageInfo;
import entity.BCrypt;
import entity.JwtUtil;
import entity.Result;
import entity.StatusCode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import com.changgou.core.AbstractCoreController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/****
 * @Author:admin
 * @Description:
 * @Date 2019/6/14 0:18
 *****/

@RestController
@RequestMapping("/user")
@CrossOrigin
public class UserController extends AbstractCoreController<User> {

    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        super(userService, User.class);
        this.userService = userService;
    }

    //实现登录功能  暂时不用了
    @RequestMapping("/login")
    public Result login(String username, String password, HttpServletResponse response) {
        //1.判断用户名和密码是否为空 为空 返回错误
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            return new Result(false, StatusCode.LOGINERROR, "用户名或密码为空");
        }
        //2.根据用户名查询出数据库数据  判断是否有  如果没有  返回错误
        User user = userService.selectByPrimaryKey(username);
        if (user == null) {
            return new Result(false, StatusCode.LOGINERROR, "用户名或密码错误");
        }
        //3.校验密码是否正确 如果不正确 返回错误  bcrypt的加密方式
        if (!BCrypt.checkpw(password,user.getPassword())) {
            return new Result(false, StatusCode.LOGINERROR, "用户名或密码错误");
        }
        //4.校验成功 登录成功  生成令牌 并返回【令牌】 todo
        Map<String,Object> info = new HashMap<String,Object>();
        //根据业务情况
        info.put("role","USER");
        info.put("success","SUCCESS");
        info.put("username",username);

        String token = JwtUtil.createJWT(UUID.randomUUID().toString(), JSON.toJSONString(info), null);

        //存储到cookie中
        Cookie cookie = new Cookie("Authorization",token);
        //设置PATH
        cookie.setPath("/");
        response.addCookie(cookie);


        return new Result(true, StatusCode.OK, "登录成功",token);

    }

    /**
     * 根据用户的名称获取用户的数据
     * @param id
     * @return
     */
    @GetMapping("/load/{id}")
    Result<User> findById(@PathVariable(name="id") String id){
        User user =  userService.selectByPrimaryKey(id);
        return new Result<User>(true,StatusCode.OK,"查询成功",user);
    }

    //这个方法用来零时测试下  这个方法要被调用必须拥有 ROLE_ADMIN的权限才行
    @GetMapping("/findAllX")
    @PreAuthorize(value="hasAuthority('ROLE_ADMIN')")//在方法被调用之前先校验 校验通过了再执行
    public List<User> findAllX(){
        return userService.selectAll();
    }


    /**
     *
     * @param username
     * @param points
     * @return
     */
    @GetMapping("/points/add")
    public Result addPoints(@RequestParam(name="username") String username,
                            @RequestParam(name="points") Integer points){
        Integer count=  userService.addPoints(username,points);
        if(count>0){
            return new Result(true,StatusCode.OK,"添加积分成功");
        }else{
            return new Result(false,StatusCode.ERROR,"添加积分失败");
        }
    }




    public static void main(String[] args) {
        System.out.println(new BCryptPasswordEncoder().encode("123456"));
        byte[] strby = Base64.getDecoder().decode(new String("eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWV9").getBytes());
        String s = new String(strby);
        System.out.println(s);

    }
}
