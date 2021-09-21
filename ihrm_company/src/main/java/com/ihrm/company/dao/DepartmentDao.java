package com.ihrm.company.dao;

import com.ihrm.domain.company.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * 部门管理
 * @author LPJ
 */
public interface DepartmentDao extends JpaRepository<Department,String> , JpaSpecificationExecutor<Department> {

    Department findByCode(String code, String companyId);
}
