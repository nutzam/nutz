package org.nutz.dao.test.interceptor;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
    SimpleDaoInterceptorTest.class,
    SimplePojoInterceptorTest.class
})
public class AllDaoInterceptorTest {

}
