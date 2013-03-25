package org.nutz.dao.test.lazy;

import javax.sql.DataSource;

import org.junit.Test;
import org.nutz.dao.impl.ext.LazyNutDao;
import org.nutz.dao.test.DaoCase;
import org.nutz.dao.test.meta.Base;
import org.nutz.json.Json;

public class LazyNutDaoTest extends DaoCase {

    @Override
    protected void before() {
        dao = new LazyNutDao(ioc.get(DataSource.class));
        pojos.initData();
    }
    
    @Test
    public void test_lazy_get() {
        Base base = dao.fetch(Base.class);
        System.out.println("------------------------------------------");
        System.out.println(base.getCountry()); //一个@One属性
        System.out.println(base.getPlatoons());
    }
    
    @Test
    public void test_lazy_toJson() {
    	LazyNutDao dao = new LazyNutDao(ioc.get(DataSource.class));
    	dao.setCycle(false);
    	Base base = dao.fetch(Base.class);
    	System.out.println("================"+Json.toJson(base));
    }
}
