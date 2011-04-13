package org.nutz.mvc.testapp;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.nutz.mvc.testapp.views.AllView;

@RunWith(Suite.class)
@Suite.SuiteClasses({BaseTest.class,
					 AllView.class})
public class AllApp {}
