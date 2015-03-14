package org.nutz.dao.test.sqls;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ SQLFileParsingTest.class, SqlImplTest.class, CustomizedSqlsTest.class, CallbackTest.class , SqlTemplateTest.class})
public class AllSqls {
}
