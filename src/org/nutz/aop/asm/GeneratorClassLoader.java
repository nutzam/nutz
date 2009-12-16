/**
 * 
 */
package org.nutz.aop.asm;

/**
 * @author wendal(wendal1985@gmail.com)
 */
class GeneratorClassLoader extends ClassLoader {
	
	Class<?> defineClassFromClassFile(String className, byte[] classFile) throws ClassFormatError {
		return defineClass(className, classFile, 0, classFile.length);
	}
}