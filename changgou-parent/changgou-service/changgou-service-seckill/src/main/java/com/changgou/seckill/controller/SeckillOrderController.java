package com.changgou.seckill.controller;

import com.changgou.core.AbstractCoreController;
import com.changgou.seckill.pojo.SeckillOrder;
import com.changgou.seckill.service.SeckillOrderService;
import com.changgou.seckill.status.SeckillStatus;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/****
 * @Author:admin
 * @Description:
 * @Date 2019/6/14 0:18
 *****/

@RestController
@RequestMapping("/seckillOrder")
@CrossOrigin
public class SeckillOrderController extends AbstractCoreController<SeckillOrder>{

    private SeckillOrderService  seckillOrderService;

    @Autowired
    public SeckillOrderController(SeckillOrderService  seckillOrderService) {
        super(seckillOrderService, SeckillOrder.class);
        this.seckillOrderService = seckillOrderService;
    }

    /**
     * 下单
     * @param time 要买的商品所属的时间段
     * @param id 要买的商品
     * @return
     */
    @RequestMapping("/add")
    public Result add(String time,Long id){
        //1.获取当前的用户 这里为了测试写死
        String username = "zhangsan";
        //String username= UUID.randomUUID().toString();

        //2.下预订单
        Boolean flag = seckillOrderService.add(id, time, username);
        if(flag) {
            return new Result(true, StatusCode.OK, "排队成功,请稍等");
        }else{
            return new Result(false, StatusCode.ERROR, "下单失败");
        }
    }

    @GetMapping("/query")
    public Result<SeckillStatus> query(){
        //1.获取当前的登录的用户
        String username = "zhangsan";
        //2.从redis中查询出该用户的状态对象
        SeckillStatus seckillStatus =seckillOrderService.queryStatus(username);
        //设置数据类型 秒杀的订单
        seckillStatus.setType(2);
        //3.返回
        return Result.ok(seckillStatus);

    }



}
