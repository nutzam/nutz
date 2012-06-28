package org.nutz.dao.test.normal;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import org.junit.Test;

import org.nutz.castor.Castors;
import org.nutz.castor.FailToCastObjectException;
import org.nutz.dao.Chain;
import org.nutz.dao.entity.annotation.*;
import org.nutz.dao.test.DaoCase;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.meta.Email;

public class SupportedFieldTypeTest extends DaoCase {

    @Override
    protected void before() {
        dao.create(EntityTypes.class, true);
    }

    public static enum TT {
        A, B
    }

    @Table("dao_supported_type")
    public static class EntityTypes {
        @Column
        @Id
        public int id;

        @Column
        @Name
        public String name;

        @Column
        @Default("zozoh@gmail.com")
        public Email email;

        @Column
        public TT enum_s;

        // It will auto detect db, and use INT
        @Column
        public TT enum_i;

        @Column
        public boolean bool_p;

        @Column
        public Boolean bool_obj;

        @Column
        public char char_p;

        @Column
        public Character char_obj;

        @Column
        public Date sqlDate;

        @Column
        public Time sqlTime;

        @Column
        public Timestamp sqlDT;

        @Column
        public int int_p;

        @Column
        public Integer int_obj;

        @Column
        public float float_p;

        @Column
        public Float float_obj;

        @Column
        public short short_p;

        @Column
        public Short short_obj;

        @Column
        public byte byte_p;

        @Column
        public Byte byte_obj;

        @Column
        public long long_p;

        @Column
        public Long long_obj;

        @Column
        public double double_p;

        @Column
        public Double double_obj;

    }

    @Test
    public void insert_char_field() {
        dao.insert(EntityTypes.class, Chain    .make("char_p", 't')
                                            .add("char_obj", Character.valueOf('O'))
                                            .add("name", "ABC"));
        EntityTypes et = dao.fetch(EntityTypes.class);
        assertEquals('t', et.char_p);
        assertEquals('O', et.char_obj.charValue());
    }

    @Test
    public void insert_timestamp_field() {
        Timestamp tm = new Timestamp(System.currentTimeMillis());
        dao.insert(EntityTypes.class, Chain.make("name", "ABC").add("sqlDT", tm));
        EntityTypes et = dao.fetch(EntityTypes.class);
        if (dao.meta().isPostgresql())
            assertEquals(tm.getTime(), et.sqlDT.getTime());
        else
            assertEquals(tm.getTime() / 1000, et.sqlDT.getTime() / 1000);
    }

    @Test
    public void check_for_sqlTime() {
        Time time = Castors.me().castTo("07:09:12", Time.class);
        dao.insert(EntityTypes.class, Chain.make("name", "ABC").add("sqlTime", time));
        EntityTypes et = dao.fetch(EntityTypes.class);
        assertEquals(time.toString(), et.sqlTime.toString());
    }

    @Test
    public void check_update_sqlTimestamp() {
        EntityTypes exp = new EntityTypes();
        exp.name = "T";
        Timestamp tm = new Timestamp(System.currentTimeMillis());
        exp.sqlDT = tm;
        dao.insert(exp);
        exp = dao.fetch(EntityTypes.class, "T");
        // MySql TIMESTAMP precision only to second
        assertEquals(tm.getTime() / 1000, exp.sqlDT.getTime() / 1000);
    }

    @Test
    public void check_if_support_all_normal_types() throws FailToCastObjectException {
        String d = "2009-02-01";
        String t = "12:23:23";
        String dt = d + " " + t;
        Date date = Castors.me().castTo(d, Date.class);
        Time time = Castors.me().castTo(t, Time.class);
        Timestamp ts = Castors.me().castTo(dt, Timestamp.class);
        EntityTypes exp = new EntityTypes();
        exp.name = "XX";
        exp.enum_s = TT.B;
        exp.enum_i = TT.A;
        exp.char_p = 'G';
        exp.char_obj = 'O';
        exp.int_p = 23;
        exp.int_obj = 23;
        exp.float_p = 34.67f;
        exp.float_obj = 34.68f;
        exp.short_p = 6;
        exp.short_obj = 6;
        exp.byte_p = 2;
        exp.byte_obj = 4;
        exp.long_p = 56787;
        exp.long_obj = 5678L;
        exp.double_p = 2.4325243;
        exp.double_obj = 3.4325243;
        exp.sqlDate = date;
        exp.sqlTime = time;
        exp.sqlDT = ts;
        dao.insert(exp);
        EntityTypes et = dao.fetch(EntityTypes.class);
        assertEquals(exp.id, et.id);
        Mirror<EntityTypes> me = Mirror.me(EntityTypes.class);
        for (Field f : me.getFields()) {
            Object expValue;
            Object ttValue;
            // Mysql 5.0.18， 会去掉毫秒数
            if (f.getName().equals("sqlTime") &&  
                    (dao.meta().isMySql() || dao.meta().isHsql())) {
                expValue = me.getValue(exp, f).toString();
                ttValue = me.getValue(et, f).toString();
            }
            // 其他的数据库无所谓
            else {
                expValue = me.getValue(exp, f);
                ttValue = me.getValue(et, f);
                if (null == expValue)
                    continue;
            }
            if (!expValue.equals(ttValue) && !dao.meta().isDB2()) //DB2的精度有点问题
                throw Lang.makeThrow(    "'%s' expect [%s] but it was [%s]",
                                        f.getName(),
                                        expValue,
                                        ttValue);
        }
        assertTrue(true);
    }

    @Test
    public void check_insert_null_timestamp_field() {
        EntityTypes exp = new EntityTypes();
        exp.name = "JJ";
        dao.insert(exp);
        assertTrue(true);
    }

}
