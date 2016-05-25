package org.nutz.ioc.loader.annotation.meta;

import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

@IocBean
public class Issue1060 {

    @Inject
    public void setDao(Dao dao){}
}
