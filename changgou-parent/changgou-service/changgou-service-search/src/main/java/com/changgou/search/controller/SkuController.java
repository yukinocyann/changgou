package com.changgou.search.controller;

import com.changgou.search.service.SkuService;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author ljh
 * @version 1.0
 * @date 2020/9/26 14:41
 * @description 标题
 * @package com.changgou.search.controller
 */
@RestController
@RequestMapping("/search")
public class SkuController {

    @Autowired
    private SkuService skuService;
    /**
     * 查询所有的商品的数据导入到es中
     * @return
     */
    @GetMapping("/import")
    public Result importFromDbToEs(){
        skuService.importSku();

        //3.返回/true/false
        return new Result(true, StatusCode.OK,"导入成功");
    }

    /**
     * 关键字搜索
     * @param searchMap  {keywords:"手机","brand":"华为","category":"手机"}
     * @return map 包括分类列表 品牌的列表 当前页的数据的列表
     */
    @PostMapping
    public Map search(@RequestBody Map<String,String> searchMap){
        return skuService.search(searchMap);
    }
}
