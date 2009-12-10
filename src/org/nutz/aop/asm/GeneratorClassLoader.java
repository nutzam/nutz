/**
 * 
 */
package org.nutz.aop.asm;

class GeneratorClassLoader extends ClassLoader {
	
	Class<?> defineClassFromClassFile(String className, byte[] classFile) throws ClassFormatError {
		return defineClass(className, classFile, 0, classFile.length);
	}
}