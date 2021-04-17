package com.changgou.pay.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.pay.service.WeixinPayService;
import com.github.wxpay.sdk.WXPayUtil;
import entity.HttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ljh
 * @version 1.0
 * @date 2020/10/13 10:31
 * @description 标题
 * @package com.changgou.pay.service.impl
 */
@Service
public class WeixinPayServiceImpl implements WeixinPayService {

    @Value("${weixin.appid}")
    private String appid;//springboot @configruationproperties

    @Value("${weixin.partner}")
    private String partner;

    @Value("${weixin.partnerkey}")
    private String partnerkey;

    @Value("${weixin.notifyurl}")
    private String notifyurl;


    @Override
    public Map<String, String> createNative(Map<String,String> parameter) {
        try {
            //1.发送请求给微信支付系统
            //1.1发送统一下单API的请求
            //1.2组装参数 到map中
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("appid",appid);
            paramMap.put("mch_id",partner);
            paramMap.put("nonce_str", WXPayUtil.generateNonceStr());
            //添加签名  todo 自动的在map转成XML的时候自动实现。
            paramMap.put("body", "畅购的商品");
            paramMap.put("out_trade_no",parameter.get("out_trade_no"));
            paramMap.put("total_fee",parameter.get("total_fee"));
            paramMap.put("spbill_create_ip","127.0.0.1");
            //回调地址 下单的是给微信支付系统
            paramMap.put("notify_url",notifyurl);
            paramMap.put("trade_type","NATIVE");

            //传递attach: {username:zhangsan, type:1}
            Map<String,String> attachMap = new HashMap<>();
            attachMap.put("username",parameter.get("username"));
            attachMap.put("type",parameter.get("type"));

            paramMap.put("attach", JSON.toJSONString(attachMap));

            //1.3.将map转换成XML
            String xmlParam = WXPayUtil.generateSignedXml(paramMap, partnerkey);
            //1.4.执行发送请求的动作
            HttpClient client=new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            client.setHttps(true);//是否是https协议
            client.setXmlParam(xmlParam);//发送的xml数据
            client.post();//执行post请求
            String content = client.getContent(); //获取结果
            System.out.println(content);
            //2.获取微信支付系统返回的数据code_url
            Map<String, String> stringStringMap = WXPayUtil.xmlToMap(content);
            //3.订单号，金额，code_url 封装成map 返回
            Map<String,String> resultMap = new HashMap<String,String>();
            resultMap.put("code_url",stringStringMap.get("code_url"));
            resultMap.put("out_trade_no",parameter.get("out_trade_no"));
            resultMap.put("total_fee",parameter.get("total_fee"));
            return resultMap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new HashMap<>();
    }

    @Override
    public Map<String, String> queryPayStatus(String out_trade_no) {
        try {
            //1.发送请求给微信支付系统
            //1.1发送查询订单的API的请求
            //1.2组装参数 到map中
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("appid",appid);
            paramMap.put("mch_id",partner);
            paramMap.put("nonce_str", WXPayUtil.generateNonceStr());
            //添加签名  todo 自动的在map转成XML的时候自动实现。
            paramMap.put("out_trade_no",out_trade_no);

            //1.3.将map转换成XML
            String xmlParam = WXPayUtil.generateSignedXml(paramMap, partnerkey);
            //1.4.执行发送请求的动作
            HttpClient client=new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
            client.setHttps(true);//是否是https协议
            client.setXmlParam(xmlParam);//发送的xml数据
            client.post();//执行post请求
            String content = client.getContent(); //获取结果
            System.out.println(content);
            //2.获取微信支付系统返回的数据code_url
            Map<String, String> resultMap = WXPayUtil.xmlToMap(content);

            return resultMap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new HashMap<>();
    }
}
