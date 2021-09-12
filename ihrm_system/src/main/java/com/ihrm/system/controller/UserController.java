package com.ihrm.system.controller;

import com.ihrm.common.controller.BaseController;
import com.ihrm.common.entity.PageResult;
import com.ihrm.common.entity.Result;
import com.ihrm.common.entity.ResultCode;
import com.ihrm.common.exception.CommonException;
import com.ihrm.common.utils.JwtUtil;
import com.ihrm.domain.company.response.ProfileResult;
import com.ihrm.domain.system.User;
import com.ihrm.system.service.UserService;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
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
    @Autowired
    private JwtUtil jwtUtils;


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

    @PostMapping("/login")
    public Result login(@RequestBody Map<String,String> loginMap) {
        String mobile = loginMap.get("mobile");
        String password = loginMap.get("password");
        User user = userService.findByMobile(mobile);
        //登录失败
        if (user == null || !user.getPassword().equals(password)) {
            return new Result(ResultCode.MOBILEORPASSWORDERROR);
        } else {
            //登录成功
            Map<String, Object> map = new HashMap<>();
            map.put("companyId", user.getCompanyId());
            map.put("companyName", user.getCompanyName());
            String token = jwtUtils.createJWT(user.getId(), user.getUsername(), map);
            return new Result(ResultCode.SUCCESS, token);
        }
    }
    /**
     * 获取个人信息
     */
    @PostMapping("/profile")
    public Result profile(HttpServletRequest request) throws CommonException {
        //请求中获取key为Authorization的头信息
        String authorization = request.getHeader("Authorization");
        if(StringUtils.isEmpty(authorization)) {
            throw new CommonException(ResultCode.UNAUTHENTICATED);
        }
        //前后端约定头信息内容以 Bearer+空格+token 形式组成
        String token = authorization.replace("Bearer ", "");
        //比较并获取claims
        Claims claims = jwtUtils.parseJWT(token);
        if(claims == null) {
            throw new CommonException(ResultCode.UNAUTHENTICATED);
        }
        String userId = claims.getId();
        User user = userService.findById(userId);
        return new Result(ResultCode.SUCCESS,new ProfileResult(user));
    }
}
