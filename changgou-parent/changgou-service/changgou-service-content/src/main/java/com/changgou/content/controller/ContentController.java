package com.changgou.content.controller;

import com.changgou.content.pojo.Content;
import com.changgou.content.service.ContentService;
import com.changgou.core.AbstractCoreController;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/****
 * @Author:admin
 * @Description:
 * @Date 2019/6/14 0:18
 *****/

@RestController
@RequestMapping("/content")
@CrossOrigin
public class ContentController extends AbstractCoreController<Content> {

    private ContentService contentService;

    @Autowired
    public ContentController(ContentService contentService) {
        super(contentService, Content.class);
        this.contentService = contentService;
    }

    /**
     * 根据分类的ID 获取分类下的所有的广告的列表数据
     * @param categoryId
     * @return
     */
    @GetMapping(value = "/list/category/{id}")
    public Result<List<Content>> findByCategory(@PathVariable(name="id") Long categoryId){
        //select * from tb_content where category_id=1
        Content condition = new Content();
        condition.setCategoryId(categoryId);
        List<Content> contentList = contentService.select(condition);
        return new Result<List<Content>>(true, StatusCode.OK,"查询成功",contentList);
    }
}
