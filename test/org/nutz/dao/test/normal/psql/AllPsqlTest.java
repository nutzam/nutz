package org.nutz.dao.test.normal.psql;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({PsqlJsonTest.class, PsqlArrayTest.class, PsqlJsonAdaptorTest.class})
public class AllPsqlTest {}
