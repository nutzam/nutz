package org.nutz.trans;

import org.nutz.dao.entity.annotation.*;

@Table("trans_master")
public class Master {

    @Column
    @Id
    private int id;

    @Column
    @Name
    private String name;

    @Column
    private int comId;

    private Company com;

    public Company getCom() {
        return com;
    }

    public void setCom(Company com) {
        this.com = com;
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

    public int getComId() {
        return comId;
    }

    public void setComId(int comId) {
        this.comId = comId;
    }

    public static Master create(String name, Company com) {
        Master c = new Master();
        c.setName(name);
        c.setCom(com);
        return c;
    }

}
