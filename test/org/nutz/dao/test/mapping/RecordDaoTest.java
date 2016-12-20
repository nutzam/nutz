package org.nutz.dao.test.mapping;

import static org.junit.Assert.*;

import org.junit.Test;
import org.nutz.dao.Dao;
import org.nutz.dao.entity.Record;
import org.nutz.dao.test.DaoCase;
import org.nutz.lang.random.R;

public class RecordDaoTest extends DaoCase {

    @Test
    public void test_insert_with_autoinc_id() {
        if (dao.meta().isOracle())
            return;
        Dao dao = ioc.get(Dao.class);
        for (int i = 0; i < 10; i++) {
            Record re = Record.create();
            re.put("*name", "wendal"+R.UU32());
            re.put("+id", 0);
            re.put(".table", "t_pet");
            dao.insert(re);
            assertTrue(re.getInt("id") > 0);
        }
    }
}
