package org.nutz.dao.test.meta;

import org.nutz.dao.entity.annotation.*;

@Table("dao_d_soldier_${id}")
public class Soldier {

    public static Soldier make(String name) {
        Soldier s = new Soldier();
        s.name = name;
        return s;
    }

    @Column
    @Name
    private String name;

    @Column
    private int age;

    @Many(target = Gun.class, field = "soldierName")
    private Gun[] guns;

    @ManyMany(target = Tank.class, relation = "dao_d_m_soldier_tank_${id}", from = "sname", to = "tid")
    private Tank tank;

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Gun[] getGuns() {
        return guns;
    }

    public void setGuns(Gun[] guns) {
        this.guns = guns;
    }

    public Tank getTank() {
        return tank;
    }

    public void setTank(Tank tank) {
        this.tank = tank;
    }

    @Override
    public String toString() {
        return String.format("%s (%d)", name, age);
    }

}
