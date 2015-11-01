package org.nutz.test;

import static org.junit.Assert.*;

import org.junit.Test;
import org.nutz.aop.interceptor.ioc.TransAop;
import org.nutz.ioc.loader.annotation.Inject;

public class PeanutTest extends BaseNutTest {

    @Inject(value=TransAop.SERIALIZABLE)
    public Object tx;
    
    @Test
    public void test_assert() {
        assertNotNull(tx); // 因为注入成功,那肯定有值
    }
    
    protected String[] getIocConfigure() throws Exception {
        return new String[]{"*tx"};
    }
}
