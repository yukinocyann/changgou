package com.changgou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author ljh
 * @version 1.0
 * @date 2020/10/7 08:58
 * @description 标题
 * @package com.changgou
 */
@SpringBootApplication
@EnableEurekaClient
public class GatewayWebApplication {
    public static void main(String[] args) {
        SpringApplication.run(GatewayWebApplication.class,args);
    }

    @Bean(name="ipKeyResolver")// <bean id="keyResolver"
    public KeyResolver keyResolver(){
        return new KeyResolver() {
            @Override
            public Mono<String> resolve(ServerWebExchange exchange) {
                //以ip地址的方式作为keyresolver
                String ip = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
                System.out.println("ip是多少："+ip);
                return Mono.just(ip);
            }
        };
    }
}
