package com.changgou.goods.feign;

import com.changgou.goods.pojo.Sku;
import entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author ljh
 * @version 1.0
 * @date 2020/9/26 14:49
 * @description 标题
 * @package com.changgou.goods.feign
 */
@FeignClient(name="goods",path = "/sku")
// path = "/sku" 和 @requestMaping("/sku")效果一样
public interface SkuFeign {
    @GetMapping("/status/{status}")
    public Result<List<Sku>> findByStatus(@PathVariable(name="status") String status);
    //根据skuid 获取SKU的数据
    @GetMapping(value = "/{id}")
    public Result<Sku> findById(@PathVariable(value = "id") Long id);

    /**
     * 更新库存
     * @param id 要更新的商品的SKU 的ID
     * @param num 购买的数据
     * @return
     */
    @GetMapping("/decCount")
    public Result decCount(@RequestParam(name="id") Long id, @RequestParam(name="num") Integer num);


}
