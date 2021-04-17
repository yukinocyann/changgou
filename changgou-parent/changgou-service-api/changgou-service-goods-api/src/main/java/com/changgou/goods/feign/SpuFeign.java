package com.changgou.goods.feign;

import com.changgou.goods.pojo.Spu;
import entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author ljh
 * @version 1.0
 * @date 2020/10/10 15:34
 * @description 标题
 * @package com.changgou.goods.feign
 */
@FeignClient(name="goods",path = "/spu")
public interface SpuFeign {
    //根据SPU的ID 获取SPU
    @GetMapping("/{id}")
    public Result<Spu> findById(@PathVariable(name = "id") Long id);
}
