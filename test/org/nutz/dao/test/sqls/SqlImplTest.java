package org.nutz.dao.test.sqls;

import org.junit.Test;
import org.nutz.dao.Sqls;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.test.DaoCase;
import org.nutz.dao.test.meta.Pet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SqlImplTest extends DaoCase {

    @Test
    public void test_sql_get_list() {
        Sql sql = Sqls.create("SELECT version()");
        ArrayList<Map<Object, Object>> list = new ArrayList<Map<Object, Object>>();
        list.add(new HashMap<Object, Object>());
        list.add(new HashMap<Object, Object>());

        sql.getContext().setResult(list);

        List<?> re = sql.getList(Map.class);// 传入 map结果会导致上面的isFrom 为false
        assertTrue(re == list);

        re = sql.getList(HashMap.class);// 因为list中的实例是HashMap,因此能够正常返回
        assertTrue(re == list);
    }

    // https://nutz.cn/yvr/t/2emsd1ma36g99rmnqtq6o87skj
    @Test
    public void test_sql_with_many_vars() {
        String str = Sqls.create("${a}${b}").setVar("a", 1).setVar("b", 2).toString();
        assertEquals(str, "12");
        
        str = Sqls.create("${a}_${b}").setVar("a", 1).setVar("b", 2).toString();
        assertEquals(str, "1_2");

        HashMap<String, Object> vars = new HashMap<String, Object>();
        vars.put("c", 3);
        str = Sqls.create("${a}_${b}_${c}").setVars(vars).setVar("a", 1).setVar("b", 2).toString();
        assertEquals(str, "1_2_3");
    }

    @Test
    public void test_sql_with_many_params() {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("a", 1);
        params.put("b", 2);
        String str = Sqls.create("select * from x where a=@a and b=@b and c=@c").setParams(params).setParam("c", 3).toString();
        assertEquals(str, "select * from x where a=1 and b=2 and c=3");
    }
    
    @Test
    public void test_sql_in_list() {
        List<String> list = new ArrayList<String>();
        list.add("wendal");
        list.add("zozoh");
        list.add("pangwu");
        Sql sql = Sqls.queryEntity("select id from t_pet where name in (@list)");
        sql.setParam("list", list);
        sql.setEntity(dao.getEntity(Pet.class));
        System.out.println(sql.forPrint());
        System.out.println(sql.toPreparedStatement());
        assertEquals(3, sql.getParamMatrix()[0].length);
        dao.create(Pet.class, true);
        dao.insert(Pet.create("wendal"));
        dao.insert(Pet.create("zozoh"));
        dao.insert(Pet.create("pangwu"));
        List<Pet> pets = dao.execute(sql).getList(Pet.class);
        assertEquals(3, pets.size());
    }
}
