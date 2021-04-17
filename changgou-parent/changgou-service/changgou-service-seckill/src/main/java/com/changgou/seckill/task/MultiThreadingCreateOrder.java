package com.changgou.seckill.task;

import com.changgou.seckill.dao.SeckillGoodsMapper;
import com.changgou.seckill.pojo.SeckillGoods;
import com.changgou.seckill.pojo.SeckillOrder;
import com.changgou.seckill.status.SeckillStatus;
import entity.IdWorker;
import entity.SystemConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author ljh
 * @version 1.0
 * @date 2020/10/17 09:03
 * @description 标题
 * @package com.changgou.seckill.task
 */
@Component
public class MultiThreadingCreateOrder {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private IdWorker idWorker;


    //下单操作
    @Async//多线程开启注解
    public void createOrder(){
        System.out.println("下单开始=="+Thread.currentThread().getName());
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //从队列中弹出元素
        SeckillStatus seckillStatus = (SeckillStatus) redisTemplate.boundListOps(SystemConstants.SEC_KILL_USER_QUEUE_KEY).rightPop();
        if(seckillStatus!=null) {
            String time = seckillStatus.getTime();
            Long id = seckillStatus.getGoodsId();
            String username = seckillStatus.getUsername();

            SeckillGoods seckillGoods = (SeckillGoods) redisTemplate.boundHashOps(SystemConstants.SEC_KILL_GOODS_PREFIX + time).get(id);

            //4.判断商品库存是否为0  如果为0 需要更新到数据库中，并删除掉redis中的商品
            if (seckillGoods.getStockCount() <= 0) {
                seckillGoodsMapper.updateByPrimaryKeySelective(seckillGoods);
                //删除商品
                redisTemplate.boundHashOps(SystemConstants.SEC_KILL_GOODS_PREFIX + time).delete(id);
            }
            //5.下预订单到redis中
            SeckillOrder seckillOrder = new SeckillOrder();
            seckillOrder.setId(idWorker.nextId());//主键
            seckillOrder.setSeckillId(id);//购买的商品的ID
            seckillOrder.setMoney(seckillGoods.getCostPrice());//设置购买金额
            seckillOrder.setUserId(username);//用户名
            seckillOrder.setCreateTime(new Date());
            seckillOrder.setStatus("0");//未支付
            //key value : key :指定一个类别（订单存储） field:用户名  value：订单
            redisTemplate.boundHashOps(SystemConstants.SEC_KILL_ORDER_KEY).put(username, seckillOrder);
            //需要修改用户的抢单的状态
            seckillStatus.setStatus(2);//待支付
            seckillStatus.setOrderId(seckillOrder.getId());//用户抢的商品所属的订单的ID
            seckillStatus.setMoney(Float.valueOf(seckillOrder.getMoney()));//抢单的金额
            redisTemplate.boundHashOps(SystemConstants.SEC_KILL_USER_STATUS_KEY).put(username,seckillStatus);
        }
        System.out.println("下单结束==" + Thread.currentThread().getName());

    }
}
