package org.nutz.trans;

import org.nutz.dao.entity.annotation.*;

@Table("trans_cat")
public class Cat {
    @Column
    @Id
    private int id;

    @Column
    @Name
    private String name;

    @Column
    private int masterId;

    private Master master;

    public Master getMaster() {
        return master;
    }

    public void setMaster(Master master) {
        this.master = master;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMasterId() {
        return masterId;
    }

    public void setMasterId(int masterId) {
        this.masterId = masterId;
    }

    public static Cat create(String name, Master m) {
        Cat c = create(name);
        c.setMaster(m);
        return c;
    }

    public static Cat create(String name) {
        Cat c = new Cat();
        c.setName(name);
        return c;
    }

}
