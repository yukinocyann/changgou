package com.changgou.order.vo;

import com.changgou.order.pojo.Order;

import java.io.Serializable;

/**
 * @author ljh
 * @version 1.0
 * @date 2020/10/17 15:02
 * @description 标题
 * @package com.changgou.order.vo
 */
public class OrderVo  implements Serializable {
    private Integer type;
    private String orderId;
    private String totalFee;

    public OrderVo() {
    }

    public OrderVo(Integer type, String orderId, String totalFee) {
        this.type = type;
        this.orderId = orderId;
        this.totalFee = totalFee;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(String totalFee) {
        this.totalFee = totalFee;
    }
}
