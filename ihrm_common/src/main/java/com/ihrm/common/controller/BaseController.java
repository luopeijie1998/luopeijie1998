package com.ihrm.common.controller;

import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BaseController {

    public  HttpServletRequest request;
    public  HttpServletResponse response;

    protected String companyId;
    protected String companyName;
    //ModelAttribute 注解  在进入控制器之前执行的方法
    @ModelAttribute
    public void setResAnReq(HttpServletRequest request,HttpServletResponse response){
        this.request=request;
        this.response=response;
        /**
         * 暂时使用 companyid 和companyname
         */
        this.companyId="1";
        this.companyName="独立团";
    }

}
