package org.nutz.dao.test.meta.nutzcn;

import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.One;
import org.nutz.dao.entity.annotation.Table;

@Table("t_abc_user")
public class AbcUser {
    @Id
    private long id;
    
    @Name
    private String name;
    
    private int age;

    @One(key="userId", field="id")
    private AbcPet pet;

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public AbcPet getPet() {
        return pet;
    }

    public void setPet(AbcPet pet) {
        this.pet = pet;
    }

    
}