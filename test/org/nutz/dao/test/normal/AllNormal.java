package org.nutz.dao.test.normal;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({	FieldFilterTest.class,
						SimpleDaoTest.class,
						QueryTest.class,
						UpdateTest.class,
						BoneCP_Test.class,
						SupportedFieldTypeTest.class,
						AutoGenerateValueTest.class,
						PkTest.class})
public class AllNormal {}
