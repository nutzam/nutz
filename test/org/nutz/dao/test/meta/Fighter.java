package org.nutz.dao.test.meta;

import org.nutz.dao.DB;
import org.nutz.dao.entity.annotation.*;

@Table("dao_fighter")
public class Fighter {

    public static Fighter make(TYPE type) {
        Fighter f = new Fighter();
        f.type = type;
        return f;
    }

    public static enum TYPE {
        F16, F22, F117A, F15, SU_31, SU_27, SU_35
    }

    @Column
    @Id
    @Next({    @SQL(db = DB.PSQL, value = "SELECT currval('dao_fighter_id_seq')"),
            @SQL(db = DB.OTHER, value = "SELECT MAX(id) AS id FROM dao_fighter")})
    private int id;

    @Column
    private TYPE type;

    @ManyMany(target = Base.class, relation = "dao_m_base_fighter", from = "fid", to = "bname")
    private Base base;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TYPE getType() {
        return type;
    }

    public void setType(TYPE type) {
        this.type = type;
    }

    public Base getBase() {
        return base;
    }

    public void setBase(Base base) {
        this.base = base;
    }

}
