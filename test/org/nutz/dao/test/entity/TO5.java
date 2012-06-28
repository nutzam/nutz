package org.nutz.dao.test.entity;

import java.util.List;

import org.nutz.dao.entity.annotation.*;

public class TO5 {

    @Column("toid")
    @Id
    private int id;

    @Column("toname")
    private String name;

    @Column("to1")
    private String to1Name;

    @One(target = TO1.class, field = "to1Name")
    private TO0 to1;

    @Many(target = TO3.class, field = "tid")
    private TO3[] toMany;

    @ManyMany(target = TO2.class, relation = "t_t_t_3", from = "t5id", to = "t3id")
    private List<TO3> toManyMany;

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

    public String getTo1Name() {
        return to1Name;
    }

    public void setTo1Name(String to1Name) {
        this.to1Name = to1Name;
    }

    public TO0 getTo1() {
        return to1;
    }

    public void setTo1(TO0 to1) {
        this.to1 = to1;
    }

    public TO3[] getToMany() {
        return toMany;
    }

    public void setToMany(TO3[] toMany) {
        this.toMany = toMany;
    }

    public List<TO3> getToManyMany() {
        return toManyMany;
    }

    public void setToManyMany(List<TO3> toManyMany) {
        this.toManyMany = toManyMany;
    }

}