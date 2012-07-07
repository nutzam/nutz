package org.nutz.mvc.testapp;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
//import org.nutz.mvc.testapp.adaptor.AllAdaptor;
import org.nutz.mvc.testapp.adaptor.AllAdaptor;
import org.nutz.mvc.testapp.upload.AllUpload;
import org.nutz.mvc.testapp.views.AllView;

@RunWith(Suite.class)
@Suite.SuiteClasses({BaseTest.class,
                     AllView.class,
                     AllAdaptor.class,
                     AllUpload.class})
public class AllApp {}
