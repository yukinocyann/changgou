package com.changgou.seckill.service.impl;

import com.changgou.core.service.impl.CoreServiceImpl;
import com.changgou.seckill.dao.SeckillGoodsMapper;
import com.changgou.seckill.dao.SeckillOrderMapper;
import com.changgou.seckill.pojo.SeckillGoods;
import com.changgou.seckill.pojo.SeckillOrder;
import com.changgou.seckill.service.SeckillOrderService;
import com.changgou.seckill.status.SeckillStatus;
import com.changgou.seckill.task.MultiThreadingCreateOrder;
import entity.IdWorker;
import entity.SystemConstants;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.client.RedisClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/****
 * @Author:admin
 * @Description:SeckillOrder业务层接口实现类
 * @Date 2019/6/14 0:16
 *****/
@Service
public class SeckillOrderServiceImpl extends CoreServiceImpl<SeckillOrder> implements SeckillOrderService {

    private SeckillOrderMapper seckillOrderMapper;

    @Autowired
    public SeckillOrderServiceImpl(SeckillOrderMapper seckillOrderMapper) {
        super(seckillOrderMapper, SeckillOrder.class);
        this.seckillOrderMapper = seckillOrderMapper;
    }

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private MultiThreadingCreateOrder multiThreadingCreateOrder;

    @Autowired
    private RedissonClient redisClient;

    @Override
    public Boolean add(Long id, String time, String username) {

        //先判断用户是否重复排队 在支付成功再删除即可。
        Long increment = redisTemplate.boundHashOps(SystemConstants.SEC_KILL_QUEUE_REPEAT_KEY).increment(username, 1);
        if(increment>1){
            throw new RuntimeException("你重复排队了");
        }

        //判断用户是否有未支付的订单
        Object o = redisTemplate.boundHashOps(SystemConstants.SEC_KILL_ORDER_KEY).get(username);
        if(o!=null){
            throw new RuntimeException("你有未支付的订单");
        }

        //1.先根据商品的ID 从redis中获取商品的数据
        //SeckillGoods seckillGoods = (SeckillGoods) redisTemplate.boundHashOps(SystemConstants.SEC_KILL_GOODS_PREFIX + time).get(id);
        //减库存 这个逻辑需要上锁 1.获取锁对象 2.上锁 3，执行代码 4.释放锁
        RLock mylock = redisClient.getLock("Mylock");
        try {
            mylock.lock(20,TimeUnit.SECONDS);
            //减库存
            dercount(id,time);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }finally {
            mylock.unlock();
        }

        //2 先要把用户压入队列  创建一个POJO 抢单的状态默认就是排队中
        SeckillStatus seckillStatus = new SeckillStatus(username, new Date(), 1, id, time);
        redisTemplate.boundListOps(SystemConstants.SEC_KILL_USER_QUEUE_KEY).leftPush(seckillStatus);

        //3.存储用户下单的状态信息
        redisTemplate.boundHashOps(SystemConstants.SEC_KILL_USER_STATUS_KEY).put(username,seckillStatus);

        //4 多线程下单
        multiThreadingCreateOrder.createOrder();


        return true;
    }

    private void dercount(Long id, String time) {
        SeckillGoods seckillGoods = (SeckillGoods) redisTemplate.boundHashOps(SystemConstants.SEC_KILL_GOODS_PREFIX + time).get(id);
        if (seckillGoods == null || seckillGoods.getStockCount() <= 0) {
            //2.判断商品是否存在 或者 是否有库存 如果商品不存在 库存为0 抛出异常
            throw new RuntimeException("商品卖完了");
        }
        //3.减库存
        seckillGoods.setStockCount(seckillGoods.getStockCount() - 1);
        redisTemplate.boundHashOps(SystemConstants.SEC_KILL_GOODS_PREFIX + time).put(id, seckillGoods);
    }

    @Override
    public SeckillStatus queryStatus(String username) {
        return (SeckillStatus) redisTemplate.boundHashOps(SystemConstants.SEC_KILL_USER_STATUS_KEY).get(username);
    }
}
