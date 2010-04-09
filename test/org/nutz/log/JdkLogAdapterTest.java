package org.nutz.log;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.nutz.log.impl.JdkLogAdapter;

public class JdkLogAdapterTest {
	
	@Before
	public void init(){
		//使用Jre默认的logging配置
		System.getProperties().put("java.util.logging.config.file", "logging.properties");
		System.getProperties().put("log4j.defaultInitOverride", "xx");
		Logs.init();
	}
	
	@After
	public void clean(){
		System.getProperties().remove("java.util.logging.config.file");
	}

	@Test
	public void testCanWork() {
		assertTrue(new JdkLogAdapter().canWork());
	}

	@Test
	public void testGetLogger() {
		Log logA = new JdkLogAdapter().getLogger(Log.class.getName());
		assertNotNull(logA);
		assertFalse(logA.isTraceEnabled());
		assertTrue(logA.isErrorEnabled());
	}

	@Test
	public void testLogAdapter() {
		Log logA = Logs.getLog(Log.class);
		assertNotNull(logA);
		assertTrue(logA.getClass().getName().contains(JdkLogAdapter.class.getName()));
		assertFalse(logA.isTraceEnabled());
		assertTrue(logA.isErrorEnabled());
	}
}
