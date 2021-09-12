package com.ihrm.system.service;

import com.ihrm.common.utils.IdWorker;
import com.ihrm.common.utils.PermissionConstants;
import com.ihrm.domain.system.Permission;
import com.ihrm.domain.system.Role;
import com.ihrm.system.dao.PermissionDao;
import com.ihrm.system.dao.RoleDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.*;

/**
 * 角色管理
 * @author LPJ
 */
@Service
public class RoleService {
    @Autowired
    private IdWorker idWorker;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private PermissionDao permissionDao;
    /*
    添加角色
     */
    public void save(Role role)
    {
        //填充其他参数
        role.setId(idWorker.nextId() + "");
        roleDao.save(role);

    }
    /**
     * 更新角色
     */
    public void update(Role role)
    {
        Role targer=roleDao.getOne(role.getId());
        targer.setDescription(role.getDescription());
        targer.setName(role.getName());
        roleDao.save(targer);


    }
    /**
     *删除角色
     */
    public void delete(String id)
    {
        roleDao.deleteById(id);

    }
    /**
     * 根据公司companyid分页查询角色
     */
    public Page<Role> findSearch(String companyId,int page, int size)
    {
        Specification<Role> specification = new Specification<Role>() {
            @Override
            public Predicate toPredicate(Root<Role> root, CriteriaQuery<?> criteriaQuery,
                                         CriteriaBuilder criteriaBuilder) {

                   return criteriaBuilder.equal(root.get("companyId").as(String.class), companyId);
                }

        };
        return roleDao.findAll(specification,PageRequest.of(page-1,size));
    }
    /**
     * 根据id查询角色
     */
    public Role findById(String id)
    {
        return roleDao.findById(id).get();
    }

    /**
     * 给角色分配权限
     * @param roleId
     * @param permIds
     */
    public void assignPerms(String roleId, List<String> permIds) {
        //1.查询出角色
        Role role= roleDao.findById(roleId).get();
        //2.构造角色的权限集合
        Set<Permission> permsSet=new HashSet<>();
        for (String permId :permIds){
            Permission permission=permissionDao.findById(permId).get();
            List<Permission> apiList= permissionDao.findByTypeAndPid(PermissionConstants.PERMISSION_API,permission.getPid());
            permsSet.addAll(apiList);//自定赋予API权限
            permsSet.add(permission);//当前菜单或按钮的权限

        }
        //3.设置角色和权限的关系
        role.setPermissions(permsSet);
        //4.更新角色
        roleDao.save(role);
    }
}
