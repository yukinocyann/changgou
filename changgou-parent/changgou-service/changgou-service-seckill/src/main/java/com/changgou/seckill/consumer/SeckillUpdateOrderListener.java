package com.changgou.seckill.consumer;

import com.alibaba.fastjson.JSON;
import com.changgou.seckill.dao.SeckillGoodsMapper;
import com.changgou.seckill.dao.SeckillOrderMapper;
import com.changgou.seckill.pojo.SeckillGoods;
import com.changgou.seckill.pojo.SeckillOrder;
import com.changgou.seckill.status.SeckillStatus;
import entity.SystemConstants;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author ljh
 * @version 1.0
 * @date 2020/10/17 16:11
 * @description 标题
 * @package com.changgou.seckill.consumer
 */
@Component
@RabbitListener(queues = "queue.seckillorder")
public class SeckillUpdateOrderListener {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private SeckillOrderMapper seckillOrderMapper;

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private RedissonClient redissonClient;

    //支付成功通知 发送消息 这边获取消息实现 删除预订单吧数据存储到mysql
    @RabbitHandler
    public void handlerSeckillOrder(String message) {
        //1.转换成map
        if (message != null) {
            Map<String, String> map = JSON.parseObject(message, Map.class);
            if (map.get("return_code").equalsIgnoreCase("SUCCESS")) {

                String attach = map.get("attach");//{username:zhangsan,type:1}
                Map<String,String> attachMap = JSON.parseObject(attach, Map.class);
                //2.判断支付成功失败  如果成功 ：1.删除预订单 2.将数据存储到mysql中 3.清除状态 重复排队相关的redis 的key
                if (map.get("result_code").equalsIgnoreCase("SUCCESS")) {
                    //21.先获取到redis中的预订单
                    SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.boundHashOps(SystemConstants.SEC_KILL_ORDER_KEY).get(attachMap.get("username"));
                    if(seckillOrder!=null) {
                        //22.设置相关的值
                        seckillOrder.setStatus("1");//已经支付
                        String time_end = map.get("time_end");
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
                        try {
                            Date payTime = simpleDateFormat.parse(time_end);
                            seckillOrder.setPayTime(payTime);//支付时间
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        seckillOrder.setTransactionId(map.get("transaction_id"));
                        //23.添加到数据库中
                        seckillOrderMapper.insertSelective(seckillOrder);
                    }

                    //删除预订单
                    redisTemplate.boundHashOps(SystemConstants.SEC_KILL_ORDER_KEY).delete(attachMap.get("username"));
                    redisTemplate.boundHashOps(SystemConstants.SEC_KILL_QUEUE_REPEAT_KEY).delete(attachMap.get("username"));
                    redisTemplate.boundHashOps(SystemConstants.SEC_KILL_USER_STATUS_KEY).delete(attachMap.get("username"));
                } else {
                    //3.如果失败   关闭微信支付 恢复库存 删除预订单  清除状态
                    //3.1 todo 关闭微信订单 写模拟浏览器发送请求 关闭微信订单的方法
                    //3.2 获取商品 数据 进行库存+1
                    SeckillStatus seckillStatus = (SeckillStatus) redisTemplate.boundHashOps(SystemConstants.SEC_KILL_USER_STATUS_KEY).get(attachMap.get("username"));
                    //先获取锁   上锁   释放锁
                    RLock myLock = redissonClient.getLock("MyLock");
                    try {
                        myLock.lock(20, TimeUnit.SECONDS);
                        if (seckillStatus != null) {
                            SeckillGoods seckillGoods = (SeckillGoods) redisTemplate.boundHashOps(SystemConstants.SEC_KILL_GOODS_PREFIX + seckillStatus.getTime()).get(seckillStatus.getGoodsId());
                            if (seckillGoods == null) {
                                seckillGoods = seckillGoodsMapper.selectByPrimaryKey(seckillStatus.getGoodsId());//库存为0
                                //将数据库的库存设置1 //todo
                            }
                            seckillGoods.setStockCount(seckillGoods.getStockCount() + 1);
                            redisTemplate.boundHashOps(SystemConstants.SEC_KILL_GOODS_PREFIX + seckillStatus.getTime()).put(seckillStatus.getGoodsId(), seckillGoods);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }finally {
                        myLock.unlock();
                    }
                    //删除状态的信息
                    redisTemplate.boundHashOps(SystemConstants.SEC_KILL_ORDER_KEY).delete(attachMap.get("username"));
                    redisTemplate.boundHashOps(SystemConstants.SEC_KILL_QUEUE_REPEAT_KEY).delete(attachMap.get("username"));
                    redisTemplate.boundHashOps(SystemConstants.SEC_KILL_USER_STATUS_KEY).delete(attachMap.get("username"));
                }

            } else {
                System.out.println("没有通信成功");
            }

        }
    }
}
