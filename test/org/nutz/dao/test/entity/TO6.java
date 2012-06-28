package org.nutz.dao.test.entity;

import org.nutz.dao.entity.annotation.*;

/**
 * For Issue #115
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
@Table("jax_pf_dict")
@PK({"type", "id"})
public class TO6 {

    @Column
    private String type = "7";

    @Column
    @Name
    private String id;

    @Column("name")
    private int value;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

}
