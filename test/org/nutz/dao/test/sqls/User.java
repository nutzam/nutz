package org.nutz.dao.test.sqls;

import org.nutz.dao.entity.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Table("usr")
public class User {
    @Name
    @ColDefine(width = 32)
    private String OID = "";

    @Column
    @ColDefine(width = 64)
    private String name = "";

    @Column
    @ColDefine(type = ColType.INT)
    private int flags = 3;

    @ManyMany(target = Role.class, relation = "rel_user_role", from = "uoid", to = "roid")
    private List<Role> roles = new ArrayList<Role>();

    public String getOID() {
        return OID;
    }

    public void setOID(String OID) {
        this.OID = OID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getFlags() {
        return flags;
    }

    public void setFlags(int flags) {
        this.flags = flags;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }
}
