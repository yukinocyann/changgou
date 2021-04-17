package com.changgou.order.service;

import com.changgou.order.pojo.OrderItem;

import java.util.List;

/**
 * @author ljh
 * @version 1.0
 * @date 2020/10/10 15:01
 * @description 标题
 * @package com.changgou.order.service
 */
public interface CartService {
    /**
     * 添加购物车
     * @param num
     * @param id
     * @param username
     */
    void add(Integer num, Long id, String username);

    List<OrderItem> list(String username);

}
