package com.zzh.lang;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.zzh.castor.CastorTest;
import com.zzh.lang.random.ArrayRandomTest;
import com.zzh.lang.segment.CharSegmentTest;

@RunWith(Suite.class)
@Suite.SuiteClasses( { MirrorTest.class, LangTest.class, ArrayRandomTest.class,
		CharSegmentTest.class, CastorTest.class })
public class AllLang {}
