package org.nutz.mvc;

import org.junit.runner.RunWith;

import org.junit.runners.Suite;
import org.nutz.mvc.adaptor.JsonAdaptorTest;
import org.nutz.mvc.adaptor.injector.AllInjector;
import org.nutz.mvc.impl.MappingNodeTest;
import org.nutz.mvc.impl.ViewProcessorTest;
import org.nutz.mvc.init.AllInit;
import org.nutz.mvc.upload.unit.UploadingUnitTest;
import org.nutz.mvc.upload.util.BufferRingTest;
import org.nutz.mvc.upload.util.RemountBytesTest;
import org.nutz.mvc.view.AllView;
import org.nutz.mvc.view.DefaultViewMakerTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({	MappingNodeTest.class,
						RemountBytesTest.class,
						BufferRingTest.class,
						UploadingUnitTest.class,
						JsonAdaptorTest.class,
						AllInit.class,
						AllInjector.class,
						AllView.class,
						DefaultViewMakerTest.class,
						ViewProcessorTest.class})
public class AllMvc {}
