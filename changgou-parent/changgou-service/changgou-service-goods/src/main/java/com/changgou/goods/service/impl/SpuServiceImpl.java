package com.changgou.goods.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.core.service.impl.CoreServiceImpl;
import com.changgou.goods.dao.BrandMapper;
import com.changgou.goods.dao.CategoryMapper;
import com.changgou.goods.dao.SkuMapper;
import com.changgou.goods.dao.SpuMapper;
import com.changgou.goods.pojo.*;
import com.changgou.goods.service.SpuService;
import entity.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

/****
 * @Author:admin
 * @Description:Spu业务层接口实现类
 * @Date 2019/6/14 0:16
 *****/
@Service
public class SpuServiceImpl extends CoreServiceImpl<Spu> implements SpuService {

    private SpuMapper spuMapper;

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private BrandMapper brandMapper;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    public SpuServiceImpl(SpuMapper spuMapper) {
        super(spuMapper, Spu.class);
        this.spuMapper = spuMapper;
    }

    @Override
    //注解的声明式事务
    //判断 如果有SPU的ID 传递 有值 要更新 否则就是添加商品
    @Transactional(rollbackFor = Exception.class)
    public void saveGoods(Goods goods) {
        //1.获取页面传递过来的spu的数据
        Spu spu = goods.getSpu();
        if (spu.getId() != null) {
            //有值 要更新  更新SPU
            spuMapper.updateByPrimaryKeySelective(spu);
            // 还更新 sku列表 先删除掉原来的sku的列表 再进行 添加sku列表
            // 删除 delete from tb_sku where spu_id=?
            Sku condition = new Sku();
            condition.setSpuId(spu.getId());
            skuMapper.delete(condition);
        } else {
            //没有值 添加
            //1.1 要生成主键 使用雪花算法来生成
            long spuId = idWorker.nextId();
            spu.setId(spuId);
            spuMapper.insertSelective(spu);
        }
        //2.获取页面传递过来的sku的列表数据
        //=========================================start========================
        addSkuList(goods, spu);
        //=========================================end========================


    }

    @Override
    public Goods findGoodsById(Long id) {
        //1.从spu表中获取spu的数据 select * from tb_spu where is_delete=0 and id=?
        //todo 优化的
        Spu spu = spuMapper.selectByPrimaryKey(id);
        //2.从sku表中获取spu下的所有的sku的列表数据 select * from tb_sku where spu_id=?
        Sku condition = new Sku();
        condition.setSpuId(id);
        List<Sku> skuList = skuMapper.select(condition);
        //3.组装返回Goods
        Goods goods = new Goods();
        goods.setSpu(spu);
        goods.setSkuList(skuList);
        return goods;
    }

    private void addSkuList(Goods goods, Spu spu) {


        //2.获取页面传递过来的sku的列表数据
        List<Sku> skuList = goods.getSkuList();
        for (Sku sku : skuList) {
            //2.1 生成主键
            long skuId = idWorker.nextId();
            sku.setId(skuId);
            //2.2 设置sku的名称 spu名+空格+规格的具体的值 拼接而成的
            // spec的值：{"电视音响效果":"立体声","电视屏幕尺寸":"20英寸","尺码":"165"}
            String name = spu.getName();
            Map<String, String> map = JSON.parseObject(sku.getSpec(), Map.class);
            for (Map.Entry<String, String> stringStringEntry : map.entrySet()) {
                String value = stringStringEntry.getValue();// 立体声
                name += " " + value;
            }
            sku.setName(name);
            //2.3 设置创建时间
            sku.setCreateTime(new Date());
            sku.setUpdateTime(sku.getCreateTime());
            //2.4 设置所属的spu的ID值
            sku.setSpuId(spu.getId());

            //2.5 设置商品的三级分类的ID 和名称
            Category category = categoryMapper.selectByPrimaryKey(spu.getCategory3Id());
            if (category != null) {
                sku.setCategoryName(category.getName());
                sku.setCategoryId(spu.getCategory3Id());
            }
            //2.6 设置品牌的名称（根据品牌的ID获取品牌的名称）
            Brand brand = brandMapper.selectByPrimaryKey(spu.getBrandId());
            if (brand != null) {
                sku.setBrandName(brand.getName());
            }
            //正常
            sku.setStatus("1");
            skuMapper.insertSelective(sku);
        }

    }
}
