package org.nutz.dao.test.normal;

import org.nutz.dao.entity.annotation.*;

@Table("t_plant")
public class Plant {

    @Name
    private String name;

    @Column("num")
    private int number;

    @Column("clr")
    @Readonly
    private String color;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

}
