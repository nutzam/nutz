package org.nutz.dao.test.normal;

import java.sql.Timestamp;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.ColType;
import org.nutz.dao.entity.annotation.Default;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

@Table("t_ts")
public class TableWithTimestampInMySql {

    @Id
    private int id;

    // 可以为空,不设默认 -> 默认为null
    private Timestamp ts1;

    // 下面这个违反了MySql限制
    // 可以为空，设置默认 -> 自定义的默认值
    @Default("DEFAULT 0")
    private Timestamp ts2;

    // 不为空，不设默认 -> 默认为0000-00-00 00:00:00
    @ColDefine(notNull = true, type = ColType.TIMESTAMP)
    private Timestamp ts3;

    // 不为空，设置默认 -> ON UPDATE CURRENT_TIMESTAMP
    @ColDefine(notNull = true, type = ColType.TIMESTAMP)
    @Default("DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private Timestamp ts4;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Timestamp getTs1() {
        return ts1;
    }

    public void setTs1(Timestamp ts1) {
        this.ts1 = ts1;
    }

    public Timestamp getTs2() {
        return ts2;
    }

    public void setTs2(Timestamp ts2) {
        this.ts2 = ts2;
    }

    public Timestamp getTs3() {
        return ts3;
    }

    public void setTs3(Timestamp ts3) {
        this.ts3 = ts3;
    }

    public Timestamp getTs4() {
        return ts4;
    }

    public void setTs4(Timestamp ts4) {
        this.ts4 = ts4;
    }

}
