package org.nutz.dao.test;

import org.junit.After;
import org.junit.Before;
import org.nutz.Nutzs;
import org.nutz.dao.Dao;
import org.nutz.dao.test.meta.Pojos;
import org.nutz.ioc.Ioc;
import org.nutz.trans.Trans;

public abstract class DaoCase {

    protected Dao dao;
    protected Ioc ioc;
    protected Pojos pojos;

    @Before
    public void setUp() {
        Trans.DEBUG = false;
        Nutzs.checkProperties();
        ioc = Nutzs.getIoc("config/dao-test.js");
        dao = ioc.get(Dao.class, "dao");
        pojos = ioc.get(Pojos.class, "metas");
        before();
    }

    @After
    public void tearDown() {
        after();
    }

    protected void before() {}

    protected void after() {}

}
