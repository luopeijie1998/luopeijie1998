package com.ihrm.system.dao;

import com.ihrm.domain.system.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
/**
 * 权限管理
 * @author LPJ
 */
public interface PermissionDao extends JpaRepository<Permission,String>,
        JpaSpecificationExecutor<Permission> {
}
