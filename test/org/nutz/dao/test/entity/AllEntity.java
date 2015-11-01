package org.nutz.dao.test.entity;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.nutz.dao.test.lazy.LazyNutDaoTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({EntityParsingTest.class, DynamicEntityParsingTest.class, LazyNutDaoTest.class})
public class AllEntity {

}
