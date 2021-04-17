package com.changgou.order.service.impl;

import com.changgou.goods.feign.SkuFeign;
import com.changgou.goods.feign.SpuFeign;
import com.changgou.goods.pojo.Sku;
import com.changgou.goods.pojo.Spu;
import com.changgou.order.pojo.OrderItem;
import com.changgou.order.service.CartService;
import entity.Result;
import org.assertj.core.internal.bytebuddy.asm.Advice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author ljh
 * @version 1.0
 * @date 2020/10/10 15:01
 * @description 标题
 * @package com.changgou.order.service.impl
 */
@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private SkuFeign skuFeign;
    @Autowired
    private SpuFeign spuFeign;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void add(Integer num, Long id, String username) {

        if(num<=0){
            //删除掉
            redisTemplate.boundHashOps("Cart_" + username).delete(id);
            return;
        }

        //1.根据ID获取商品的数据
        //1.1 changgou-service-goods-api 中创建接口 添加方法
        //1.2 changgou-service-goods 实现接口
        //1.3 添加依赖 启用feignclients 注入 使用
        Result<Sku> result = skuFeign.findById(id);

        Sku sku = result.getData();
        if(sku!=null) {
            //通过feign调用获取spu的数据
            Result<Spu> spuResult = spuFeign.findById(sku.getSpuId());
            Spu spu = spuResult.getData();
            //2. 将sku的数据转换成orderItem POJO

            OrderItem orderItem = new OrderItem();
            orderItem.setSkuId(id);
            orderItem.setName(sku.getName());
            orderItem.setPrice(sku.getPrice());
            //购买的数量
            orderItem.setNum(num);
            //小计的金额
            orderItem.setMoney(sku.getPrice() * num);
            //实付金额 todo
            orderItem.setPayMoney(orderItem.getMoney());
            //图片
            orderItem.setImage(sku.getImage());

            orderItem.setSpuId(sku.getSpuId());

            //设置分类 1 2 3

            orderItem.setCategoryId1(spu.getCategory1Id());
            orderItem.setCategoryId2(spu.getCategory2Id());
            orderItem.setCategoryId3(spu.getCategory3Id());

            //3.添加到redis中 hset key field value
            redisTemplate.boundHashOps("Cart_" + username).put(id, orderItem);
        }
    }

    @Override
    public List<OrderItem> list(String username) {
        //根据用户名从redis中获取该用户的购物车的列表数据
        List values = redisTemplate.boundHashOps("Cart_" + username).values();
        return values;
    }
}
