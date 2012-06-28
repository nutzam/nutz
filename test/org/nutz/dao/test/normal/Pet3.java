package org.nutz.dao.test.normal;

import org.nutz.dao.DB;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.EL;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Next;
import org.nutz.dao.entity.annotation.Prev;
import org.nutz.dao.entity.annotation.SQL;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.lang.random.R;

@Table("t_pet")
public class Pet3 {

    public String createName() {
        return "N_" + R.sg(4).next();
    }

    @Id
    @Next({@SQL(db = DB.PSQL, value = "SELECT currval('$table$_id_seq')")})
    private int id;

    @Name
    @Prev(els = @EL("$me.createName()"))
    private String name;

    @Column("alias")
    private String nickName;

    @Column
    private int age;

    @Column("mas")
    private int masterId;

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

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getMasterId() {
        return masterId;
    }

    public void setMasterId(int masterId) {
        this.masterId = masterId;
    }

}
