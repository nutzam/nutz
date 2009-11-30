package org.nutz.log;

import junit.framework.Assert;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.nutz.log.Log;
import org.nutz.log.LogFactory;
import org.nutz.log.helper.TestClassLoader;
import org.nutz.log.helper.log4j.TestAppender;
import org.nutz.log.impl.Log4jAdapter;

public class Log4jAdapterTest {

	private String oldValue;

	@Before
	public void init() {
		oldValue = System.setProperty("log4j.configuration", "mylog4j.properties");
	}

	@After
	public void cleanUp() {
		if (oldValue != null)
			System.setProperty("log4j.configuration", oldValue);
		else
			System.clearProperty("log4j.configuration");
	}

	@Test
	public void testLog4jAlone() {
		Logger log = LogManager.getLogger("abc");

		log.info("test info message");
	}

	/**
	 * verify log4jAdapter can work in a common manner.
	 */
	@Test
	public void testCommonManner() {

		Log log = LogFactory.getLog("abc");

		log.info("test message");

	}

	/**
	 * verify log4jAdapter can handle system property
	 * "log4j.defaultInitOverride" properly.
	 */
	@Test
	public void testProperty_defaultInitOverride() {

		Log4jAdapter adapter = new Log4jAdapter();

		String oldValue = System.setProperty("log4j.defaultInitOverride", "abc");

		Assert.assertFalse(adapter.canWork());

		if (oldValue != null)
			System.setProperty("log4j.defaultInitOverride", oldValue);
		else
			System.clearProperty("log4j.defaultInitOverride");
	}

	/**
	 * Verify log4jAdapter can handle system property "log4j.configuration"
	 * properly.
	 */
	@Test
	public void testProperty_configuration() {

		Log4jAdapter adapter = new Log4jAdapter();

		String oldOverrideValue = System.clearProperty("log4j.defaultInitOverride");

		String oldValue = System.setProperty("log4j.configuration", "abc");

		Assert.assertTrue(adapter.canWork());

		if (oldValue != null)
			System.setProperty("log4j.configuration", oldValue);
		else
			System.clearProperty("log4j.configuration");

		if (oldOverrideValue != null)
			System.setProperty("log4j.defaultInitOverride", oldOverrideValue);
		else
			System.clearProperty("log4j.defaultInitOverride");
	}

	/**
	 * Verify log4jAadapter works if default configure file found.
	 * 
	 * we use a test class loader to control default configure file searching
	 * result.
	 */
	@Test
	public void testDefaultPropertiesFile() {

		// prepare context...
		String oldOverrideValue = System.clearProperty("log4j.defaultInitOverride");

		String oldConfigValue = System.clearProperty("log4j.configuration");

		ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();

		TestClassLoader classLoader = new TestClassLoader(oldClassLoader);

		Thread.currentThread().setContextClassLoader(classLoader);

		classLoader.setCanWeFoundLog4jDefaultProperties(false);

		Log4jAdapter adapter = new Log4jAdapter();

		// begin test...

		classLoader.setCanWeFoundLog4jDefaultProperties(true);

		Assert.assertTrue(adapter.canWork());

		// restore context...
		Thread.currentThread().setContextClassLoader(oldClassLoader);

		if (oldOverrideValue != null)
			System.setProperty("log4j.defaultInitOverride", oldOverrideValue);

		if (oldConfigValue != null)
			System.setProperty("log4j.configuration", oldConfigValue);
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testLevelAndMessage() {

		Log log = LogFactory.getLog("abc");

		String message = "test err message";
		log.error(message);

		Assert.assertEquals(Priority.ERROR, TestAppender.getLastEvent().level);

		Assert.assertEquals(message, TestAppender.getLastEvent().getMessage());

		message = "warn message";
		Exception e = new Exception();

		log.warn(message, e);

		Assert.assertEquals(Priority.WARN, TestAppender.getLastEvent().level);
		Assert.assertEquals(message, TestAppender.getLastEvent().getMessage());
		Assert.assertEquals(e, TestAppender.getLastEvent().getThrowableInformation().getThrowable());
	}
	
	@Test
	public void testLevel() {
		
		Log log = LogFactory.getLog("org.nutz.log.level.fatal");
		
		Assert.assertTrue(log.isFatalEnabled());
		Assert.assertFalse(log.isErrorEnabled());
		
		log = LogFactory.getLog("org.nutz.log.level.error");
		
		Assert.assertTrue(log.isFatalEnabled());
		Assert.assertTrue(log.isErrorEnabled());
		Assert.assertFalse(log.isWarnEnabled());
		
		log = LogFactory.getLog("org.nutz.log.level.warn");
		
		Assert.assertTrue(log.isFatalEnabled());
		Assert.assertTrue(log.isErrorEnabled());
		Assert.assertTrue(log.isWarnEnabled());
		
		log = LogFactory.getLog("org.nutz.log.level.info");
		
		Assert.assertTrue(log.isWarnEnabled());
		Assert.assertTrue(log.isInfoEnabled());
		Assert.assertFalse(log.isDebugEnabled());
		
		log = LogFactory.getLog("org.nutz.log.level.debug");
		
		Assert.assertTrue(log.isInfoEnabled());
		Assert.assertTrue(log.isDebugEnabled());
		Assert.assertFalse(log.isTraceEnabled());
		
		log = LogFactory.getLog("org.nutz.log.level.trace");
		
		Assert.assertTrue(log.isDebugEnabled());
		Assert.assertTrue(log.isTraceEnabled());
	}
}
