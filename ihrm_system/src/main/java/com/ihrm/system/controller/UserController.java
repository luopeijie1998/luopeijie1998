package com.ihrm.system.controller;

import com.ihrm.common.controller.BaseController;
import com.ihrm.common.entity.PageResult;
import com.ihrm.common.entity.Result;
import com.ihrm.common.entity.ResultCode;
import com.ihrm.domain.system.User;
import com.ihrm.system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 用户管理
 * @author LPJ
 */
@CrossOrigin
@RestController
@RequestMapping("/sys")
public class UserController extends BaseController {

    @Autowired
    private UserService userService;


    @PostMapping("/user/assignRoles")
    public Result assignRoles(@RequestBody Map<String, Object> map){
        //1.获取被分配的用户id
        String userId=(String)map.get("id");
        //2.获取到角色的id列表
        List<String> roleIds=(List<String>) map.get("roleds");
        //3.调用service完成角色的分配
        userService.assignRoles(userId,roleIds);
        return Result.SUCCESS();

    }
    @PostMapping(value = "/user")
    public Result add(@RequestBody User user) throws Exception {
        userService.save(user);
        return Result.SUCCESS();
    }

    @PutMapping(value = "/user/{id}")
    public Result update(@RequestBody User user)
            throws Exception {
        userService.update(user);
        return Result.SUCCESS();
    }

    @DeleteMapping(value = "/user/{id}")
    public Result delete(@PathVariable(name = "id") String id) throws Exception {
        userService.delete(id);
        return Result.SUCCESS();
    }

    @GetMapping(value = "/user/{id}")
    public Result findById(@PathVariable(name = "id") String id) throws Exception {
        User user = userService.findById(id);
        return new Result(ResultCode.SUCCESS,user);
    }

    @GetMapping("/user")
    public Result findByPage(int page,int pagesize,@RequestParam Map<String,Object>
            map) throws Exception {
        Page<User> searchPage = userService.findSearch(map, page, pagesize);
        PageResult<User> pr = new
                PageResult(searchPage.getTotalElements(),searchPage.getContent());
        return new Result(ResultCode.SUCCESS,pr);
    }

}
