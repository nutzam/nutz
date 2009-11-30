package org.nutz.log;

import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.nutz.log.Log;
import org.nutz.log.LogFactory;
import org.nutz.log.helper.jdkLogger.TestHandler;
import org.nutz.log.impl.JdkLoggerAdapter;

public class JDKloggerAdapterTest {

	private String oldValue;
	private String oldProperty;

	@Before
	public void init() {
		URL url = getClass().getClassLoader().getResource("myLogging.properties");

		oldValue = System.setProperty("java.util.logging.config.file", url.getFile());

		oldProperty = System.getProperty("log4j.defaultInitOverride");
		System.setProperty("log4j.defaultInitOverride", "true");

	}

	@After
	public void finishup() {
		if (null != oldProperty)
			System.setProperty("log4j.defaultInitOverride", oldProperty);
		else
			System.clearProperty("log4j.defaultInitOverride");
		
		if (oldValue != null)
			System.setProperty("java.util.logging.config.file", oldValue);
		else
			System.clearProperty("java.util.logging.config.file");

	}

	/**
	 * 测试Adapter判断是否使用jdk logger的逻辑是否正确
	 */
	@Test
	public void testConfig() {

		JdkLoggerAdapter log = new JdkLoggerAdapter();

		System.clearProperty("java.util.logging.config.class");
		System.clearProperty("java.util.logging.config.file");

		Assert.assertFalse(log.canWork());

		System.setProperty("java.util.logging.config.file", "abc");

		Assert.assertTrue(log.canWork());

		System.setProperty("java.util.logging.config.class",
				"org.nutz.testing.helper.jdkLogger.MyConfigClass");
		System.clearProperty("java.util.logging.config.file");

		Assert.assertTrue(log.canWork());

		System.clearProperty("java.util.logging.config.class");
	}

	@Test
	public void testCommonManner() {

		Log log = LogFactory.getLog(getClass().getName());

		log.fatal("test fatal message");

		log.trace("test trace message");

	}

	/**
	 * 对于同一名字，adapter获得的logger和直接获得的logger是同一对象
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void testLoggerIdentity() {
		Logger jdkLogger = Logger.getLogger("abc");

		Log log = LogFactory.getLog("abc");

		Logger innerLog = ((JdkLoggerAdapter) log).getJdkLogger();
		Assert.assertEquals(jdkLogger, innerLog);
	}

	@Test
	public void testLevel() {
		Logger jdkLogger = Logger.getLogger("abc");

		Level oldLevel = jdkLogger.getLevel();

		jdkLogger.setLevel(JdkLoggerAdapter.DEBUG_LEVEL);

		Log log = LogFactory.getLog("abc");

		Assert.assertTrue(log.isDebugEnabled());
		Assert.assertFalse(log.isTraceEnabled());

		jdkLogger.setLevel(Level.OFF);

		log = LogFactory.getLog("abc");

		Assert.assertFalse(log.isWarnEnabled());
		Assert.assertFalse(log.isFatalEnabled());

		jdkLogger.setLevel(oldLevel);
	}

	@Test
	public void testMessage() {

		Logger jdkLogger = Logger.getLogger("abc");

		Level oldLevel = jdkLogger.getLevel();

		Log log = LogFactory.getLog("abc");

		String message = "test error message";
		log.error(message);

		Assert.assertEquals(message, TestHandler.lastLogRecord.getMessage());
		Assert.assertEquals(JdkLoggerAdapter.ERROR_LEVEL, TestHandler.lastLogRecord.getLevel());

		message = "test warn message";
		Exception e = new Exception();

		log.warn(message, e);

		Assert.assertEquals(message, TestHandler.lastLogRecord.getMessage());
		Assert.assertEquals(e, TestHandler.lastLogRecord.getThrown());
		Assert.assertEquals(JdkLoggerAdapter.WARN_LEVEL, TestHandler.lastLogRecord.getLevel());

		jdkLogger.setLevel(oldLevel);
	}
}
