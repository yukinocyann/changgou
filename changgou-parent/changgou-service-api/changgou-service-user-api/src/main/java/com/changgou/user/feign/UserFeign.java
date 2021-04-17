package com.changgou.user.feign;

import com.changgou.user.pojo.User;
import entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author ljh
 * @version 1.0
 * @date 2020/10/10 10:59
 * @description 标题
 * @package com.changgou.user.feign
 */
@FeignClient(name="user",path = "/user")
//@RequestMapping("/user") 等价于path = "/user"
public interface UserFeign {
    /**
     * 根据用户名获取用户的数据
     * @param id  用户名
     * @return
     */
    @GetMapping("/load/{id}")
    Result<User> findById(@PathVariable(name="id") String id);

    //加积分
    @GetMapping("/points/add")
    public Result addPoints(@RequestParam(name="username") String username,
                            @RequestParam(name="points") Integer points);
}
