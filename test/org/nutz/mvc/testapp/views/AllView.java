package org.nutz.mvc.testapp.views;

import org.junit.runners.Suite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ForwardViewTest.class,
               JspViewTest.class,
               RawViewTest.class,
               ServerRedirectViewTest.class})
public class AllView {

}
