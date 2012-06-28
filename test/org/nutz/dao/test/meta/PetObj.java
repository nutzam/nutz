package org.nutz.dao.test.meta;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.lang.Strings;

@Table("t_pet")
public class PetObj {

    public static PetObj create(String name) {
        PetObj pet = new PetObj();
        pet.setName(name);
        return pet;
    }

    public static PetObj[] create(int num) {
        PetObj[] pets = new PetObj[num];
        for (int i = 0; i < num; i++)
            pets[i] = create("pet_" + Strings.fillHex(i, 2));
        return pets;
    }

    @Id
    private int id;

    @Name
    private String name;

    @Column("alias")
    private String nickName;

    @Column
    private Integer age;

    public int getId() {
        return id;
    }

    public PetObj setId(int id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public PetObj setName(String name) {
        this.name = name;
        return this;
    }

    public String getNickName() {
        return nickName;
    }

    public PetObj setNickName(String nickName) {
        this.nickName = nickName;
        return this;
    }

    public Integer getAge() {
        return age;
    }

    public PetObj setAge(Integer age) {
        this.age = age;
        return this;
    }

    public String toString() {
        return name;
    }

}
