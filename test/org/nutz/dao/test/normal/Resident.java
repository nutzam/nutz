package org.nutz.dao.test.normal;

import org.nutz.dao.entity.annotation.*;

@Table("t_resident")
public class Resident {

    public Resident() {}

    public Resident(String name) {
        this.name = name;
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

}
