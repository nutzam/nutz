package org.nutz.dao.impl.sql;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.junit.Test;
import org.nutz.dao.test.meta.Pet;

public class SqlLiteralTest {

    private static NutSql L(String s) {
        return new NutSql(s);
    }

    @Test
    public void test_chinese_var_name() {
        NutSql sql = L("INSERT INTO t_chin(名称,描述) VALUES($名,$述)");
        assertEquals(2, sql.literal.getVarIndexes().size());
        Iterator<String> nms = sql.literal.getVarIndexes().names().iterator();
        assertEquals("名", nms.next());
        assertEquals("述", nms.next());

        String expect = "INSERT INTO t_chin(名称,描述) VALUES(,)";
        assertEquals(expect, sql.toPreparedStatement());
        sql.vars().set("名", "老张");
        sql.vars().set("述", "很棒");
        expect = "INSERT INTO t_chin(名称,描述) VALUES(老张,很棒)";
        assertEquals(expect, sql.toString());
    }

    @Test
    public void test_chinese_param_name() {
        NutSql sql = L("INSERT INTO t_chin(名称,描述) VALUES(@名,@述)");
        assertEquals(2, sql.literal.getParamIndexes().size());
        Iterator<String> nms = sql.literal.getParamIndexes().names().iterator();
        assertEquals("名", nms.next());
        assertEquals("述", nms.next());

        String expect = "INSERT INTO t_chin(名称,描述) VALUES(?,?)";
        assertEquals(expect, sql.toPreparedStatement());
        sql.params().set("名", "老张");
        sql.params().set("述", "很棒");
        expect = "INSERT INTO t_chin(名称,描述) VALUES('老张','很棒')";
        assertEquals(expect, sql.toString());
    }

    @Test
    public void test_name_with_underline() {
        NutSql sql = L("@a_1:$a_1");
        sql.params().set("a_1", "A");
        sql.vars().set("a_1", "B");
        assertEquals("'A':B", sql.toString());
        assertEquals("?:B", sql.toPreparedStatement());
    }

    @Test
    public void test_simple() {
        NutSql sql = L("A$a B@a C@b D$condition");
        sql.vars().set("a", "T");
        sql.params().set("a", 23);
        sql.params().set("b", false);
        assertEquals("AT B? C? D", sql.toPreparedStatement());
        assertEquals("AT B23 Cfalse D", sql.toString());
    }

    @Test
    public void test_holder_var_escaping() {
        NutSql sql = L("@@@@$$T$%$a@a;");
        sql.vars().set("a", "V");
        sql.params().set("a", "H");
        assertEquals("@@$T$%V?;", sql.toPreparedStatement());
        assertEquals("@@$T$%V'H';", sql.toString());
    }

    @Test
    public void test_sql_types() {
        assertTrue(L("InSeRT INTO $T ($id,$name) VALUES(@id,@name)").literal.isINSERT());
        assertTrue(L("UPDaTE $T SET $id=@id").literal.isUPDATE());
        assertTrue(L("sELECT * FROM $T").literal.isSELECT());
        assertTrue(L("DeLETE FROM $T").literal.isDELETE());
        assertTrue(L("Drop table $T").literal.isDROP());
        assertTrue(L("crEATE table abc(id INT)").literal.isCREATE());
    }

    @Test
    public void test_var_set_index() {
        NutSql sql = L("$A,$B,@C,@D,@C");
        int[] is = sql.literal.getParamIndexes().getOrderIndex("C");
        assertEquals(0, is[0]);
        assertEquals(2, is[1]);
        is = sql.literal.getParamIndexes().getOrderIndex("D");
        assertEquals(1, is[0]);
    }

    @Test
    public void test_toPreparedStatement() {
        NutSql sql = L("=@a=@b");
        String exp = "=?=?";
        String actural = sql.toPreparedStatement();
        assertEquals(exp, actural);

    }

    @Test
    public void test_toPreparedStatement_regularly() {
        NutSql sql = L("UPDATE dao_platoon SET name=@name,base=@baseName,leader=@leaderName WHERE id=@id");
        String exp = "UPDATE dao_platoon SET name=?,base=?,leader=? WHERE id=?";
        String actural = sql.toPreparedStatement();
        assertEquals(exp, actural);
    }

    @Test
    public void test_dot_with_var() {
        NutSql sql = L("$x.y");
        sql.vars().set("x", "T");
        assertEquals("T.y", sql.toString());
    }

    @Test
    public void test_dot_with_param() {
        NutSql sql = L("@x.y");
        sql.params().set("x", "T");
        assertEquals("'T'.y", sql.toString());
    }

    @Test
    public void test_param_names() {
        NutSql sql = L("UPDATE dao_platoon SET name=@name1,base=@baseName2,leader=@leaderName3 WHERE id=@id4");
        String[] paramNames = sql.literal.getParamIndexes()
                                            .names()
                                            .toArray(new String[sql.literal.getParamIndexes()
                                                                            .names()
                                                                            .size()]);
        String result[] = {"leaderName3", "id4", "baseName2", "name1"};
        Arrays.sort(paramNames);
        Arrays.sort(result);
        assertArrayEquals(paramNames, result);
    }

    @Test
    public void test_var_names() {
        NutSql sql = L("InSeRT INTO $T ($id,$name) VALUES(@id1,@name2)");
        String[] varNames = sql.literal.getVarIndexes()
                                        .names()
                                        .toArray(new String[sql.literal.getVarIndexes().size()]);
        String result[] = {"T", "name", "id"};
        // System.out.println(Json.toJson(varNames));

        Arrays.sort(varNames);
        Arrays.sort(result);
        assertArrayEquals(varNames, result);
    }
    
    @Test
    public void test_param_names_putall_map() {
        NutSql sql = L("INSERT INTO t_pet($id,$name,$alias,$age) VALUES(@id,@name,@nickName,@age)");
        
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("id", "userId");
        map.put("name", "userName");
        map.put("alias", "alias");
        map.put("age", "age");
        
        Pet pet = new Pet();
        pet.setId(18);
        pet.setName("pet");
        pet.setNickName("haha");
        pet.setAge(5);
        
        sql.vars().putAll(map);
        sql.params().putAll(pet);

        String expect = "INSERT INTO t_pet(userId,userName,alias,age) VALUES(18,'pet','haha',5)";
        assertEquals(expect, sql.toString());
    }
}
