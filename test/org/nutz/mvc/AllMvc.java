package org.nutz.mvc;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.nutz.mvc.init.PathNodeTest;
import org.nutz.mvc.upload.UploadingTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({PathNodeTest.class,UploadingTest.class})
public class AllMvc {}
