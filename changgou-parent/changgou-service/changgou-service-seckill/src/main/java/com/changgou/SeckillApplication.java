package com.changgou;

import entity.IdWorker;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import tk.mybatis.spring.annotation.MapperScan;

import java.net.UnknownHostException;

/**
 * @author ljh
 * @version 1.0
 * @date 2020/10/16 11:11
 * @description 标题
 * @package com.changgou
 */
@SpringBootApplication
@EnableEurekaClient
@EnableScheduling//开启定时任务 spring task
@MapperScan(basePackages = "com.changgou.*.dao")
//开启多线程注解的支持
@EnableAsync
public class SeckillApplication {
    public static void main(String[] args) {
        SpringApplication.run(SeckillApplication.class,args);
    }

    @Bean
    public RedisTemplate<Object, Object> redisTemplate(
            RedisConnectionFactory redisConnectionFactory) throws UnknownHostException {
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        //使用序列化机制为字符串 key的序列化机制
        template.setKeySerializer(new StringRedisSerializer());
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }
    @Bean
    public IdWorker idWorker(){
        return new IdWorker(0,3);
    }
    @Autowired
    private Environment environment;
    //创建队列  创建交换机 创建绑定
    //配置创建队列
    @Bean
    public Queue createSekillQueue(){
        // queue.order
        return new Queue(environment.getProperty("mq.pay.queue.seckillorder"));
    }

    //创建交换机

    @Bean
    public DirectExchange createSeckillExchange(){
        // exchange.order
        return new DirectExchange(environment.getProperty("mq.pay.exchange.seckillorder"));
    }

    // 绑定队列到交换机
    @Bean
    public Binding seckillbinding(){
        // routing key : queue.order
        String property = environment.getProperty("mq.pay.routing.seckillkey");
        return BindingBuilder.bind(createSekillQueue()).to(createSeckillExchange()).with(property);
    }
}
