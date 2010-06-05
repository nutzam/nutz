package org.nutz.ioc;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.nutz.ioc.aop.config.impl.AllAopConfigration;
import org.nutz.ioc.java.ChainParsingTest;
import org.nutz.ioc.json.AllJsonIoc;
import org.nutz.ioc.loader.annotation.AnnotationIocLoaderTest;
import org.nutz.ioc.loader.xml.XmlIocLoaderTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({ChainParsingTest.class, 
	                 AllJsonIoc.class,
	                 XmlIocLoaderTest.class,
	                 AnnotationIocLoaderTest.class,
	                 AllAopConfigration.class})
public class AllIoc {}
