package com.ihrm.system.dao;

import com.ihrm.domain.system.PermissionPoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
/**
 * 权限管理
 * @author LPJ
 */
public interface PermissionPointDao extends JpaRepository<PermissionPoint,String>,
        JpaSpecificationExecutor<PermissionPoint> {
}
