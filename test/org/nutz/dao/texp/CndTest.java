package org.nutz.dao.texp;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.nutz.dao.Cnd;
import org.nutz.dao.Condition;
import org.nutz.dao.FieldMatcher;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.sql.Criteria;
import org.nutz.dao.test.DaoCase;
import org.nutz.dao.test.meta.Pet;
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
        Condition c = Cnd.where(Cnd.exps("id", ">", 45)).and("name",
                                                             "LIKE",
                                                             "%ry%");
        String exp = "WHERE (wid>45) AND wname LIKE '%ry%'";
        assertEquals(exp, c.toSql(en).trim());
    }

    @Test
    public void test_order() {
        Condition c = Cnd.orderBy()
                         .asc("id")
                         .desc("name")
                         .asc("age")
                         .desc("workingDay");
        String exp = "ORDER BY wid ASC, wname DESC, age ASC, days DESC";
        assertEquals(exp, c.toSql(en).trim());
    }

    @Test
    public void test_like_in() {
        int[] ages = {4, 7, 9};
        SqlExpression e = Cnd.exps("age", ">", 35).and("id", "<", 47);
        SqlExpression e2 = Cnd.exps("name", "\tLIKE ", "%t%")
                              .and("age", "IN  \n\r", ages)
                              .or(e);
        Condition c = Cnd.where("id", "=", 37)
                         .and(e)
                         .or(e2)
                         .asc("age")
                         .desc("id");
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
        SqlExpression e1 = Cnd.exps("city", "=", "beijing")
                              .or("city", "=", "shanghai")
                              .or("city", "=", "guangzhou")
                              .or("city", "=", "shenzhen");
        SqlExpression e2 = Cnd.exps("age", ">", 18).and("age", "<", 30);
        String exp = "WHERE (ct='beijing' OR ct='shanghai' OR ct='guangzhou' OR ct='shenzhen') AND (age>18 AND age<30)";
        assertEquals(exp, Cnd.where(e1).and(e2).toSql(en).trim());
    }

    @Test
    public void test_other_op() {
        assertEquals(" WHERE ok IS true", Cnd.where("ok", "is", true)
                                             .toString());
    }
    
    @Test
    public void test_from_obj() {
        Pet pet = new Pet();
        pet.setName("wendal");
        System.out.println(Cnd.from(dao, pet));
        assertEquals(" WHERE (name='wendal')", Cnd.from(dao, pet).toString());
        
        pet.setAge(10);
        System.out.println(Cnd.from(dao, pet));
        assertEquals(" WHERE (name='wendal' AND age=10)", Cnd.from(dao, pet).toString());
        
        pet.setAge(0);
        assertEquals(" WHERE (name='wendal' AND age=0)", Cnd.from(dao, pet, FieldMatcher.make("age|name", null, true).setIgnoreZero(false)).toString());
    }
    
    @Test
    public void test_not_sql_group() {
        SqlExpression e2 = Cnd.exps("f2", "=", 1);
        SqlExpression e3 = Cnd.exps("f3", "=", 1);
        Condition c = Cnd.where(e2).andNot(e3);
        assertEquals(" WHERE (f2=1) AND NOT (f3=1)", c.toString());
    }
    
    /**
     * 序列化测试
     */
    @Test
    public void test_obj_read_write() {
        SqlExpression e2 = Cnd.exps("f2", "=", 1);
        SqlExpression e3 = Cnd.exps("f3", "=", 1);
        Condition c = Cnd.where(e2).andNot(e3);
        
        byte[] buf = Lang.toBytes(c);
        c = Lang.fromBytes(buf, Cnd.class);
        assertEquals(" WHERE (f2=1) AND NOT (f3=1)", c.toString());
    }
    
    /**
     *  Criteria 接口测试int[]数组
     */
    @Test
    public void test_in_by_criteria_int_array () {
    	int[] ids = {1,2,3};
    	Criteria cri = Cnd.cri();
    	cri.where().andInIntArray2("nm", ids);
    	assertEquals(" WHERE nm IN (1,2,3)", cri.toString());
    }
    
    /**
     *  Criteria 接口测试List&lt;Integer&gt;
     */
    @Test
    public void test_in_by_criteria_int_list () {
    	 List<Integer> ids = new ArrayList<Integer>();
    	 ids.add(1);
    	 ids.add(2);
    	 ids.add(3);
    	Criteria cri = Cnd.cri();
    	cri.where().andInIntList("nm", ids);
    	assertEquals(" WHERE nm IN (1,2,3)", cri.toString());
    }
    
    /**
     *  Criteria 接口测试long[]数组
     */
    @Test
    public void test_in_by_criteria_long_array () {
    	long[] ids = {1L,2L,3L};
    	Criteria cri = Cnd.cri();
    	cri.where().andInArray("nm", ids);
    	assertEquals(" WHERE nm IN (1,2,3)", cri.toString());
    }
    
    /**
     *  Criteria 接口测试List&lt;Long&gt;
     */
    @Test
    public void test_in_by_criteria_long_list () {
    	List<Long> ids = new ArrayList<Long>();
    	ids.add(1L);
    	ids.add(2L);
    	ids.add(3L);
    	Criteria cri = Cnd.cri();
    	cri.where().andInList("nm", ids);
    	assertEquals(" WHERE nm IN (1,2,3)", cri.toString());
    }
    
    /**
     *  Criteria 接口测试String[]数组
     */
    @Test
    public void test_in_by_criteria_string_array () {
    	String[] ids = {"bj","sh","gz","sz"};
    	Criteria cri = Cnd.cri();
    	cri.where().andInStrArray("nm", ids);
    	assertEquals(" WHERE nm IN ('bj','sh','gz','sz')", cri.toString());
    }
    
    /**
     *  Criteria 接口测试List&lt;String&gt;
     */
    @Test
    public void test_in_by_criteria_string_list () {
    	List<String> ids = new ArrayList<String>();
    	ids.add("bj");
    	ids.add("sh");
    	ids.add("gz");
    	ids.add("sz");
    	Criteria cri = Cnd.cri();
    	cri.where().andInStrList("nm", ids);
    	assertEquals(" WHERE nm IN ('bj','sh','gz','sz')", cri.toString());
    }
    
    /**
     *  Criteria 接口测试int[]数组
     */
    @Test
    public void test_not_in_by_criteria_int_array () {
    	int[] ids = {1,2,3};
    	Criteria cri = Cnd.cri();
    	cri.where().andNotInArray("nm", ids);
    	assertEquals(" WHERE nm NOT IN (1,2,3)", cri.toString());
    }
    
    /**
     *  Criteria 接口测试List&lt;Integer&gt;
     */
    @Test
    public void test_not_in_by_criteria_int_list () {
    	 List<Integer> ids = new ArrayList<Integer>();
    	 ids.add(1);
    	 ids.add(2);
    	 ids.add(3);
    	Criteria cri = Cnd.cri();
    	cri.where().andNotInIntList("nm", ids);
    	assertEquals(" WHERE nm NOT IN (1,2,3)", cri.toString());
    }
    
    /**
     *  Criteria 接口测试int[]数组
     */
    @Test
    public void test_not_in_by_criteria_long_array () {
    	int[] ids = {1,2,3};
    	Criteria cri = Cnd.cri();
    	cri.where().andNotInArray("nm", ids);
    	assertEquals(" WHERE nm NOT IN (1,2,3)", cri.toString());
    }
    
    /**
     *  Criteria 接口测试List&lt;Integer&gt;
     */
    @Test
    public void test_not_in_by_criteria_long_list () {
    	List<Integer> ids = new ArrayList<Integer>();
    	ids.add(1);
    	ids.add(2);
    	ids.add(3);
    	Criteria cri = Cnd.cri();
    	cri.where().andNotInIntList("nm", ids);
    	assertEquals(" WHERE nm NOT IN (1,2,3)", cri.toString());
    }
    
    /**
     *  Criteria 接口测试String[]数组
     */
    @Test
    public void test_not_in_by_criteria_string_array () {
    	String[] ids = {"bj","sh","gz","sz"};
    	Criteria cri = Cnd.cri();
    	cri.where().andNotInArray("nm", ids);
    	assertEquals(" WHERE nm NOT IN ('bj','sh','gz','sz')", cri.toString());
    }
    
    /**
     *  Criteria 接口测试List&lt;String&gt;
     */
    @Test
    public void test_not_in_by_criteria_string_list () {
    	List<String> ids = new ArrayList<String>();
    	ids.add("bj");
    	ids.add("sh");
    	ids.add("gz");
    	ids.add("sz");
    	Criteria cri = Cnd.cri();
    	cri.where().andNotInStrList("nm", ids);
    	assertEquals(" WHERE nm NOT IN ('bj','sh','gz','sz')", cri.toString());
    }
}
