package org.nutz.ioc.loader;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.nutz.ioc.loader.annotation.AnnotationIocLoaderTest;
import org.nutz.ioc.loader.xml.XmlIocLoaderTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({XmlIocLoaderTest.class, AnnotationIocLoaderTest.class})
public class AllLoader {

}
