package org.nutz.dao.test.entity;

import org.nutz.dao.entity.annotation.*;

public class TO3 {

    @Column
    private int tid;

    @Column
    @Id
    private int id;

    @Column
    @Name
    private String name;

    public int getTid() {
        return tid;
    }

    public void setTid(int tid) {
        this.tid = tid;
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

}
