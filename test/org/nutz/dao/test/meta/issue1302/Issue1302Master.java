package org.nutz.dao.test.meta.issue1302;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.ColType;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Table;

@Table("t_issue_1302_master")
public class Issue1302Master {

    @Id
    private int id;
    
    @Name
    private String name;
    
    @Column
    @ColDefine(type=ColType.INT)
    private Issue1302UserAction act;

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

    public Issue1302UserAction getAct() {
        return act;
    }

    public void setAct(Issue1302UserAction act) {
        this.act = act;
    }
    
    
}
