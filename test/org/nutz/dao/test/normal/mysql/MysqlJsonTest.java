package org.nutz.dao.test.normal.mysql;

import static org.junit.Assert.*;

import java.math.BigDecimal;

import org.junit.Test;
import org.nutz.dao.test.DaoCase;
import org.nutz.lang.util.NutMap;

public class MysqlJsonTest extends DaoCase {

    @Override
    protected void before() {
        if (!dao.meta().isMySql())
            return;
        dao.create(StudentJson.class, true);
    }

    @Test
    public void crud() {
        if (!dao.meta().isMySql())
            return;
        NutMap data = NutMap.NEW().setv("name", "Alpha").setv("age", 20).setv("addr", "Beijing");
        StudentResult alphaResult = new StudentResult();
        alphaResult.setMathematics("A");
        alphaResult.setPhysics(new BigDecimal("100"));
        alphaResult.setForeignLanguage(120);
        StudentJson student = new StudentJson();
        student.setData(data);
        student.setStudentResult(alphaResult);

        int insertId = dao.insert(student).getId();
        StudentJson insertStudent = dao.fetch(StudentJson.class, insertId);
        assertEquals(data, insertStudent.getData());
        assertTrue(alphaResult.equals(insertStudent.getStudentResult()));

        insertStudent.getData().put("addr", "Dalian");
        insertStudent.getStudentResult().setMathematics("S");
        dao.updateIgnoreNull(insertStudent);
        StudentJson updateStudent = dao.fetch(StudentJson.class, insertId);
        assertEquals("Dalian", updateStudent.getData().get("addr"));
        assertEquals("S", updateStudent.getStudentResult().getMathematics());

        dao.delete(StudentJson.class, insertId);
        assertNull(dao.fetch(StudentJson.class, insertId));
    }
}
