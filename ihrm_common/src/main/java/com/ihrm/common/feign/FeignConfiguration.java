package com.ihrm.common.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * FeignConfiguration 过滤器，获取到请求头信息，并在调用Feign时自动配置请求头信息
 */
@Configuration
public class FeignConfiguration {
    @Bean
    public RequestInterceptor requestInterceptor() {
        return new RequestInterceptor() {
            //获取所有浏览器发送的请求属性，请求头赋值到feign
            @Override
            public void apply(RequestTemplate template) {
                ServletRequestAttributes attributes = (ServletRequestAttributes)
                        RequestContextHolder
                                .getRequestAttributes();
                if (attributes != null) {
                    HttpServletRequest request = attributes.getRequest();
                    //获取所有的请求头属性
                    Enumeration<String> headerNames = request.getHeaderNames();
                    if (headerNames != null) {
                        while (headerNames.hasMoreElements()) {
                            String name = headerNames.nextElement();  //Authorization
                            String values = request.getHeader(name);  //Bearer xxxxx-xxxx-xxxx
                            template.header(name, values);
                        }
                    }
                }
            }
        };
    }
}
