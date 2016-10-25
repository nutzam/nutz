package org.nutz.dao.test.meta.issue1155;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Table;

@Table("t_pet_master")
public class SuperPetMaster {

    @Name
    private String name;

    @Column
    @ColDefine(width=1024)
    private SuperPet pet;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SuperPet getPet() {
        return pet;
    }

    public void setPet(SuperPet pet) {
        this.pet = pet;
    }
}
