package org.nutz.ioc;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( { AllIocWithDB.class, AllIocWithoutDB.class })
public class AllIoc {}
