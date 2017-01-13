package org.nutz.dao.test.meta.issue1206;

import org.nutz.dao.DB;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Next;
import org.nutz.dao.entity.annotation.SQL;
import org.nutz.dao.entity.annotation.Table;

@Table("t_issue1206_pet")
public class Issue1206Pet {

    @Id
    @Next({@SQL(db = DB.PSQL, value = "SELECT currval('$table$_id_seq')")})
    private int id;

    @Name
    private String name;

    @Column("mas")
    private int issue1206MasterId;
    
    private Issue1206Master master;

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


    public Issue1206Master getMaster() {
        return master;
    }

    public void setMaster(Issue1206Master master) {
        this.master = master;
    }

    public int getIssue1206MasterId() {
        return issue1206MasterId;
    }

    public void setIssue1206MasterId(int issue1206MasterId) {
        this.issue1206MasterId = issue1206MasterId;
    }
    
    
}
