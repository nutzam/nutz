package org.nutz.dao.test.meta;

import org.nutz.dao.entity.annotation.*;

@Table("dao_d_gun_${id}")
public class Gun {

    public static Soldier assign(Soldier s, TYPE... types) {
        Gun[] guns = new Gun[types.length];
        for (int i = 0; i < types.length; i++) {
            guns[i] = new Gun();
            guns[i].type = types[i];
        }
        s.setGuns(guns);
        return s;
    }

    public static enum TYPE {
        AK47, M16, M60, MP5, AWP, UMP_45, XM1014, P228
    }

    @Column
    @Id
    private int id;

    @Column("sname")
    private String soldierName;

    @Column
    private TYPE type;

    @One(target = Soldier.class, field = "soldierName")
    private Soldier soldier;

    public Soldier getSoldier() {
        return soldier;
    }

    public void setSoldier(Soldier soldier) {
        this.soldier = soldier;
    }

    public String getSoldierName() {
        return soldierName;
    }

    public void setSoldierName(String soldierName) {
        this.soldierName = soldierName;
    }

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

}
