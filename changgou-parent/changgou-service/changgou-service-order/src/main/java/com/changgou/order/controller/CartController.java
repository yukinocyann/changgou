package com.changgou.order.controller;

import com.alibaba.fastjson.JSON;
import com.changgou.order.config.TokenDecode;
import com.changgou.order.pojo.OrderItem;
import com.changgou.order.service.CartService;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author ljh
 * @version 1.0
 * @date 2020/10/10 14:56
 * @description 标题
 * @package com.changgou.order.controller
 */
@RestController
@RequestMapping("/cart")
public class CartController {
    @Autowired
    private CartService cartService;
    @Autowired
    private TokenDecode tokenDecode;
    /**
     *
     * @param num 要购买的商品的数量
     * @param id 要购买的商品的SKU的ID
     * @return
     */
    @RequestMapping("/add")
    public Result add(Integer num,Long id){

        String username = tokenDecode.getUsername();

        cartService.add(num,id,username);

        return new Result(true, StatusCode.OK,"添加成功");
    }

    @GetMapping("/list")
    public Result<List<OrderItem>> list(){
        String username = tokenDecode.getUsername();
        List<OrderItem> orderItems = cartService.list(username);
        return new Result(true, StatusCode.OK,"查询成功",orderItems);

    }

}
