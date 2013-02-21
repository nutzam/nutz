package org.nutz.dao.test.normal;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({    FieldFilterTest.class,
                        SimpleDaoTest.class,
                        InsertTest.class,
                        QueryTest.class,
                        EachTest.class,
                        UpdateTest.class,
                        BoneCP_Test.class,
                        SupportedFieldTypeTest.class,
                        AutoGenerateValueTest.class,
                        PkTest.class,
                        BinaryDaoTest.class,
                        CreateDropTableTest.class,
                        CreateTableWithCommentTest.class,
                        DaoRecordTest.class})
public class AllNormal {}
