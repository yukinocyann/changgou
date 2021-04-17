package com.changgou.seckill.service;

import com.changgou.core.service.CoreService;
import com.changgou.seckill.pojo.SeckillOrder;
import com.changgou.seckill.status.SeckillStatus;

/****
 * @Author:admin
 * @Description:SeckillOrder业务层接口
 * @Date 2019/6/14 0:16
 *****/
public interface SeckillOrderService extends CoreService<SeckillOrder> {

    Boolean add(Long id, String time, String username);

    SeckillStatus queryStatus(String username);

}
