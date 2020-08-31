package org.nutz.dao.test.meta;

import org.nutz.dao.entity.annotation.*;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.random.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Hamming_Yu on 2018/12/29.
 */
@Table("test_menu")
public class TestMenu extends BaseBean implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column
    @Name
    @Comment("ID")
    @ColDefine(type = ColType.VARCHAR, width = 64)
    @Prev(els = {@EL("uuid()")})
    private String id;

    @Column("menu_name")
    @Comment("菜单名称")
    @ColDefine(type = ColType.VARCHAR, width = 100)
    private String menuName;

    @Column("parent_id")
    @Comment("父菜单ID")
    @ColDefine(type = ColType.VARCHAR, width = 64)
    private String parentId;

    /**
     * 父菜单名称
     */
    private String parentName;

    @Column("order_num")
    @Comment("显示顺序")
    private String orderNum;

    @Column("url")
    @Comment("菜单URL")
    private String url;

    @Column("menu_type")
    @Comment("类型:0目录,1菜单,2按钮")
    private String menuType;

    @Column("visible")
    @Comment("菜单状态:0显示,1隐藏")
    private boolean visible;

    @Column("perms")
    @Comment("权限字符串")
    private String perms;

    @Column
    @Comment("菜单图标")
    private String icon;

    @Column
    @Comment("备注")
    private String remark;



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


    /** 子菜单 */
    private List<TestMenu> children = new ArrayList<TestMenu>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMenuType() {
        return menuType;
    }

    public void setMenuType(String menuType) {
        this.menuType = menuType;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public String getPerms() {
        return perms;
    }

    public void setPerms(String perms) {
        this.perms = perms;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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

    public List<TestMenu> getChildren() {
        return children;
    }

    public void setChildren(List<TestMenu> children) {
        this.children = children;
    }

    public static List<TestMenu>  getMenuList(List<TestMenu> list, String pid){
        List<TestMenu> allMenu =new ArrayList<TestMenu>();
        for(TestMenu menu:allMenu){
            menu.setId(R.UU32().toLowerCase());
            menu.setParentId(pid);
            allMenu.add(menu);
            if(Lang.isNotEmpty(menu.getChildren()) && menu.getChildren().size()>0){
                List<TestMenu> tmp = getMenuList(menu.getChildren(),menu.getId());
                allMenu.addAll(tmp);
            }
        }
        return allMenu;
    }

    /**
     * 子方法
     **/
    public static List<TestMenu> getChild(String id, List<TestMenu> allMenu) {
        // 子菜单
        List<TestMenu> childList = new ArrayList<TestMenu>();
        for (TestMenu menu : allMenu) {
            // 遍历所有节点，将父菜单id与传过来的id比较
            if (Strings.isNotBlank(menu.getParentId())) {
                if (menu.getParentId().equals(id)) {
                    childList.add(menu);
                }
            }
        }
        // 把子菜单的子菜单再循环一遍
        for (TestMenu menu : childList) {
            menu.setChildren(getChild(menu.getId(), allMenu));
        } // 递归退出条件
        if (childList.size() == 0) {
            return null;
        }
        return childList;
    }
}
