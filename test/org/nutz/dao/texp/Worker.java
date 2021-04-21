package org.nutz.dao.texp;

import org.nutz.dao.entity.annotation.*;

@Table("t_worker")
public class Worker {

    @Column("wid")
    @Id
    public int id;

    @Column("wname")
    @Name
    public String name;

    @Column("ct")
    public String city;

    @Column
    public short age;

    @Column("days")
    public int workingDay;

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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public short getAge() {
        return age;
    }

    public void setAge(short age) {
        this.age = age;
    }

    public int getWorkingDay() {
        return workingDay;
    }

    public void setWorkingDay(int workingDay) {
        this.workingDay = workingDay;
    }
}
