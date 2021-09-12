package com.ihrm.system.controller;

import com.ihrm.common.controller.BaseController;
import com.ihrm.common.entity.Result;
import com.ihrm.common.entity.ResultCode;
import com.ihrm.domain.system.Role;
import com.ihrm.system.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

/**
 * 角色管理
 * @author LPJ
 */
@RestController
@RequestMapping("/sys")
public class RoleController extends BaseController {

    @Autowired
    private RoleService roleService;

    //分配权限
    @PutMapping("/role/assignPrem")
    public Result assignPrem(@RequestBody Map<String, Object>map){
        //1.获取被分配的角色id
        String roleId=(String) map.get("id");
        //2.获取权限的id列表
        List<String> permIds=(List<String>)map.get("permIds");
        //3.给所选角色分配权限
        roleService.assignPerms(roleId,permIds);
        return new Result(ResultCode.SUCCESS);

    }
    //添加角色
    @PostMapping("/role")
    public Result save(@RequestBody Role role){
        String companyId="1";
        role.setCompanyId(companyId);
        roleService.save(role);
        return Result.SUCCESS();
    }

    //更新角色
    @PostMapping("/role/{id}")
    public Result update(@PathVariable(name = "id") String id, @RequestBody Role role){
        roleService.update(role);
        return Result.SUCCESS();
    }
    //删除角色
    @DeleteMapping("/role/{id}")
    public Result delete(@PathVariable(name = "id") String id) throws Exception {
        roleService.delete(id);
        return Result.SUCCESS();
    }
    /**
     * 根据ID获取角色信息
     */
    @GetMapping(value = "/role/{id}")
    public Result findById(@PathVariable(name = "id") String id) throws Exception {
        Role role = roleService.findById(id);
     return new Result(ResultCode.SUCCESS,role);
    }



}