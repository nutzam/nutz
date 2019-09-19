package org.nutz.dao.test.interceptor;

import org.junit.Test;
import org.nutz.dao.Sqls;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.test.DaoCase;
import org.nutz.dao.test.meta.XPet;
import org.nutz.lang.Lang;

import static org.junit.Assert.*;
import static org.junit.Assert.assertNotNull;

public class SimplePojoInterceptorTest extends DaoCase {

    @Test
    public void test_pojo_interceptor_anno() {
        String name="huchuc@vip.qq.com";
        dao.create(XPet.class, true);
        XPet xPet = new XPet();
        //主动设置name值，PrevInsert nullEffective=true，实际name已有预设值则PrevInsert不会起效
        xPet.setName(name);
        dao.insert(xPet);
        assertEquals(1, dao.count(XPet.class));
        assertNotNull(dao.fetch(XPet.class));
        Sql sql = Sqls.fetchEntity("select * from t_xpet");
        sql.setEntity(dao.getEntity(XPet.class));
        dao.execute(sql);
        XPet xPet1 = sql.getObject(XPet.class);
        assertEquals(xPet1.getName(),name);
        assertNotNull(sql.getObject(XPet.class));
        assertNull(sql.getObject(XPet.class).getOtherTime());
        Lang.quiteSleep(1000);
        dao.update(sql.getObject(XPet.class), "updateTime");
    }
}
