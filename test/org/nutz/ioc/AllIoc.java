package org.nutz.ioc;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.nutz.ioc.aop.impl.XmlFileMirrorFactoryTest;
import org.nutz.ioc.java.ChainParsingTest;
import org.nutz.ioc.json.AllJsonIoc;
import org.nutz.ioc.loader.xml.XmlIocLoaderTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({ChainParsingTest.class, 
	                 AllJsonIoc.class,
	                 XmlIocLoaderTest.class,
	                 XmlFileMirrorFactoryTest.class})
public class AllIoc {}
