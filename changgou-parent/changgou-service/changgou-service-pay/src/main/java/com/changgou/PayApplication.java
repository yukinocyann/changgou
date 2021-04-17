package com.changgou;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

/**
 * @author ljh
 * @version 1.0
 * @date 2020/10/13 10:14
 * @description 标题
 * @package com.changgou
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@EnableEurekaClient
public class PayApplication {
    public static void main(String[] args) {
        SpringApplication.run(PayApplication.class,args);
    }

    @Autowired
    private Environment environment;
    //创建队列
    @Bean
    public Queue queueOrder(){
        return new Queue(environment.getProperty("mq.pay.queue.order"));
    }

    //创建交互机 路由模式的交换机
    @Bean
    public DirectExchange createExchange(){
        return new DirectExchange(environment.getProperty("mq.pay.exchange.order"));
    }

    //创建绑定
    @Bean
    public Binding createBinding(){
        return BindingBuilder.bind(queueOrder()).to(createExchange()).with(environment.getProperty("mq.pay.routing.key"));
    }
}
