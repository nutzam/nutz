package org.nutz.dao.test.sqls;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.nutz.dao.Sqls;
import org.nutz.dao.impl.sql.callback.FetchBooleanCallback;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.test.DaoCase;
import org.nutz.dao.test.meta.Pet;

public class CallbackTest extends DaoCase {

    @Test
    public void test_fetchBooleanCallback() {
        if (dao.meta().isOracle())
            return;
        pojos.initPet();
        dao.insert(Pet.create(4));

        Sql sql = Sqls.create("SELECT 'pet_02' IN (SELECT name FROM t_pet)");
        sql.setCallback(new FetchBooleanCallback());
        dao.execute(sql);
        boolean pet02IsExsit = sql.getBoolean();
        assertEquals(true, pet02IsExsit);

        sql = Sqls.create("SELECT 'pet_05' IN (SELECT name FROM t_pet)");
        sql.setCallback(new FetchBooleanCallback());
        dao.execute(sql);
        boolean pet05IsExsit = sql.getBoolean();
        assertEquals(false, pet05IsExsit);
    }
}
