package org.nutz.dao.test.entity;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Table;

@Table(value = "test", prefix = "t_", suffix = "nut")
public class TO7 {
    @Id
    @Column(prefix = "c_", suffix = "_int")
    private int id;

    @Name
    @Column(prefix = "c_", suffix = "_str", hump = true)
    private String to7Name;

    @Column(value = "to7_age", prefix = "c_", suffix = "_int")
    private int age;

    @Column
    private String addr;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTo7Name() {
        return to7Name;
    }

    public void setTo7Name(String to7Name) {
        this.to7Name = to7Name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }
}
