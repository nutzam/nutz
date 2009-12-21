package org.nutz.aop;

public class DefaultClassDefiner extends ClassLoader implements ClassDefiner {
	
	public Class<?> define(String className, byte[] bytes) throws ClassFormatError {
		try {
			return this.loadClass(className);
		} catch (ClassNotFoundException e) {}
		// If not found ...
		return defineClass(className, bytes, 0, bytes.length);
	}

	public boolean has(String className) {
		try {
			load(className);
			return true;
		} catch (ClassNotFoundException e) {}
		return false;
	}

	public Class<?> load(String className) throws ClassNotFoundException {
		return this.loadClass(className);
	}
}
