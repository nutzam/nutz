package org.nutz.lang;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.nutz.castor.AllCastor;
import org.nutz.lang.encrypt.AllEncrypts;
import org.nutz.lang.meta.AllMeta;
import org.nutz.lang.random.AllRandom;
import org.nutz.lang.segment.CharSegmentTest;
import org.nutz.lang.segment.SegmentsTest;
import org.nutz.lang.stream.StringOutputStreamTest;
import org.nutz.lang.tmpl.TmplTest;
import org.nutz.lang.util.AllUtil;
import org.nutz.lang.util.XmlsTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({MirrorTest.class,
                     LangTest.class,
                     TimesTest.class,
                     FilesTest.class,
                     MathsTest.class,
                     AllRandom.class,
                     CharSegmentTest.class,
                     TmplTest.class,
                     SegmentsTest.class,
                     AllCastor.class,
                     AllUtil.class,
                     NumsTest.class,
                     StringsTest.class,
                     StringOutputStreamTest.class,
                     AllMeta.class,
                     CodeTest.class,
                     AllEncrypts.class,
                     XmlsTest.class})
public class AllLang {}
