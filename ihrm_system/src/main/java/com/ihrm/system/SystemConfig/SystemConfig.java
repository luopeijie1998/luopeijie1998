package com.ihrm.system.SystemConfig;

import com.ihrm.common.Interceptor.JwtInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
//配置拦截器
//@Configuration
public class SystemConfig extends WebMvcConfigurationSupport {
    @Autowired
    private JwtInterceptor jwtInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry){
        registry.addInterceptor(jwtInterceptor). //设置拦截器
                addPathPatterns("/**"). //设置拦截的路径
                excludePathPatterns("/sys/login","/frame/register/**"); //设置不拦截的请求地址
    }

}
