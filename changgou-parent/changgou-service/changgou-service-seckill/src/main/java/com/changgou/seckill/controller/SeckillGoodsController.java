package com.changgou.seckill.controller;

import com.changgou.core.AbstractCoreController;
import com.changgou.seckill.pojo.SeckillGoods;
import com.changgou.seckill.service.SeckillGoodsService;
import entity.DateUtil;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.unit.DataUnit;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

/****
 * @Author:admin
 * @Description:
 * @Date 2019/6/14 0:18
 *****/

@RestController
@RequestMapping("/seckillGoods")
@CrossOrigin
public class SeckillGoodsController extends AbstractCoreController<SeckillGoods>{

    private SeckillGoodsService  seckillGoodsService;

    @Autowired
    public SeckillGoodsController(SeckillGoodsService  seckillGoodsService) {
        super(seckillGoodsService, SeckillGoods.class);
        this.seckillGoodsService = seckillGoodsService;
    }

    //获取当前的时间的5个时间段
    @GetMapping("/menus")
    public List<Date> menus(){
        return DateUtil.getDateMenus();
    }

    @GetMapping("/list")
    public List<SeckillGoods> list(String time){
        return seckillGoodsService.list(time);//???redis如何实现分页呢？
    }

    /**
     * 商品详情
     * @param time 要查询的商品所属的时间段的字符串
     * @param id 要查询的商品的ID
     * @return
     */
    @GetMapping("/one")
    public Result<SeckillGoods> one(String time,Long id){
        SeckillGoods seckillGoods =seckillGoodsService.one(time,id);
        return Result.ok(seckillGoods);
    }
}
