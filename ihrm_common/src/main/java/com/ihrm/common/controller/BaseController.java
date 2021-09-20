package com.ihrm.common.controller;


import com.ihrm.domain.system.response.ProfileResult;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BaseController {

    public  HttpServletRequest request;
    public  HttpServletResponse response;

    protected String companyId;
    protected String companyName;
    //使用shiro  获取公共数据
    //ModelAttribute 注解  在进入控制器之前执行的方法
    @ModelAttribute
    public void setResAnReq(HttpServletRequest request,HttpServletResponse response){
        this.request=request;
        this.response=response;
   //获取session中的安全数据
        Subject subject = SecurityUtils.getSubject();
        //1.subject获取所有的安全数据集合
        PrincipalCollection principals = subject.getPrincipals();
        if(principals != null && !principals.isEmpty()){
            //2.获取安全数据
            ProfileResult result = (ProfileResult)principals.getPrimaryPrincipal();
            this.companyId = result.getCompanyId();
            this.companyName = result.getCompany();
        }

    }
//  使用jwt获取公共数据 //ModelAttribute 注解  在进入控制器之前执行的方法
//    @ModelAttribute
//    public void setResAnReq(HttpServletRequest request,HttpServletResponse response){
//        this.request=request;
//        this.response=response;
//        /**
//         * 暂时使用 companyid 和companyname
//         */
//        this.companyId="1";
//        this.companyName="独立团";
//    }

}
