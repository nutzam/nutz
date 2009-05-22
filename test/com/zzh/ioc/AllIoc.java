package com.zzh.ioc;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.zzh.ioc.meta.AllMetaTest;

@RunWith(Suite.class)
@Suite.SuiteClasses( { AllMetaTest.class, AllJsonIoc.class, AllDatabaseIoc.class })
public class AllIoc {}
