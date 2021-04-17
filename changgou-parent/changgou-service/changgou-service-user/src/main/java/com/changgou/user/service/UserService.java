package com.changgou.user.service;
import com.changgou.user.pojo.User;
import com.github.pagehelper.PageInfo;
import java.util.List;
import com.changgou.core.service.CoreService;
/****
 * @Author:admin
 * @Description:User业务层接口
 * @Date 2019/6/14 0:16
 *****/
public interface UserService extends CoreService<User> {

    Integer addPoints(String username, Integer points);
}
