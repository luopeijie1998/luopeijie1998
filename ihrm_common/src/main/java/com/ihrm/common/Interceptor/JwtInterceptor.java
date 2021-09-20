package com.ihrm.common.Interceptor;

import com.ihrm.common.entity.ResultCode;
import com.ihrm.common.exception.CommonException;
import com.ihrm.common.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//@Component
public class JwtInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private JwtUtil jwtUtil;

   @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception{
       //1.通过request获取请求头信息
       String authorization =request.getHeader("Authorization");
       //2. 校验请求头信息解析token
       if (!StringUtils.isEmpty(authorization)&&authorization.startsWith("Bearer")) {
           //获取token数据
           String token = authorization.replace("Bearer", "");
           //解析token获取数据
           Claims clamis = jwtUtil.parseJWT(token);
           if (clamis == null) {
               throw new CommonException(ResultCode.UNAUTHENTICATED);
           }
           //3.获取权限
           //通过claims获取到当前用户的可访问API权限字符串
           String apis = (String) clamis.get("apis"); //api-user-delete,api-userupdate
           //4.获取请求路径(requestmapping)所需要的权限
           //通过handler
           HandlerMethod h = (HandlerMethod) handler;
           //获取接口上的reqeustmapping注解
           RequestMapping annotation = h.getMethodAnnotation(RequestMapping.class);
           //获取当前请求接口中的name属性
           String name = annotation.name();
           //判断当前用户是否具有响应的请求权限
           if (apis.contains(name)) {
               request.setAttribute("user_claims", clamis);
               return true;
           } else {
               throw new CommonException(ResultCode.UNAUTHORISE);
           }
       }

       throw new CommonException(ResultCode.UNAUTHENTICATED);

   }
}
