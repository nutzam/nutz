package org.nutz.dao.texp;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.nutz.dao.Cnd;
import org.nutz.dao.Condition;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.test.DaoCase;
import org.nutz.dao.util.cri.SqlExpression;
import org.nutz.lang.Lang;

public class CndTest extends DaoCase {

    private Entity<?> en;

    protected void before() {
        en = dao.create(Worker.class, true);
    }

    protected void after() {}
    
    @Test
    public void test_segment() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("name", "比尔盖茨");
        map.put("age", 50);
        Condition c1 = Cnd.wrap("name='${name}' AND age>${age}", map);
        assertEquals("name='比尔盖茨' AND age>50", c1.toSql(en));
        
        Worker worker = new Worker();
        worker.name = "老板";
        worker.age = 30;
        Condition c2 = Cnd.wrap("name like'${name}%' AND age>${age}", worker);
        assertEquals("name like'老板%' AND age>30", c2.toSql(en));
    }

    @Test
    public void test_gt_like() {
        Condition c = Cnd.where("id", ">", 45).and("name", "LIKE", "%ry%");
        String exp = "WHERE wid>45 AND wname LIKE '%ry%'";
        assertEquals(exp, c.toSql(en).trim());
    }

    @Test
    public void test_bracket() {
        Condition c = Cnd.where(Cnd.exps("id", ">", 45)).and("name", "LIKE", "%ry%");
        String exp = "WHERE (wid>45) AND wname LIKE '%ry%'";
        assertEquals(exp, c.toSql(en).trim());
    }

    @Test
    public void test_order() {
        Condition c = Cnd.orderBy().asc("id").desc("name").asc("age").desc("workingDay");
        String exp = "ORDER BY wid ASC, wname DESC, age ASC, days DESC";
        assertEquals(exp, c.toSql(en).trim());
    }

    @Test
    public void test_like_in() {
        int[] ages = {4, 7, 9};
        SqlExpression e = Cnd.exps("age", ">", 35).and("id", "<", 47);
        SqlExpression e2 = Cnd.exps("name", "\tLIKE ", "%t%").and("age", "IN  \n\r", ages).or(e);
        Condition c = Cnd.where("id", "=", 37).and(e).or(e2).asc("age").desc("id");
        String exp = "WHERE wid=37 AND (age>35 AND wid<47) OR (wname LIKE '%t%' AND age IN (4,7,9) OR (age>35 AND wid<47)) ORDER BY age ASC, wid DESC";
        assertEquals(exp, c.toSql(en).trim());
    }

    @Test
    public void test_equel() {
        Condition c = Cnd.where("ff", "=", true);
        String exp = "WHERE ff=true";
        assertEquals(exp, c.toSql(en).trim());
    }

    @Test
    public void test_in_by_int_array() {
        int[] ids = {3, 5, 7};
        Condition c = Cnd.where("id", "iN", ids);
        String exp = "WHERE id IN (3,5,7)";
        assertEquals(exp, c.toSql(null).trim());
    }

    @Test
    public void test_in_by_int_list() {
        List<Integer> list = new ArrayList<Integer>();
        list.add(3);
        list.add(5);
        list.add(7);
        Condition c = Cnd.where("id", "iN", list);
        String exp = "WHERE id IN (3,5,7)";
        assertEquals(exp, c.toSql(null).trim());
    }

    @Test
    public void test_in_by_str_array() {
        Condition c = Cnd.where("nm", "iN", Lang.array("'A'", "B"));
        String exp = "WHERE nm IN ('''A''','B')";
        assertEquals(exp, c.toSql(null).trim());
    }

    @Test
    public void test_in_by_str_list() {
        List<String> list = new ArrayList<String>();
        list.add("'A'");
        list.add("B");
        Condition c = Cnd.where("nm", "iN", list);
        String exp = "WHERE nm IN ('''A''','B')";
        assertEquals(exp, c.toSql(null).trim());
    }

    @Test
    public void test_is_null() {
        Condition c = Cnd.where("nm", " is ", null);
        String exp = "WHERE nm IS NULL";
        assertEquals(exp, c.toSql(null).trim());
    }

    @Test
    public void test_is_not_null() {
        Condition c = Cnd.where("nm", " is nOT ", null);
        String exp = "WHERE nm IS NOT NULL";
        assertEquals(exp, c.toSql(null).trim());
    }

    @Test
    public void test_not_in() {
        Condition c = Cnd.where("nm", " Not iN ", new int[]{1, 2, 3});
        String exp = "WHERE nm NOT IN (1,2,3)";
        assertEquals(exp, c.toSql(null).trim());
    }

    @Test
    public void test_add_other_or_method_by_github_issuse_148() {
        SqlExpression e1 = Cnd.exps("city", "=", "beijing").or("city", "=", "shanghai").or("city", "=", "guangzhou").or("city", "=", "shenzhen");
        SqlExpression e2 = Cnd.exps("age", ">", 18).and("age", "<", 30);
        String exp = "WHERE (ct='beijing' OR ct='shanghai' OR ct='guangzhou' OR ct='shenzhen') AND (age>18 AND age<30)";
        assertEquals(exp, Cnd.where(e1).and(e2).toSql(en).trim());
    }
    
    @Test
    public void test_other_op() {
        assertEquals(" WHERE ok IS true", Cnd.where("ok", "is", true).toString());
    }
}
