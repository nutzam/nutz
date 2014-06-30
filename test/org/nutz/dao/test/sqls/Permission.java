package org.nutz.dao.test.sqls;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Table;

@Table("u_permission")
public class Permission {
    @Name
    @ColDefine(width = 32)
    private String OID = "";

    @Column
    @ColDefine(width = 64)
    private String name = "";

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
