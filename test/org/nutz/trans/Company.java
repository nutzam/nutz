package org.nutz.trans;

import org.nutz.dao.entity.annotation.*;

@Table("trans_company")
public class Company {

    public static Company make(String name) {
        Company c = new Company();
        c.name = name;
        return c;
    }

    @Column
    @Id
    private int id;

    @Column
    @Name
    private String name;

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

    public static Company create(String name) {
        Company c = new Company();
        c.setName(name);
        return c;
    }

}
