package com.changgou.order.consumer;

import com.alibaba.fastjson.JSON;
import com.changgou.order.dao.OrderMapper;
import com.changgou.order.pojo.Order;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * @author ljh
 * @version 1.0
 * @date 2020/10/13 16:13
 * @description 标题
 * @package com.changgou.order.consumer
 */
@Component
@RabbitListener(queues = "queue.order")//监听队列 指定队列的名称
public class PayOrderUpdateListener {

    @Autowired
    private OrderMapper orderMapper;

    //接收消息的数据
    @RabbitHandler
    public void jieshouMessage(String msg) {
        //1.转换成MAP
        Map<String, String> resultMap = JSON.parseObject(msg, Map.class);
        if (resultMap != null) {
            if (resultMap.get("return_code").equalsIgnoreCase("SUCCESS")) {
                //2.判断成功与否 如果成功则更新订单的状态
                if (resultMap.get("result_code").equalsIgnoreCase("SUCCESS")) {
                    //2.1 先根据订单号查询订单数据
                    Order order = orderMapper.selectByPrimaryKey(resultMap.get("out_trade_no"));
                    if(order!=null) {
                        //2.2 设置订单的值
                        order.setPayStatus("1");//已经支付
                        String time_end = resultMap.get("time_end");
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
                        Date parse = null;
                        try {
                            parse = dateFormat.parse(time_end);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        order.setPayTime(parse);//付款时间
                        order.setTransactionId(resultMap.get("transaction_id"));
                        //2.3 更新订单的状态
                        orderMapper.updateByPrimaryKeySelective(order);
                    }
                } else {
                    //3.判断成功与否 如果失败,关闭原来的支付订单
                    //todo  关闭原来的支付订单
                    //todo  恢复库存
                    //todo  恢复积分
                    Order order = orderMapper.selectByPrimaryKey(resultMap.get("out_trade_no"));
                    order.setIsDelete("1");//已经删除
                    orderMapper.updateByPrimaryKeySelective(order);
                }

            }

        }
    }
}
