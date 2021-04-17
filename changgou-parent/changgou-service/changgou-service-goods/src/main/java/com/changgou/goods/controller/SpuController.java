package com.changgou.goods.controller;

import com.changgou.core.AbstractCoreController;
import com.changgou.goods.pojo.Goods;
import com.changgou.goods.pojo.Spu;
import com.changgou.goods.service.SpuService;
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
@RequestMapping("/spu")
@CrossOrigin
public class SpuController extends AbstractCoreController<Spu> {

    private SpuService spuService;

    @Autowired
    public SpuController(SpuService spuService) {
        super(spuService, Spu.class);
        this.spuService = spuService;
    }
    /**
     * 添加/更新数据
     * @param goods
     * @return
     */
    @PostMapping("/save")
    public Result save(@RequestBody Goods goods){
        spuService.saveGoods(goods);
        return new Result(true, StatusCode.OK,"保存成功");
    }

    @GetMapping("/goods/{id}")
    public Result<Goods> findGoodsById(@PathVariable(name = "id")Long id){
        Goods goods = spuService.findGoodsById(id);
        return new Result<Goods>(true,StatusCode.OK,"查询成功",goods);
    }
}
