package com.changgou.goods.service.impl;

import com.changgou.core.service.impl.CoreServiceImpl;
import com.changgou.goods.dao.CategoryMapper;
import com.changgou.goods.dao.SpecMapper;
import com.changgou.goods.pojo.Category;
import com.changgou.goods.pojo.Spec;
import com.changgou.goods.service.SpecService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/****
 * @Author:admin
 * @Description:Spec业务层接口实现类
 * @Date 2019/6/14 0:16
 *****/
@Service
public class SpecServiceImpl extends CoreServiceImpl<Spec> implements SpecService {
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private SpecMapper specMapper;

    @Autowired
    public SpecServiceImpl(SpecMapper specMapper) {
        super(specMapper, Spec.class);
        this.specMapper = specMapper;
    }


    @Override
    public List<Spec> findByCategoryId(Integer id) {
        //1.根据点击到的分类的ID 获取模板的ID  select template_id from tb_category where id=
        Category category = categoryMapper.selectByPrimaryKey(id);
        Integer templateId = category.getTemplateId();
        //2.根据模板的ID 获取规格的列表数据    select * from tb_spec where template_id=42(上边的那个template_id)
        Spec condition = new Spec();
        condition.setTemplateId(templateId);
        List<Spec> specList = specMapper.select(condition);
        //3.返回规格的列表
        return specList;
    }
}
