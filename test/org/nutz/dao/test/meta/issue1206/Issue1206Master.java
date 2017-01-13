package org.nutz.dao.test.meta.issue1206;

import java.util.List;

import org.nutz.dao.DB;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Many;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Next;
import org.nutz.dao.entity.annotation.SQL;
import org.nutz.dao.entity.annotation.Table;

@Table("t_issue1206_master")
public class Issue1206Master {

    @Id
    @Next({@SQL(db = DB.PSQL, value = "SELECT currval('$table$_id_seq')")})
    private int id;

    @Name
    private String name;

    @Many
    private List<Issue1206Pet> pets;

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

    public List<Issue1206Pet> getPets() {
        return pets;
    }

    public void setPets(List<Issue1206Pet> pets) {
        this.pets = pets;
    }
    
    
}
