package org.nutz.dao.test.meta;

import java.sql.Timestamp;

import org.nutz.dao.DB;
import org.nutz.dao.entity.annotation.*;
import org.nutz.lang.Strings;
import org.nutz.lang.random.R;
import org.nutz.lang.random.StringGenerator;

@Table("t_pet")
@TableIndexes({@Index(name = "t_pet_name_masterId", unique = true,
                       fields = {"name", "masterId"}),
               @Index(name = "t_pet_name_age", fields = {"name", "age"},
                       unique = false)})
public class Pet {

    private static StringGenerator sg = R.sg(5, 10);

    public static Pet create(String name) {
        Pet pet = new Pet();
        pet.setName(name);
        pet.setAge(R.random(3, 10));
        pet.setNickName(sg.next());
        pet.setBirthday(new Timestamp(System.currentTimeMillis()));
        return pet;
    }

    public static Pet[] create(int num) {
        Pet[] pets = new Pet[num];
        for (int i = 0; i < num; i++)
            pets[i] = create("pet_" + Strings.fillHex(i, 2));
        return pets;
    }

    @Id
    @Next({@SQL(db = DB.PSQL, value = "SELECT currval('$table$_id_seq')")})
    private int id;

    @Name
    private String name;

    @Column("alias")
    private String nickName;

    @Column
    private int age;

    @Column("mas")
    private int masterId;

    @Column
    private Timestamp birthday;

    @Column
    private float price;

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public Pet setId(int id) {
        this.id = id;
        return this;
    }

    public int getMasterId() {
        return masterId;
    }

    public void setMasterId(int masterId) {
        this.masterId = masterId;
    }

    public String getName() {
        return name;
    }

    public Pet setName(String name) {
        this.name = name;
        return this;
    }

    public String getNickName() {
        return nickName;
    }

    public Pet setNickName(String nickName) {
        this.nickName = nickName;
        return this;
    }

    public int getAge() {
        return age;
    }

    public Pet setAge(int age) {
        this.age = age;
        return this;
    }

    public Timestamp getBirthday() {
        return birthday;
    }

    public void setBirthday(Timestamp birthday) {
        this.birthday = birthday;
    }

    public String toString() {
        return name;
    }
}
