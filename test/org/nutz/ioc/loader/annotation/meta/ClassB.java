package org.nutz.ioc.loader.annotation.meta;

import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

@IocBean
public class ClassB {
    
    @Inject("refer:dao")
    public Dao dao;
    
    public void setDao(Dao dao) {
        this.dao = dao;
    }

}
