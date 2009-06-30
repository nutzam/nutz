package org.nutz.ioc;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import org.nutz.ioc.meta.AllMetaTest;

@RunWith(Suite.class)
@Suite.SuiteClasses( { AllMetaTest.class, AllJsonIoc.class, AllDatabaseIoc.class })
public class AllIoc {}
