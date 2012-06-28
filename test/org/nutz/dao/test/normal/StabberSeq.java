package org.nutz.dao.test.normal;

import org.nutz.dao.entity.annotation.*;

@Table("t_stabber_seq")
public class StabberSeq {

    @Column
    @Id
    private int id;

    @Column("v")
    private int value;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

}
