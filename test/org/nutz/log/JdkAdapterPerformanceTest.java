package org.nutz.log;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.nutz.lang.Stopwatch;
import org.nutz.lang.random.StringGenerator;
import org.nutz.trans.Atom;

public class JdkAdapterPerformanceTest {

	private String oldValue;
	private int times = 10000;
	private List<String> randomStrings1 = new ArrayList<String>(times);
	private List<String> randomStrings2 = new ArrayList<String>(times);

	private void info(Stopwatch sw, String type, String action) {
		System.out.printf("JDK-Nutz  >>%6s.%6s %4dms - %8dTimes\n", type, action, sw.getDuration(),
				times);
	}

	@Before
	public void setUp() throws Exception {
		URL url = getClass().getClassLoader().getResource("myLogging.properties");

		oldValue = System.setProperty("java.util.logging.config.file", url.getFile());

		StringGenerator sg = new StringGenerator(40);

		for (int i = 0; i < times; i++)
			randomStrings1.add(sg.next());

		for (int i = 0; i < times; i++)
			randomStrings2.add(sg.next());

	}

	@After
	public void tearDown() throws Exception {
		if (oldValue != null)
			System.setProperty("log4j.configuration", oldValue);
		else
			System.clearProperty("log4j.configuration");
	}

	@Test
	public void testJdkCreation() {
		info(Stopwatch.run(new Atom() {
			public void run() {
				for (int i = 0; i < times; i++)
					Logger.getLogger(randomStrings2.get(i));
			}
		}), "JDK", "create");
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
	public void testJdkOutput() {
		final Logger jdkLogger = Logger.getLogger("raw-jdk.performance2.test");
		info(Stopwatch.run(new Atom() {
			public void run() {
				for (int i = 0; i < times; i++)
					jdkLogger.log(Level.INFO, randomStrings1.get(i));
			}
		}), "JDK", "output");
	}

	@Test
	public void testNutzOutput() {
		final Log nutzLog = LogFactory.getLog("nutz-jdk.performance1.test");
		info(Stopwatch.run(new Atom() {
			public void run() {
				for (int i = 0; i < times; i++)
					nutzLog.info(randomStrings1.get(i));
			}
		}), "Nutz", "output");
	}

}
