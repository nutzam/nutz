package org.nutz.dao.test.normal.psql;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.nutz.dao.test.DaoCase;
import org.nutz.lang.Lang;

public class PsqlArrayTest extends DaoCase {
    @Override
    protected void before() {
        if (!dao.meta().isPostgresql())
            return;
        dao.create(StudentArray.class, true);
    }

    @Test
    public void crud() {
        if (!dao.meta().isPostgresql())
            return;
        StudentArray student = new StudentArray();
        Integer[] payByQuarter = {1000, 1300, 1500, 1200};
        String[] schedule = new String[]{"02", "05", "08", "11"};
        student.setPayByQuarter(payByQuarter);
        student.setSchedule(schedule);

        int insertId = dao.insert(student).getId();
        StudentArray insertStudent = dao.fetch(StudentArray.class, insertId);
        assertTrue(Lang.equals(payByQuarter, insertStudent.getPayByQuarter()));
        assertTrue(Lang.equals(schedule, insertStudent.getSchedule()));

        payByQuarter[2] = 2500;
        schedule[2] = "09";
        insertStudent.setPayByQuarter(payByQuarter);
        insertStudent.setSchedule(schedule);
        dao.updateIgnoreNull(insertStudent);
        StudentArray updateStudent = dao.fetch(StudentArray.class, insertId);
        assertEquals(Integer.valueOf(2500), updateStudent.getPayByQuarter()[2]);
        assertEquals("09", updateStudent.getSchedule()[2]);

        dao.delete(StudentJson.class, insertId);
        assertNull(dao.fetch(StudentJson.class, insertId));
    }
}
