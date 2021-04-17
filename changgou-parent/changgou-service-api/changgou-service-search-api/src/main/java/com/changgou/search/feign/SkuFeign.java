package com.changgou.search.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

/**
 * @author ljh
 * @version 1.0
 * @date 2020/9/29 11:26
 * @description 标题
 * @package com.changgou.search.feign
 */
@FeignClient(name="search",path = "/search")
//@RequestMapping("/search") == path="/search"
public interface SkuFeign {
    @PostMapping
    public Map search(@RequestBody(required = false) Map<String, String> searchMap);
}
