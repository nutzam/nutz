package org.nutz.dao.test.meta;

import java.io.InputStream;
import java.io.Reader;
import java.sql.Clob;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

@Table("bin_object")
public class BinObject {

    @Id
    private long id;

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    @Column
    private InputStream xblob;

    @Column
    private Reader xclob;

    public InputStream getXblob() {
        return xblob;
    }

    public void setXblob(InputStream xblob) {
        this.xblob = xblob;
    }

    public Reader getXclob() {
        return xclob;
    }

    public void setXclob(Reader xclob) {
        this.xclob = xclob;
    }

    @Column
    private Clob myClob;

    public Clob getMyClob() {
        return myClob;
    }

    public void setMyClob(Clob myClob) {
        this.myClob = myClob;
    }
}
