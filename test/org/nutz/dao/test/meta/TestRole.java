package org.nutz.dao.test.meta;

import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 角色表
 *
 * @author haiming
 */
@Table("test_role")
public class TestRole extends BaseBean implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column
    @Name
    @Comment("ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @Prev(els = {@EL("uuid()")})
    private String id;

    /**
     * 角色名称
     */
    @Column("role_name")
    @Comment("角色名称 ")
    private String roleName;

    /**
     * 角色权限
     */
    @Column("role_key")
    @Comment("角色权限")
    private String roleKey;

    /**
     * 角色排序
     */
    @Column("role_sort")
    @Comment("角色排序")
    private String roleSort;

    /**
     * 数据范围（1：所有数据权限；2：自定义数据权限）
     */
    @Column("data_scope")
    @Comment("数据范围 ")
    private String dataScope;

    /**
     * 角色状态（0正常 1停用）
     */
    @Column("status")
    @Comment("角色状态（0正常 1停用） ")
    private boolean status;

    /**
     * 删除标志（0代表存在 1代表删除）
     */
    @Column("del_flag")
    @Comment("删除标记")
    @ColDefine(type = ColType.BOOLEAN)
    private boolean delFlag;

    /**
     * 用户是否存在此角色标识 默认不存在
     */
    private boolean flag = false;

    @Column
    @Comment("备注")
    private String remark;

    /**
     * 菜单组
     */
    private String menuIds;

    /**
     * 部门组（数据权限）
     */
    private String[] deptIds;

    @ManyMany(from = "role_id", relation = "test_role_menu", to = "menu_id")
    protected List<TestMenu> menus;


    /**
     * 创建时间
     */
    @Column("create_time")
    @Comment("创建时间 ")
    @Prev(els = {@EL("$me.now()")})
    private Date createTime;

    /**
     * 更新时间
     */
    @Column("update_time")
    @Comment("更新时间 ")
    @Prev(els = {@EL("$me.now()")})
    private Date updateTime;

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TestRole) {
            TestRole role = (TestRole) obj;
//            System.out.println("equal"+ role.id);
            return (id.equals(role.id));
        }
        return super.equals(obj);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleKey() {
        return roleKey;
    }

    public void setRoleKey(String roleKey) {
        this.roleKey = roleKey;
    }

    public String getRoleSort() {
        return roleSort;
    }

    public void setRoleSort(String roleSort) {
        this.roleSort = roleSort;
    }

    public String getDataScope() {
        return dataScope;
    }

    public void setDataScope(String dataScope) {
        this.dataScope = dataScope;
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

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getMenuIds() {
        return menuIds;
    }

    public void setMenuIds(String menuIds) {
        this.menuIds = menuIds;
    }

    public String[] getDeptIds() {
        return deptIds;
    }

    public void setDeptIds(String[] deptIds) {
        this.deptIds = deptIds;
    }

    public List<TestMenu> getMenus() {
        return menus;
    }

    public void setMenus(List<TestMenu> menus) {
        this.menus = menus;
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
        return "TestRole{" +
                "id='" + id + '\'' +
                ", roleName='" + roleName + '\'' +
                ", roleKey='" + roleKey + '\'' +
                ", roleSort='" + roleSort + '\'' +
                ", dataScope='" + dataScope + '\'' +
                ", status=" + status +
                ", delFlag=" + delFlag +
                ", flag=" + flag +
                ", remark='" + remark + '\'' +
                ", menuIds='" + menuIds + '\'' +
                ", deptIds=" + Arrays.toString(deptIds) +
                ", menus=" + menus +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
