package com.changgou.order.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * 拦截器 feigin调用的时候 用来 拦截所有请求 获取请求对象中的token 信息，再封装传递给下游被调用方微服务
 *
 * @author ljh
 * @version 1.0
 * @date 2020/10/10 16:53
 * @description 标题
 * @package com.changgou.order.config
 */
@Component
public class MyFeignInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        //1.获取当前线程的请求对象request
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            HttpServletRequest request = requestAttributes.getRequest();
            //2.获取请求对象中所有的头和头对应值 传递给下游
            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                String headerValue = request.getHeader(headerName);
                //3.添加到下游请求头中 传递下游微服务
                template.header(headerName, headerValue);
            }
        }
    }
}
