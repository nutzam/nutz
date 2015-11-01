package org.nutz.dao.test.normal.psql;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.ColType;
import org.nutz.dao.entity.annotation.Column;

public class StudentArray extends Student {

    public StudentArray() {}

    public StudentArray(ResultSet rs) throws SQLException {
        this.setId(rs.getInt("id"));
        this.payByQuarter = (Integer[]) rs.getArray("pay_by_quarter").getArray();
        this.schedule = (String[]) rs.getArray("schedule").getArray();
    }

    @Column("pay_by_quarter")
    @ColDefine(customType = "integer[]", type = ColType.PSQL_ARRAY)
    private Integer[] payByQuarter;

    @Column("schedule")
    @ColDefine(customType = "varchar[]", type = ColType.PSQL_ARRAY)
    private String[] schedule;

    public Integer[] getPayByQuarter() {
        return payByQuarter;
    }

    public void setPayByQuarter(Integer[] payByQuarter) {
        this.payByQuarter = payByQuarter;
    }

    public String[] getSchedule() {
        return schedule;
    }

    public void setSchedule(String[] schedule) {
        this.schedule = schedule;
    }
}
