package org.nutz.dao.test.normal.psql;

import org.nutz.dao.entity.annotation.Id;

public class Student {

    @Id
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
