package com.ihrm.system.controller;

import com.ihrm.common.entity.Result;
import com.ihrm.common.entity.ResultCode;
import com.ihrm.domain.system.Permission;
import com.ihrm.system.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
/**
 * 权限管理
 * @author LPJ
 */
//1.解决跨域
@CrossOrigin
//2.声明restContoller
@RestController
//3.设置父路径
@RequestMapping(value="/sys")
public class PermissionController {
    @Autowired
    private PermissionService permissionService;
    /**
     * 保存
     */
    @PostMapping("/permission")
    public Result save(@RequestBody Map<String,Object> map) throws Exception {
        permissionService.save(map);
        return new Result(ResultCode.SUCCESS);
    }
    /**
     * 修改
     */
    @PutMapping("/permission/{id}")
    public Result update(@PathVariable(value = "id") String id, @RequestBody
            Map<String,Object> map) throws Exception {
        //构造id
        map.put("id",id);
        permissionService.update(map);
        return new Result(ResultCode.SUCCESS);

    }
    /**
     * 查询列表
     */
    @GetMapping("/permission")
    public Result findAll(@RequestParam Map map){
        List<Permission> list =  permissionService.findAll(map);
        return new Result(ResultCode.SUCCESS,list);
    }
    /**
     * 根据ID查询
     */
    @DeleteMapping("/permission/{id}")
    public Result delete(@PathVariable(value = "id") String id) throws Exception {
        permissionService.deleteById(id);
        return new Result(ResultCode.SUCCESS);
    }

}
