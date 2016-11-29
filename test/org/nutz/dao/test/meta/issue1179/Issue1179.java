package org.nutz.dao.test.meta.issue1179;

import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Table;

@Table("t_issue_1179")
public class Issue1179 {

    @Id
    private int id;
    
    @Name
    private String name;
    
    private Issue1179Enum st;

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

    public Issue1179Enum getSt() {
        return st;
    }

    public void setSt(Issue1179Enum st) {
        this.st = st;
    }
}
