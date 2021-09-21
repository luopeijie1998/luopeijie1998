package com.ihrm.company.controller;

import com.ihrm.common.controller.BaseController;
import com.ihrm.common.entity.Result;
import com.ihrm.common.entity.ResultCode;
import com.ihrm.company.service.CompanyService;
import com.ihrm.company.service.DepartmentService;
import com.ihrm.domain.company.Company;
import com.ihrm.domain.company.Department;
import com.ihrm.domain.company.response.DeptListResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
/**
 * 部门管理
 * @author LPJ
 */
@CrossOrigin
@RestController
@RequestMapping("/commpany")
public class DepartmentController extends BaseController {

    @Autowired
    private CompanyService companyService;
    @Autowired
    private DepartmentService departmentService;
    /**
     * 保存
     */
    @RequestMapping(value = "department",method = RequestMethod.POST)
    public Result save(@RequestBody Department department)
    {
        //1.设置保存的企业id
        /**
         * 目前使用固定值 以后解决
         */
        department.setCompanyId(companyId);
        //2.调用service完成保存
        departmentService.save(department);
        //3.构造返回结果
        return new Result(ResultCode.SUCCESS);
    }
    /**
     * 查询全部列表
     */
    @RequestMapping(value="/department",method = RequestMethod.GET)
    public Result findAll(String companyid)
    {
        //1.指定企业id
        Company company=companyService.findById(companyid);
        //2.完成查询
        List<Department> list=departmentService.findAll(companyid);
        //3.构造返回结果
        DeptListResult deptListResult=new DeptListResult(company,list);
        return  new Result(ResultCode.SUCCESS,deptListResult);
    }
    /**
     * 根据ID查询department
     */
    @RequestMapping(value = "/department/{id}",method = RequestMethod.GET)
    public Result findById(@PathVariable(value="id")String id)
    {
        Department department=departmentService.findById(id);
        return new Result(ResultCode.SUCCESS,department);
    }
    /**
     * 修改Department
     */
    @RequestMapping(value = "/department/{id}",method = RequestMethod.PUT)
    public Result update(@PathVariable(value = "id")String id,@RequestBody Department department)
    {
        //1.设置修改的部门id
        department.setId(id);
        //2.调用service更新
        departmentService.update(department);
        return new Result(ResultCode.SUCCESS);
    }
    /**
     * 根据id删除
     */
    @RequestMapping(value = "/department/{id}",method = RequestMethod.DELETE)
    public Result delete(@PathVariable(value = "id")String id)
    {
        departmentService.deleteById(id);
        return new Result(ResultCode.SUCCESS);
    }
    @RequestMapping(value = "/company/departments/search/", method =
            RequestMethod.POST)
    public Department findById(@RequestParam(value = "code") String code,
                               @RequestParam(value = "companyId") String companyId)
            throws Exception{
        Department dept = departmentService.findByCode(code,companyId);
        return dept;
    }









}
