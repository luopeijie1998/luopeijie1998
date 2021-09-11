package com.ihrm.company.service;

import com.ihrm.common.service.BaseService;
import com.ihrm.common.utils.IdWorker;
import com.ihrm.company.dao.CompanyDao;
import com.ihrm.domain.company.Company;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 公司管理
 * @author LPJ
 */
import java.sql.Date;
import java.util.List;

@Service
public class CompanyService extends BaseService {

    @Autowired
    private CompanyDao companyDao;
    @Autowired
    private IdWorker idWorker;
    /**
     * 添加企业
     *
     * @param company 企业信息
     */
    public Company add(Company company) {
        company.setId(idWorker.nextId() + "");
        company.setCreateTime(new Date(2017));
        company.setState(1);    //启用
        company.setAuditState("0"); //待审核
        company.setBalance(0d);
        return companyDao.save(company);
    }
    public Company update(Company company) {
        return companyDao.save(company);
    }
    public Company findById(String id) {
        return companyDao.findById(id).get();
    }
    public void deleteById(String id) {
        companyDao.deleteById(id);
    }
    public List<Company> findAll() {
        return companyDao.findAll();
    }

}
