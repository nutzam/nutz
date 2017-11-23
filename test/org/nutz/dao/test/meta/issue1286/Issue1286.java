package org.nutz.dao.test.meta.issue1286;

import java.sql.Timestamp;

import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.http.Request.METHOD;

@Table("t_issue_1286")
public class Issue1286 {

    @Id
    private int id;
    
    @Name
    private String name;
    
    private METHOD method;
    
    private Timestamp t;

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

    public METHOD getMethod() {
        return method;
    }

    public void setMethod(METHOD method) {
        this.method = method;
    }

    public Timestamp getT() {
        return t;
    }

    public void setT(Timestamp t) {
        this.t = t;
    }
}
