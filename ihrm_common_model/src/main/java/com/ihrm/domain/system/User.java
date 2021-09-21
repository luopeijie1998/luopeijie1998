package com.ihrm.domain.system;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * 用户实体类
 */
@Entity
@Table(name = "bs_user")
@Getter
@Setter
public class User implements Serializable {
    private static final long serialVersionUID = 4297464181093070302L;
    /**
     * ID
     */
    @Id
    private String id;
    /**
     * 手机号码
     */
    private String mobile;
    /**
     * 用户名称
     */
    private String username;
    /**
     * 密码
     */
    private String password;

    /**
     * 启用状态 0为禁用 1为启用
     */
    private Integer enableState;
    /**
     * 创建时间
     */
    private Date createTime;

    private String companyId;

    private String companyName;

    /**
     * 部门ID
     */
    private String departmentId;

    /**
     * 入职时间
     */
    private Date timeOfEntry;

    /**
     * 聘用形式
     */
    private Integer formOfEmployment;

    /**
     * 工号
     */
    private String workNumber;

    /**
     * 管理形式
     */
    private String formOfManagement;

    /**
     * 工作城市
     */
    private String workingCity;

    /**
     * 转正时间
     */
    private Date correctionTime;

    /**
     * 在职状态 1.在职  2.离职
     */
    private Integer inServiceStatus;

    private String departmentName;

    /**
     * 层级
     */
    private  String Level;


    //多对多注解
    @ManyToMany
    /**
     * JsonIgnore:忽略json转化
     */
    @JsonIgnore
    @JoinTable(name="pe_user_role",joinColumns={@JoinColumn(name="user_id",referencedColumnName="id")},
            inverseJoinColumns={@JoinColumn(name="role_id",referencedColumnName="id")}
    )
    private Set<Role> roles = new HashSet<>();//用户与角色   多对多
    //根据解析出的excel列数据，进行user初始构造，objs数据位置和excel上传位置一致。
    public User(Object[] objs, String companyId, String companyName) {
        //默认手机号excel读取为字符串会存在科学记数法问题，转化处理
        this.mobile = new DecimalFormat("#").format(objs[2]);
        this.username = objs[1].toString();
        this.createTime = new Date();
        this.timeOfEntry = (Date) objs[5];
        this.formOfEmployment = ((Double) objs[4]).intValue() ;
        this.workNumber = new DecimalFormat("#").format(objs[3]).toString();
        this.companyId = companyId;
        this.companyName = companyName;
    }
}
