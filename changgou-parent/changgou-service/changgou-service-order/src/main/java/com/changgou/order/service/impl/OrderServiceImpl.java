package com.changgou.order.service.impl;

import com.changgou.core.service.impl.CoreServiceImpl;
import com.changgou.goods.feign.SkuFeign;
import com.changgou.order.dao.OrderItemMapper;
import com.changgou.order.dao.OrderMapper;
import com.changgou.order.pojo.Order;
import com.changgou.order.pojo.OrderItem;
import com.changgou.order.service.OrderService;
import com.changgou.order.vo.OrderVo;
import com.changgou.user.feign.UserFeign;
import entity.IdWorker;
import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/****
 * @Author:admin
 * @Description:Order业务层接口实现类
 * @Date 2019/6/14 0:16
 *****/
@Service
public class OrderServiceImpl extends CoreServiceImpl<Order> implements OrderService {

    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    public OrderServiceImpl(OrderMapper orderMapper) {
        super(orderMapper, Order.class);
        this.orderMapper = orderMapper;
    }

    @Autowired
    private IdWorker idWorker;
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private SkuFeign skuFeign;
    @Autowired
    private UserFeign userFeign;

    //添加订单
    @Override
    @Transactional(rollbackFor = Exception.class)//本地事务
    @GlobalTransactional//开启全局分布式事务管理
    public OrderVo add(Order order) {
        //1.添加订单
        //1.1生成订单的主键
        String orderId = idWorker.nextId() + "";
        order.setId(orderId);
        //直接从购物车（redis中）中获取 循环遍历算出来总金额
        List<OrderItem> values = redisTemplate.boundHashOps("Cart_" + order.getUsername()).values();

        Integer totalNum=0;
        Integer totalMoney=0;

        for (OrderItem orderItem : values) {
            Integer money = orderItem.getMoney();//小计
            Integer num = orderItem.getNum();//购买的数量
            totalNum+=num;
            totalMoney+=money;
            //2.添加订单选项
            //2.1 设置主键
            String orderItemId = idWorker.nextId() + "";
            orderItem.setId(orderItemId);

            //2.2 设置所属的订单的ID
            orderItem.setOrderId(order.getId());

            orderItemMapper.insertSelective(orderItem);
            //3.更新库存 update tb_sku set num=num-#{num} where id=#{id} and num>=#{num}
            //3.1 changgou-service-goods-api创建接口 添加注解和方法
            //3.2 changgou-service-goods controller【实现】接口
            //3.3 order微服务中添加依赖
            //3.4 开启feignclients
            //3.5 注入 调用
            skuFeign.decCount(orderItem.getSkuId(),orderItem.getNum());
        }
        //1.2 设置总数量
        order.setTotalNum(totalNum);
        //1.3 设置总金额
        order.setTotalMoney(totalMoney);

        order.setPayMoney(totalMoney);

        order.setPostFee(0);//免邮费
        //1.4 设置创建时间 和更新时间
        order.setCreateTime(new Date());
        order.setUpdateTime(order.getCreateTime());
        //1.5 设置订单所属的用户 controller已经设置


        order.setBuyerRate("0");//未评价
        order.setSourceType("1");//web
        order.setOrderStatus("0");
        order.setPayStatus("0");
        order.setConsignStatus("0");
        order.setIsDelete("0");//未删除
        orderMapper.insertSelective(order);

        //4.添加积分 feign update tb_user set points=points+#{points} where username=#{username}
        //4.1 changgou-service-user-api中创建接口和方法
        //4.2 changgou-service-user的controller中【实现】方法
        //4.3 加入依赖 启用feignclients 注入 调用
        userFeign.addPoints(order.getUsername(),10);
        //5.清空购物车
        redisTemplate.delete("Cart_" + order.getUsername());
        //金额 单位分
        OrderVo orderVo = new OrderVo(1,orderId,totalMoney.toString());
        return orderVo;
    }
}
