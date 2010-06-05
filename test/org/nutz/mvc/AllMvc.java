package org.nutz.mvc;

import org.junit.runner.RunWith;

import org.junit.runners.Suite;
import org.nutz.mvc.adaptor.JsonAdaptorTest;
import org.nutz.mvc.init.MvcBaseTest;
import org.nutz.mvc.init.PathNodeTest;
import org.nutz.mvc.upload.unit.UploadingUnitTest;
import org.nutz.mvc.upload.util.BufferRingTest;
import org.nutz.mvc.upload.util.RemountBytesTest;
import org.nutz.mvc.view.AllView;

@RunWith(Suite.class)
@Suite.SuiteClasses({	PathNodeTest.class,
						MvcBaseTest.class,
						RemountBytesTest.class,
						BufferRingTest.class,
						UploadingUnitTest.class,
						JsonAdaptorTest.class,
						AllView.class})
public class AllMvc {}
