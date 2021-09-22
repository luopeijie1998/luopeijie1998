package com.ihrm.system.service;

import com.ihrm.common.utils.IdWorker;
import com.ihrm.domain.company.Department;
import com.ihrm.domain.system.Role;
import com.ihrm.domain.system.User;
import com.ihrm.system.client.DepartmentFeignClient;
import com.ihrm.system.dao.RoleDao;
import com.ihrm.system.dao.UserDao;
import com.sun.org.apache.xml.internal.security.utils.Base64;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.shiro.crypto.hash.Md2Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.io.IOException;
import java.util.*;

/**
 * 用户管理
 * @author LPJ
 */
@Service
public class UserService {

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private UserDao userDao;
    @Autowired
    private RoleDao roleDao;

    @Autowired
    private DepartmentFeignClient departmentFeignClient;

    /**
     * 添加用户
     */
    public void save(User user) {
        //填充其他参数
        user.setId(idWorker.nextId() + "");
        user.setCreateTime(new Date()); //创建时间
        String password =new Md2Hash("123456",user.getMobile(),3).toString();
        user.setLevel("user");
        user.setPassword(password);//设置默认登录密码
        user.setEnableState(1);//状态
        userDao.save(user);
    }
    /**
     * 更新用户
     */
    public void update(User user) {
        User targer = userDao.getOne(user.getId());
        targer.setPassword(user.getPassword());
        targer.setUsername(user.getUsername());
        targer.setMobile(user.getMobile());
        targer.setDepartmentId(user.getDepartmentId());
        targer.setDepartmentName(user.getDepartmentName());
        userDao.save(targer);
    }
    /**
     * 删除部门
     *
     * @param id 部门ID
     */
    public void delete(String id) {
        userDao.deleteById(id);
    }
    /**
     * 根据ID查询用户
     */
    public User findById(String id) {
        return userDao.findById(id).get();
    }

    public Page<User> findSearch(Map<String,Object> map, int page, int size) {
        return userDao.findAll(createSpecification(map), PageRequest.of(page-1, size));
    }
    /**
     * 调整部门
     */
    public void changeDept(String deptId,String deptName,List<String> ids) {
        for (String id : ids) {
            User user = userDao.findById(id).get();
            user.setDepartmentName(deptName);
            user.setDepartmentId(deptId);
            userDao.save(user);
        }
    }
    /**
     * 分配角色
     */
    public void assignRoles(String userId,List<String> roleIds) {
        User user = userDao.findById(userId).get();
        Set<Role> roles = new HashSet<>();
        for (String id : roleIds) {
            Role role = roleDao.findById(id).get();
            roles.add(role);
        }
        //设置用户和角色之间的关系
        user.setRoles(roles);
        userDao.save(user);
    }


    /**
     * 动态条件构建
     * @param searchMap
     * @return
     */
    private Specification<User> createSpecification(Map searchMap) {
        return new Specification<User>() {
            @Override
            public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query,
                                         CriteriaBuilder cb) {
                List<Predicate> predicateList = new ArrayList<Predicate>();
                // ID
                if (searchMap.get("id") != null && !"".equals(searchMap.get("id"))) {
                    predicateList.add(cb.equal(root.get("id").as(String.class),
                            (String) searchMap.get("id")));
                }
                // 手机号码
                if (searchMap.get("mobile") != null && !"".equals(searchMap.get("mobile"))) {
                    predicateList.add(cb.equal(root.get("mobile").as(String.class),
                            (String) searchMap.get("mobile")));
                }
                // 用户ID
                if (searchMap.get("departmentId") != null && !"".equals(searchMap.get("departmentId"))) {
                }
                predicateList.add(cb.like(root.get("departmentId").as(String.class),
                        (String) searchMap.get("departmentId")));
                // 标题
                if (searchMap.get("formOfEmployment") != null && !"".equals(searchMap.get("formOfEmployment"))) {

                    predicateList.add(cb.like(root.get("formOfEmployment").as(String.class),
                            (String) searchMap.get("formOfEmployment")));
                }
                if (searchMap.get("companyId") != null && !"".equals(searchMap.get("companyId"))) {
                    predicateList.add(cb.like(root.get("companyId").as(String.class),
                            (String) searchMap.get("companyId")));
                }
                if (searchMap.get("hasDept") != null && !"".equals(searchMap.get("hasDept"))) {
                    if ("0".equals((String) searchMap.get("hasDept"))) {
                        predicateList.add(cb.isNull(root.get("departmentId")));
                    } else {
                        predicateList.add(cb.isNotNull(root.get("departmentId")));
                    }
                }
                return cb.and(predicateList.toArray(new
                        Predicate[predicateList.size()]));
            }
        };
    }


    public User findByMobile(String mobile) {
        return userDao.findByMobile(mobile);

    }

    public void save(List<User> users, String companyId, String companyName) throws Exception {
       for (User user:users){
          //配置密码
          user.setPassword(new Md2Hash("123456",user.getMobile(),3).toString());
           //配置id
           user.setId(idWorker.nextId()+"");
           //其他基本属性
           user.setCompanyId(companyId);
           user.setCompanyName(companyName);
           user.setInServiceStatus(1);
           user.setEnableState(1);
           user.setLevel("user");
           //获取部门信息
           Department dept =
                   departmentFeignClient.findById(user.getDepartmentId(),user.getCompanyId());
           if(dept != null) {
               user.setDepartmentId(dept.getId());
               user.setDepartmentName(dept.getName());
           }
           userDao.save(user);
       }
    }

    /**
     * 处理EXCEL数据对应类型的获取
     * @param cell
     * @return
     */
    public Object getValue(Cell cell) {
        Object value = null;
        switch (cell.getCellType()){
            case STRING: //字符串类型
                value = cell.getStringCellValue();
                break;
            case BOOLEAN: //boolean类型
                value = cell.getBooleanCellValue();
                break;
            case NUMERIC: //数字类型（包含日期和普通数字）
                if(DateUtil.isCellDateFormatted(cell)) {
                    value = cell.getDateCellValue();
                }else{
                    value = cell.getNumericCellValue();
                }
                break;
            case FORMULA: //公式类型
                value = cell.getCellFormula();
                break;
            default:
                break;
        }
        return value;
    }

    /**
     * Data URL方式存储图像
     * @param id
     * @param file
     * @return
     */
    public String uploadImage(String id, MultipartFile file) throws Exception {
        //根据id查询用户
        User user = userDao.findById(id).get();
        //对上传文件进行Base64编码
        String s = Base64.encode(file.getBytes());
        //拼接DataURL数据头
        String dataUrl = new String("data:image/jpg;base64,"+s);
        //保存图片信息
        userDao.save(user);
        return dataUrl;
    }
}
