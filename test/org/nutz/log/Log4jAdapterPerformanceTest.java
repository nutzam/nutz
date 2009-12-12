package org.nutz.log;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.nutz.lang.Stopwatch;
import org.nutz.trans.Atom;

public class Log4jAdapterPerformanceTest {

	private String oldValue;
	private int times = 10000;
	private List<String> randomStrings1 = new ArrayList<String>(times);
	private List<String> randomStrings2 = new ArrayList<String>(times);

	private void info(Stopwatch sw, String type, String action) {
		System.out.printf("Log4j-Nutz>>%6s.%6s %4dms - %8dTimes\n", type, action, sw.getDuration(),
				times);
	}

	@Before
	public void setUp() throws Exception {
		oldValue = System.setProperty("log4j.configuration", "mylog4j.properties");

		for (int i = 0; i < times; i++)
			randomStrings1.add(Integer.toString((new Object()).hashCode()));

		for (int i = 0; i < times; i++)
			randomStrings2.add(Integer.toString((new Object()).hashCode()));
		LogFactory.getRootLog();
		Logger.getRootLogger();

	}

	@After
	public void tearDown() throws Exception {
		if (oldValue != null)
			System.setProperty("log4j.configuration", oldValue);
		else
			System.clearProperty("log4j.configuration");
	}

	@Test
	public void testLog4jCreation() {
		info(Stopwatch.run(new Atom() {

			public void run() {
				for (int i = 0; i < times; i++)
					LogManager.getLogger(randomStrings2.get(i));
			}

		}), "Log4j", "create");
	}

	@Test
	public void testNutzCreation() {
		info(Stopwatch.run(new Atom() {

			public void run() {
				for (int i = 0; i < times; i++)
					LogFactory.getLog(randomStrings1.get(i));
			}
		}), "Nutz", "create");
	}

	@Test
	public void testLog4jOutput() {
		final Logger log4jLogger = LogManager.getLogger("raw-log4j.performance2.test");
		info(Stopwatch.run(new Atom() {

			public void run() {
				for (int i = 0; i < times; i++)
					log4jLogger.info(randomStrings2.get(i));
			}

		}), "Log4j", "output");
	}

	@Test
	public void testNutzOutput() {
		final Log nutzLog = LogFactory.getLog("nutz-log4j.performance1.test");
		info(Stopwatch.run(new Atom() {

			public void run() {
				for (int i = 0; i < times; i++)
					nutzLog.info(randomStrings1.get(i));
			}

		}), "Nutz", "output");
	}
}
