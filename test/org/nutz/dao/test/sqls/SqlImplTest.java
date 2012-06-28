package org.nutz.dao.test.sqls;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.nutz.dao.Sqls;
import org.nutz.dao.sql.Sql;

public class SqlImplTest {

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

}
