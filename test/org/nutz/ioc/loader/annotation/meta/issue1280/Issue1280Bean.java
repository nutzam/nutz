package org.nutz.ioc.loader.annotation.meta.issue1280;

import javax.sql.DataSource;

import org.nutz.dao.Dao;
import org.nutz.dao.impl.NutDao;
import org.nutz.dao.impl.SimpleDataSource;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

@IocBean
public class Issue1280Bean {

    @IocBean
    public DataSource getDataSource() {
        SimpleDataSource ds = new SimpleDataSource();
        ds.setJdbcUrl("jdbc:h2:mem:nutztest");
        return ds;
    }
    
    @IocBean
    public Dao buildDao(DataSource dataSource) {
        return new NutDao(dataSource);
    }
    

    @IocBean(name="dao2")
    public Dao xxxgetDao2(DataSource dataSource) {
        return new NutDao(dataSource);
    }
    

    @IocBean(name="dao3")
    public Dao xxyyy(@Inject("refer:dataSource")DataSource ds) {
        return new NutDao(ds);
    }
}
