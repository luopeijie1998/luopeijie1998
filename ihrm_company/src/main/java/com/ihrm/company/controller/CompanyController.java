package com.ihrm.company.controller;

import com.ihrm.common.controller.BaseController;
import com.ihrm.common.entity.Result;
import com.ihrm.common.entity.ResultCode;
import com.ihrm.company.service.CompanyService;
import com.ihrm.domain.company.Company;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
//解决跨域问题
@CrossOrigin
@RestController
@RequestMapping("/company")
public class CompanyController extends BaseController {

    @Autowired
    private CompanyService companyService;

    /**
     * 添加企业
     */
    @RequestMapping(value = "",method = RequestMethod.POST)
    public Result add(@RequestBody Company company)
    {
        companyService.add(company);
        return Result.SUCCESS();
    }
    /**
     * 根据id更新企业信息
     */
    @RequestMapping(value = "/{id}",method = RequestMethod.PUT)
    public Result update(@PathVariable("id") String id,@RequestBody Company company)
    {
        Company one=companyService.findById(id);
        one.setName(company.getName());
        one.setRemarks(company.getRemarks());
        one.setState(company.getState());
        one.setAuditState(company.getAuditState());
        companyService.update(company);
        return Result.SUCCESS();
    }

    /**
     * 获取企业列表
     */
    @RequestMapping(value = "",method = RequestMethod.GET)
    public Result findAll()
    {
        List<Company> companyList=companyService.findAll();
        return  new Result(ResultCode.SUCCESS);
    }

}
