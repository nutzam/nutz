package com.zzh.dao.test;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;

import static com.zzh.lang.Printer.*;

public class DatabaseTestRunner extends Runner {

	public DatabaseTestRunner(Class<?> testClass) {
		super();
	}

	@Override
	public Description getDescription() {
		return Description.createSuiteDescription("Nutz Database relative testing");
	}

	@Override
	public void run(RunNotifier rn) {
		println("before");
		
		println("end");
	}

}
