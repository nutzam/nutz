package org.nutz.dao.test.meta;

import java.sql.Timestamp;

import org.nutz.castor.Castors;
import org.nutz.dao.entity.annotation.*;

@Table("dao_d_mission_${id}")
public class Mission {

    public static Mission make(String name, String dday) {
        Mission m = new Mission();
        m.name = name;
        m.dDay = Castors.me().castTo(dday, Timestamp.class);
        return m;
    }

    @Column
    @Name
    private String name;

    @Column("dday")
    private Timestamp dDay;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Timestamp getDDay() {
        return dDay;
    }

    public void setDDay(Timestamp day) {
        dDay = day;
    }

}
