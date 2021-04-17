package com.changgou.pay.controller;

import com.alibaba.fastjson.JSON;
import com.changgou.pay.service.WeixinPayService;
import com.github.wxpay.sdk.WXPayUtil;
import entity.Result;
import entity.StatusCode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

/**
 * @author ljh
 * @version 1.0
 * @date 2020/10/13 10:22
 * @description 标题
 * @package com.changgou.pay.controller
 */
@RestController
@RequestMapping("/weixin/pay")
public class WeixinPayController {

    @Autowired
    private WeixinPayService weixinPayService;

    //生成支付二维码

    /**   /create/native?out_trade_no=1&total_fee=7
     * @param out_trade_no 订单号
     * @param total_fee    金额 金额单位 分
     * @return
     */
    @RequestMapping("/create/native")
    public Result<Map<String, String>> createNative(@RequestParam Map<String, String> parameter) {
        //todo 从令牌中获取用户的名称
        String username="zhangsan";
        parameter.put("username",username);
        Map<String, String> map = weixinPayService.createNative(parameter);
        return new Result<Map<String, String>>(true, StatusCode.OK, "创建二维码成功", map);
    }

    /**
     * 根据指定的订单号 查询该订单的支付的状态数据返回
     *
     * @param out_trade_no
     * @return
     */
    @GetMapping(value = "/status/query")
    public Result queryStatus(String out_trade_no) {
        Map<String, String> resultMap = weixinPayService.queryPayStatus(out_trade_no);
        return new Result(true, StatusCode.OK, "查询状态成功！", resultMap);
    }

    String returnSuccess = "<xml>\n" +
            "  <return_code><![CDATA[SUCCESS]]></return_code>\n" +
            "  <return_msg><![CDATA[OK]]></return_msg>\n" +
            "</xml>";


    @Autowired
    private RabbitTemplate rabbitTemplate;


    @Value("${mq.pay.exchange.order}")
    private String exchange;
    @Value("${mq.pay.queue.order}")
    private String queue;
    @Value("${mq.pay.routing.key}")
    private String routing;

    @Autowired
    private Environment environment;


    /**
     * 该路径用来 接收微信发送的通知数据 并返回给微信响应
     *
     * @return
     */
    @RequestMapping("/notify/url")
    public String notifyUrl(HttpServletRequest request) {
        ServletInputStream inputStream = null;
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {
            //1.接收数据流的形式 获取通知的结果
            inputStream = request.getInputStream();
            //2.   先将读流 变成 字节输出流  将数据写入输出流  转成字节数组 转成字符串  转MAP对象
            byteArrayOutputStream = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, len);
            }

            byte[] bytes = byteArrayOutputStream.toByteArray();
            String xml = new String(bytes, "utf-8");
            System.out.println(xml);
            Map<String, String> resultMap = WXPayUtil.xmlToMap(xml);
            System.out.println(resultMap);

            String jsonattachMap = resultMap.get("attach");//{username:zhangsan ,type:1}
            Map<String,String> attachMap = JSON.parseObject(jsonattachMap, Map.class);
            //如果是 秒杀  发送到秒杀 如果是普通 发送普通
            switch (Integer.parseInt(attachMap.get("type"))){
                case 1:{
                    //如果是1 标识 是普通的订单
                    rabbitTemplate.convertAndSend(exchange,routing, JSON.toJSONString(resultMap));
                    break;
                }
                case 2:{
                    //如果是2 标识 秒杀的订单
                    rabbitTemplate.convertAndSend(
                            environment.getProperty("mq.pay.exchange.seckillorder"),
                            environment.getProperty("mq.pay.routing.seckillkey"),
                            JSON.toJSONString(resultMap));
                    rabbitTemplate.convertAndSend(exchange,routing, JSON.toJSONString(resultMap));
                    break;
                }
                default:{
                    break;
                }
            }

            //3.响应给微信字符串
            return returnSuccess;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (byteArrayOutputStream != null) {
                    byteArrayOutputStream.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return returnSuccess;
    }


}
