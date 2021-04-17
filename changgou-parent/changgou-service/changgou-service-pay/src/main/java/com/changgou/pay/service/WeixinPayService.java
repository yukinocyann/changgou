package com.changgou.pay.service;

import java.util.Map;

/**
 * @author ljh
 * @version 1.0
 * @date 2020/10/13 10:31
 * @description 标题
 * @package com.changgou.pay.service
 */
public interface WeixinPayService {

    //
    // Map<String, String> createNative(String out_trade_no, String total_fee);
    Map<String, String> createNative(Map<String, String> parameter);

    Map<String, String> queryPayStatus(String out_trade_no);
}
