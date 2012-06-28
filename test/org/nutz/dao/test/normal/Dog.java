package org.nutz.dao.test.normal;

import java.sql.Timestamp;

import org.nutz.dao.entity.annotation.*;

@Table("t_dog")
@PK({"masterId", "id"})
public class Dog {

    @Column("mid")
    private int masterId;

    @Column
    private int id;

    @Column
    private String name;

    @Column
    private int age;

    @Column
    private Timestamp dead;

    public Timestamp getDead() {
        return dead;
    }

    public void setDead(Timestamp dead) {
        this.dead = dead;
    }

    public int getMasterId() {
        return masterId;
    }

    public void setMasterId(int masterId) {
        this.masterId = masterId;
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

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

}
