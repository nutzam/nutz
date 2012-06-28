package org.nutz.json.generic;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Table;

/**
* 
*/
@Table("tb_employee")
public class Employee {

    /**
     * 员工分类，0:普通员工
     */
    public static final int TYPE_NOMAL = 0;

    /**
     * 员工分类，1:技师
     */
    public static final int TYPE_TECHNICIAN = 1;

    /**
     * 机构id
     */
    @Column("branch_id")
    private String branchId;

    private String mobile;

    /**
     * 员工编号
     */
    @Column("employee_no")
    private String employeeNo;

    /**
     * 职位
     */
    @Column("position_id")
    private String positionId;

    /**
     * 职称，关联字典表id
     */
    @Column("title")
    private String title;

    /**
     * 员工分类，0:普通员工,1:技师
     */
    @Column("type")
    private Integer type;

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    public String getEmployeeNo() {
        return employeeNo;
    }

    public void setEmployeeNo(String employeeNo) {
        this.employeeNo = employeeNo;
    }

    public String getPositionId() {
        return positionId;
    }

    public void setPositionId(String positionId) {
        this.positionId = positionId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    // @One(target = EmployeeInfo.class, field = "id")
    // private EmployeeInfo employeeInfo;

}