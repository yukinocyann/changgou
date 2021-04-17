package com.changgou.filter;

import com.changgou.util.JwtUtil;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author ljh
 * @version 1.0
 * @date 2020/10/7 16:08
 * @description 标题
 * @package com.changgou.filter
 */
@Component
public class AuthorizeFilter implements GlobalFilter, Ordered {
    private static final String AUTHORIZE_TOKEN = "Authorization";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //执行校验的逻辑

        //1.获取request对象
        ServerHttpRequest request = exchange.getRequest();
        //2.获取response对象
        ServerHttpResponse response = exchange.getResponse();
        //3.先判断 请求的路径是否需要校验（/api/user/login）如果是登录，，，，，， 放行
        String path = request.getURI().getPath();// /api/user/login
        if (path.startsWith("/api/user/login")) {
            //放行
            return chain.filter(exchange);
        }

        //4.先从请求参数中获取token 判断是否为空  如果为空
        String token = request.getQueryParams().getFirst(AUTHORIZE_TOKEN);
        if (StringUtils.isEmpty(token)) {
            //5.再从请求的header中获取token 判断是否为空 如果为空
            token = request.getHeaders().getFirst(AUTHORIZE_TOKEN);
        }
        if (StringUtils.isEmpty(token)) {
            //6.再从cookie中获取token 判断是否为空 如果为空   直接返回 401 （）
            HttpCookie cookie = request.getCookies().getFirst(AUTHORIZE_TOKEN);
            if (cookie != null) {
                token = cookie.getValue();//token
            }
        }
        if (StringUtils.isEmpty(token)) {
            //todo  要去登录
            //1.设置要重定向到的路径
            response.getHeaders().set("Location","http://localhost:9001/oauth/login?url="+request.getURI().toString());
            //2.设置重定向的状态
            response.setStatusCode(HttpStatus.SEE_OTHER);
            //response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }
        //7.校验 令牌是否正确  如果不正确 直接返回
        /*try {
            JwtUtil.parseJWT(token);
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }*/
        //8.校验正确 放行
        //手动将令牌的信息传递给下游微服务--》将cookie中的token 存储到header中传递给下游
        request.mutate().header(AUTHORIZE_TOKEN,"Bearer "+token);
        return chain.filter(exchange);
    }

    //值越小 优先级越高 优先执行
    @Override
    public int getOrder() {
        return 0;
    }
}
