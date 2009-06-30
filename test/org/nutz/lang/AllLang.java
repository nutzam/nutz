package org.nutz.lang;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import org.nutz.castor.CastorTest;
import org.nutz.lang.random.ArrayRandomTest;
import org.nutz.lang.segment.CharSegmentTest;

@RunWith(Suite.class)
@Suite.SuiteClasses( { MirrorTest.class, LangTest.class, MathsTest.class, ArrayRandomTest.class,
		CharSegmentTest.class, CastorTest.class })
public class AllLang {}
