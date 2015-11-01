package org.nutz.mvc.testapp.classes.action.adaptor;

import org.nutz.aop.interceptor.ioc.TransAop;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.test.meta.Pet;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;

@IocBean(create="init")
@At("/aop")
public class AopTransModule {
    
    @Inject Dao dao;
    
    public void init() {
        dao.create(Pet.class, true);
    }

    @At("/test1")
    @Aop(TransAop.SERIALIZABLE)
    @Fail("http:200")
    @Ok("http:500")
    public void test_aop_trans_1(@Param("name")String name){
        dao.insert(Pet.create(name));
        throw new RuntimeException();
    }
    
    @At("/test1/result")
    @Ok("raw")
    @Fail("http:500")
    public int tets_aop_trans_1_result(@Param("name")String name) {
        return dao.count(Pet.class, Cnd.where("name", "=", name));
    }
}
