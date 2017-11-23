package org.nutz.dao.test.interceptor;

import java.util.Arrays;

import javax.sql.DataSource;

import org.junit.Assert;
import org.junit.Test;
import org.nutz.dao.Cnd;
import org.nutz.dao.DaoException;
import org.nutz.dao.DaoInterceptor;
import org.nutz.dao.DaoInterceptorChain;
import org.nutz.dao.FieldMatcher;
import org.nutz.dao.impl.NutDao;
import org.nutz.dao.test.DaoCase;
import org.nutz.dao.test.meta.Pet;

public class SimpleDaoInterceptorTest extends DaoCase {

    /**
     * 测试的内容: 在DaoInterceptor内设置字段过滤,看看能不能起作用
     */
    @Test
    public void issue_1325() {
        // 现有默认dao创建记录

        this.dao.create(Pet.class, true);
        Pet pet = Pet.create("wendal");
        pet.setAge(30);
        this.dao.insert(pet);
        
        // 然后构建一个临时用的Dao实例, 拦截器顺序: 设置FieldFilter, log
        DataSource ds = ioc.get(DataSource.class);
        NutDao dao = new NutDao(ds);
        dao.setInterceptors(Arrays.asList(new DaoInterceptor() {
            public void filter(DaoInterceptorChain chain) throws DaoException {
                chain.getDaoStatement().getContext().setFieldMatcher(FieldMatcher.make(null, "age", false));
                chain.doChain();
            }
        }, "log"));
        // 用临时dao,应该是没有age数据
        pet = dao.fetch(Pet.class, Cnd.where("name", "=", "wendal"));
        Assert.assertEquals(0, pet.getAge());
        // 用默认dao, 应该有age数据
        pet = this.dao.fetch(Pet.class, Cnd.where("name", "=", "wendal"));
        Assert.assertEquals(30, pet.getAge());
    }
}
