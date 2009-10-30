package org.nutz.dao.test.normal;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({FieldFilterTest.class, QueryTest.class, UpdateTest.class,
		SupportedFieldType.class, AutoGenerateValueTest.class})
public class AllNormal {}
