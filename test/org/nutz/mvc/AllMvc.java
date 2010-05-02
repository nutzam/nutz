package org.nutz.mvc;

import org.junit.runner.RunWith;

import org.junit.runners.Suite;
import org.nutz.mvc.adaptor.JsonAdaptorTest;
import org.nutz.mvc.init.MvcBaseTest;
import org.nutz.mvc.init.PathNodeTest;
import org.nutz.mvc.upload.unit.UploadingUnitTest;
import org.nutz.mvc.upload.util.BufferRingTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({	PathNodeTest.class,
						MvcBaseTest.class,
						BufferRingTest.class,
						UploadingUnitTest.class,
						JsonAdaptorTest.class})
public class AllMvc {}
