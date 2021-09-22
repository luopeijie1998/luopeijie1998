package com.ihrm.system.controller;

import com.ihrm.common.controller.BaseController;
import com.ihrm.common.entity.PageResult;
import com.ihrm.common.entity.Result;
import com.ihrm.common.entity.ResultCode;
import com.ihrm.common.exception.CommonException;
import com.ihrm.common.utils.JwtUtil;
import com.ihrm.domain.system.User;

import com.ihrm.domain.system.response.ProfileResult;
import com.ihrm.system.client.DepartmentFeignClient;
import com.ihrm.system.service.UserService;
import io.jsonwebtoken.Claims;
import org.apache.poi.ss.usermodel.*;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
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
    private DepartmentFeignClient departmentFeignClient;
    Claims claims;


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

    @RequiresPermissions(value="API-USER-DELETE")
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
        try {
            //1.构造登录令牌
            //加密密码
            password = new Md5Hash(password,mobile,3).toString();  //1.密码，盐，加密次数
            UsernamePasswordToken upToken = new UsernamePasswordToken(mobile,password);
            //2.获取subject
            Subject subject = SecurityUtils.getSubject();
            //3.调用login方法，进入realm完成认证
            subject.login(upToken);
            //4.返回sessionid
            String sessionId = (String)subject.getSession().getId();
            //5.构造返回结果
            return new Result(ResultCode.SUCCESS,sessionId);
        }catch (Exception e) {
            return new Result(ResultCode.MOBILEORPASSWORDERROR);
        }

//        User user = userService.findByMobile(mobile);
//        //登录失败
//        if (user == null || !user.getPassword().equals(password)) {
//            return new Result(ResultCode.MOBILEORPASSWORDERROR);
//        } else {
//            //登录成功
//            Map<String, Object> map = new HashMap<>();
//            map.put("companyId", user.getCompanyId());
//            map.put("companyName", user.getCompanyName());
//            String token = jwtUtils.createJWT(user.getId(), user.getUsername(), map);
//            return new Result(ResultCode.SUCCESS, token);
//        }
    }
    /**
     * 获取个人信息
     *       1.获取用户id
     *       2.根据用户id查询用户
     *       3.构建返回值对象
     *       4.响应
     */
    @PostMapping(path="/profile" ,name="api-userupdate")
    public Result profile(HttpServletRequest request) throws CommonException {

        //获取session中的安全数据
        Subject subject=SecurityUtils.getSubject();
        //1.subject获取所有的安全数据集合
        PrincipalCollection principals = subject.getPrincipals();
        //2.获取安全数据
        ProfileResult result=(ProfileResult) principals.getPrimaryPrincipal();
        return new Result(ResultCode.SUCCESS,result);

//        claims = (Claims) request.getAttribute("user_claims");
//        if(claims == null) {
//            throw new CommonException(ResultCode.UNAUTHENTICATED);
//        }
//        String userId = claims.getId();
//        User user = userService.findById(userId);
//        return new Result(ResultCode.SUCCESS,new ProfileResult(user));
    }
    //测试通过系统微服务调用企业微服务方法
    @RequestMapping(value = "/test/{id}")
    public void findDeptById(@PathVariable  String id){
        Result dept = departmentFeignClient.findById(id);
        System.out.println(dept.getData());
    }

    //通过POI导入EXCEL新增用户
    @RequestMapping(value="/user/import", method = RequestMethod.POST)
    public Result importExcel(@RequestParam(name = "file") MultipartFile attachment) throws Exception {
        //根据上传流信息创建工作簿
        Workbook workbook = WorkbookFactory.create(attachment.getInputStream());
        //获取第一个sheet
        Sheet sheet = workbook.getSheetAt(0);
        List<User> users = new ArrayList<>();

        //从第二行开始获取数据
        for (int rowNum = 1; rowNum <sheet.getLastRowNum(); rowNum++){
            Row row = sheet.getRow(rowNum);
            Object  objs[] = new Object[row.getLastCellNum()];
            //从第二列获取数据
            for(int cellNum = 1; cellNum < row.getLastCellNum();cellNum++) {
                Cell cell = row.getCell(cellNum);
                objs[cellNum] = userService.getValue(cell);
            }
            User user = new User(objs,companyId,companyName);
            user.setDepartmentId(objs[objs.length-1].toString());
            users.add(user);
        }
        //第一个参数：用户列表，第二个参数：部门编码
        userService.save(users,companyId,companyName);
        return Result.SUCCESS();

    }
    //Data URL 头像上传
    @RequestMapping(value = "user/upload/{id}")
    public Result upload(@PathVariable String id,@RequestParam(name = "file") MultipartFile file)
            throws Exception{
        String image = userService.uploadImage(id, file);
        return new Result(ResultCode.SUCCESS,image);
    }


}
