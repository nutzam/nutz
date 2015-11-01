package org.nutz.dao.test.meta;

import java.sql.Blob;
import java.sql.Clob;

import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Table;

@Table("t_use_blob_clob")
public class UseBlobClob {

    @Id
    private long id;
    
    @Name
    private String name;
    
    private Blob x;
    
    private Clob y;

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

    public Blob getX() {
        return x;
    }

    public void setX(Blob x) {
        this.x = x;
    }

    public Clob getY() {
        return y;
    }

    public void setY(Clob y) {
        this.y = y;
    }
}
