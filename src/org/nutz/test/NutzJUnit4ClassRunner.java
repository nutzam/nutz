package org.nutz.test;

import org.junit.internal.runners.InitializationError;
import org.junit.internal.runners.TestClassRunner;

public class NutzJUnit4ClassRunner extends TestClassRunner {

	public NutzJUnit4ClassRunner(final Class<?> klass) throws InitializationError {
		super(klass, new NutTestClassMethodsRunner(klass));
	}
}
