package com.changgou.search.controller;

import com.changgou.search.feign.SkuFeign;
import entity.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * @author ljh
 * @version 1.0
 * @date 2020/9/29 11:07
 * @description 标题
 * @package com.changgou.search.controller
 */
@Controller
@RequestMapping("/search")
public class SkuController {

    @Autowired
    private SkuFeign skuFeign;

    /**
     * 参数 map
     * 返回 map
     *
     * 接收用户的请求 调用feign 获取搜索的结果 进行数据渲染 返回html页面给用户看
     * @return
     */
    @GetMapping("/list")
    public String search(@RequestParam(required = false) Map<String,String> searchMap, Model model){
        //1.接收页面传递的参数
        //2.调用feign 获取到es中的搜索的结果
            //2.1 在service-search-api中创建feig接口
            //2.2 写一个方法 （搜索ES的数据）
            //2.3 在service-search微服务中实现feign的接口的方法

            //2.4 在web-search微服务中使用feign  启用feiggnclients(basepackages="")
            //2.5 注入feign 调用
        Map resultMap = skuFeign.search(searchMap);
        //3.封装结果集
        //4.设置model值,返回    map
        model.addAttribute("result",resultMap);
        //回显
        model.addAttribute("searchMap",searchMap);

        //先获取到url 然后进行拼接 组装好 再放入到model中返回给页面
        String url = url(searchMap);
        model.addAttribute("url",url);

        //封装分页对象 返回给model
        Page page = new Page(Long.valueOf(resultMap.get("total").toString()),
                Integer.valueOf(resultMap.get("pageNum").toString()),
                Integer.valueOf(resultMap.get("pageSize").toString()));
        model.addAttribute("page",page);
        return "search";
    }

    //解析页面传递过来的参数的和路径 /search/list?     keywords=zhangsan&category=手机&brand=华为&pageNum=1

    private String url(Map<String, String> searchMap) {
        String url = "/search/list";
        if(searchMap!=null && searchMap.size()>0) {
            url+="?";
            for (Map.Entry<String, String> stringStringEntry : searchMap.entrySet()) {

                String key = stringStringEntry.getKey();//keywords

                if(key.equalsIgnoreCase("pageNum")){
                    continue;
                }
                String value = stringStringEntry.getValue();//zhangsan
                url+=key+"="+value+"&";

            }
            url=url.substring(0,url.length()-1);
        }
        return url;
    }

}
