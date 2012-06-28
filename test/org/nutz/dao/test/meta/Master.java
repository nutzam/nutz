package org.nutz.dao.test.meta;

import java.util.List;

import org.nutz.dao.DB;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Many;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Next;
import org.nutz.dao.entity.annotation.SQL;
import org.nutz.dao.entity.annotation.Table;

@Table("t_master")
public class Master {

    @Id
    @Next({@SQL(db = DB.PSQL, value = "SELECT currval('$table$_id_seq')")})
    private int id;

    @Name
    private String name;

    @Many(target = Pet.class, field = "masterId")
    private List<Pet> pets;

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

    public List<Pet> getPets() {
        return pets;
    }

    public void setPets(List<Pet> pets) {
        this.pets = pets;
    }

}
