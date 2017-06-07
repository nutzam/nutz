package org.nutz.dao.test.meta.other;

import java.sql.Blob;
import java.sql.Clob;

import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

@Table("t_update_clob_blob")
public class UpdateClobBlobBean {

    @Id
    private int id;
    private Clob manytext;
    private Blob manybinary;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Clob getManytext() {
        return manytext;
    }

    public void setManytext(Clob manytext) {
        this.manytext = manytext;
    }

    public Blob getManybinary() {
        return manybinary;
    }

    public void setManybinary(Blob manybinary) {
        this.manybinary = manybinary;
    }
}
