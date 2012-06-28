package org.nutz.dao.test.smoke;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Table;

@Table("${name}")
public class DPet {

    @Id
    private int id;

    @Name
    private String name;

    @Column("alias")
    private String nickName;

    @Column
    private int age;

    public int getId() {
        return id;
    }

    public DPet setId(int id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public DPet setName(String name) {
        this.name = name;
        return this;
    }

    public String getNickName() {
        return nickName;
    }

    public DPet setNickName(String nickName) {
        this.nickName = nickName;
        return this;
    }

    public int getAge() {
        return age;
    }

    public DPet setAge(int age) {
        this.age = age;
        return this;
    }

    public String toString() {
        return name;
    }
}
