package com.changgou.order.controller;

import com.changgou.core.AbstractCoreController;
import com.changgou.order.config.TokenDecode;
import com.changgou.order.pojo.Order;
import com.changgou.order.service.OrderService;
import com.changgou.order.vo.OrderVo;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/****
 * @Author:admin
 * @Description:
 * @Date 2019/6/14 0:18
 *****/

@RestController
@RequestMapping("/order")
@CrossOrigin
public class OrderController extends AbstractCoreController<Order>{

    private OrderService  orderService;

    @Autowired
    public OrderController(OrderService  orderService) {
        super(orderService, Order.class);
        this.orderService = orderService;
    }


    @Autowired
    private TokenDecode tokenDecode;
    /**
     * 添加订单 1.添加订单 2.更新库存 3 添加积分 4.清空购物车
     * @param
     * @return
     */
    @PostMapping("/add")
    public Result add(@RequestBody Order order) {
        String username = tokenDecode.getUsername();
        order.setUsername(username);
        OrderVo orderVo = orderService.add(order);
        //返回数据给前端 前端拿到数据进行跳转 给：订单号 金额 type类型


        return new Result(true, StatusCode.OK,"添加订单成功",orderVo);
    }
}
