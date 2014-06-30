package org.nutz.dao.test.sqls;

import org.nutz.dao.entity.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Table("u_role")
public class Role {
    @Name
    @ColDefine(width = 32)
    private String OID = "";

    @Column
    @ColDefine(width = 64)
    private String name = "";

    @ManyMany(target = Permission.class, relation = "rel_role_permission", from = "roid", to = "poid")
    private List<Permission> permissions = new ArrayList<Permission>();

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
}
