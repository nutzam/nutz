package org.nutz.dao.test.meta.issue803;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Table;

@Table("t_issue803")
public class Issue803 {

    public Issue803() {}
    
    public Issue803(String name) {
        this.name = name;
    }

    @Id
    private long id;
    @Name
    private String name;
    
    @ColDefine(width=3)
    private int p;

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

    public int getP() {
        return p;
    }

    public void setP(int p) {
        this.p = p;
    }
}
