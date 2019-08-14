package org.nutz.dao.test.interceptor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.nutz.dao.Sqls;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.test.DaoCase;
import org.nutz.dao.test.meta.XPet;
import org.nutz.lang.Lang;

public class SimplePojoInterceptorTest extends DaoCase {

    @Test
    public void test_pojo_interceptor_anno() {
        dao.create(XPet.class, true);
        dao.insert(new XPet());
        
        assertEquals(1, dao.count(XPet.class));
        assertNotNull(dao.fetch(XPet.class));
        
        Sql sql = Sqls.fetchEntity("select * from t_xpet");
        sql.setEntity(dao.getEntity(XPet.class));
        dao.execute(sql);
        assertNotNull(sql.getObject(XPet.class));
        assertNull(sql.getObject(XPet.class).getOtherTime());
        Lang.quiteSleep(1000);
        dao.update(sql.getObject(XPet.class), "updateTime");
    }
}
