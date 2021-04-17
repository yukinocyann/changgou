package com.changgou.user.service.impl;
import com.changgou.user.dao.UserMapper;
import com.changgou.user.pojo.User;
import com.changgou.user.service.UserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;
import com.changgou.core.service.impl.CoreServiceImpl;
import java.util.List;
/****
 * @Author:admin
 * @Description:User业务层接口实现类
 * @Date 2019/6/14 0:16
 *****/
@Service
public class UserServiceImpl extends CoreServiceImpl<User> implements UserService {

    private UserMapper userMapper;

    @Autowired
    public UserServiceImpl(UserMapper userMapper) {
        super(userMapper, User.class);
        this.userMapper = userMapper;
    }

    @Override
    public Integer addPoints(String username, Integer points) {
        return userMapper.addPoints(username,points);
    }
}
