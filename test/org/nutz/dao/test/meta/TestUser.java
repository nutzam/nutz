package org.nutz.dao.test.meta;

import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 用户信息
 * @author haiming
 */
@Table("test_user")
public class TestUser extends BaseBean implements Serializable {
    private static final long serialVersionUID = 1L;

    @Name
    @Column
    @Comment("ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String id;

    /**
     * 部门ID
     */
    @Column("dept_id")
    @Comment("部门ID")
    private String deptId;

    /**
     * 部门父ID
     */
    private String parentId;

    /**
     * 登录名称
     */
    @Column("login_name")
    @Comment("登录名称")
    private String loginName;

    /**
     * 用户名称
     */
    @Column("user_name")
    @Comment("用户名称")
    private String userName;

    /**
     * 密码
     */
    @Column("password")
    @Comment("密码")
    private String password;

    /**
     * 盐加密
     */
    @Column("salt")
    @Comment("盐加密")
    private String salt;

    /**
     * 用户邮箱
     */
    @Column("email")
    @Comment("用户邮箱")
    private String email;

    /**
     * 手机号码
     */
    @Column("phonenumber")
    @Comment("手机号码")
    private String phonenumber;

    /**
     * 用户性别
     */
    @Column
    @Comment("用户性别")
    private String gender;

    /**
     * 用户头像
     */
    @Column
    @Comment("用户头像")
    private String avatar;

    @One(field = "avatar")
    private TestImage image;

    /**
     * 帐号状态（0正常 1停用）
     */
    @Column
    @Comment("帐号状态（0正常 1停用） ")
    private boolean status;

    /**
     * 删除标志（0代表存在 1代表删除）
     */
    @Column("del_flag")
    @Comment("删除标记")
    @ColDefine(type = ColType.BOOLEAN)
    private boolean delFlag;

    /**
     * 最后登陆IP
     */
    @Column("login_ip")
    @Comment("最后登陆IP")
    private String loginIp;

    /**
     * 最后登陆时间
     */
    @Column("login_date")
    @Comment("最后登陆时间")
    private Date loginDate;

    /**
     * 角色集合
     */
    @ManyMany(from = "user_id", relation = "test_user_role", to = "role_id")
    private List<TestRole> roles;

    /**
     * 角色组
     */
    private String roleIds;

    /**
     * 岗位组
     */
    private String postIds;


    /**
     * 创建时间
     */
    @Column("create_time")
    @Comment("创建时间 ")
    private Date createTime;



    /**
     * 更新时间
     */
    @Column("update_time")
    @Comment("更新时间 ")
    private Date updateTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public TestImage getImage() {
        return image;
    }

    public void setImage(TestImage image) {
        this.image = image;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public boolean isDelFlag() {
        return delFlag;
    }

    public void setDelFlag(boolean delFlag) {
        this.delFlag = delFlag;
    }

    public String getLoginIp() {
        return loginIp;
    }

    public void setLoginIp(String loginIp) {
        this.loginIp = loginIp;
    }

    public Date getLoginDate() {
        return loginDate;
    }

    public void setLoginDate(Date loginDate) {
        this.loginDate = loginDate;
    }

    public List<TestRole> getRoles() {
        return roles;
    }

    public void setRoles(List<TestRole> roles) {
        this.roles = roles;
    }

    public String getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(String roleIds) {
        this.roleIds = roleIds;
    }

    public String getPostIds() {
        return postIds;
    }

    public void setPostIds(String postIds) {
        this.postIds = postIds;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }


    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "TestUser{" +
                "id='" + id + '\'' +
                ", deptId='" + deptId + '\'' +
                ", parentId='" + parentId + '\'' +
                ", loginName='" + loginName + '\'' +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", salt='" + salt + '\'' +
                ", email='" + email + '\'' +
                ", phonenumber='" + phonenumber + '\'' +
                ", gender='" + gender + '\'' +
                ", avatar='" + avatar + '\'' +
                ", image=" + image +
                ", status=" + status +
                ", delFlag=" + delFlag +
                ", loginIp='" + loginIp + '\'' +
                ", loginDate=" + loginDate +
                ", roles=" + roles +
                ", roleIds='" + roleIds + '\'' +
                ", postIds='" + postIds + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
