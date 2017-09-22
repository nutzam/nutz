package org.nutz.dao;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.nutz.dao.impl.sql.SqlLiteralTest;
import org.nutz.dao.test.entity.AllEntity;
import org.nutz.dao.test.exec.AllDaoExec;
import org.nutz.dao.test.interceptor.AllDaoInterceptorTest;
import org.nutz.dao.test.mapping.AllMapping;
import org.nutz.dao.test.normal.AllNormal;
import org.nutz.dao.test.normal.mysql.AllMysqlTest;
import org.nutz.dao.test.normal.psql.AllPsqlTest;
import org.nutz.dao.test.smoke.AllSmoke;
import org.nutz.dao.test.sqls.AllSqls;
import org.nutz.dao.texp.ChainTest;
import org.nutz.dao.texp.CndTest;

/**
 * Prepare a database with URL: jdbc:mysql://localhost:3306/zzhtest support user
 * root@123456
 *
 * @author zozoh
 *
 */

@RunWith(Suite.class)
@Suite.SuiteClasses({AllEntity.class,
                     AllSmoke.class,
                     AllSqls.class,
                     AllMapping.class,
                     AllNormal.class,
                     CndTest.class,
                     ChainTest.class,
                     SqlLiteralTest.class,
                     AllDaoExec.class,
                     AllPsqlTest.class,
                     AllMysqlTest.class,
                     AllDaoInterceptorTest.class})
public class AllDao {}
