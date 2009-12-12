package org.nutz.aop.asm;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

import org.nutz.aop.AbstractClassAgent;
import org.nutz.aop.MethodInterceptor;
import org.nutz.aop.asm.org.asm.Opcodes;

/**
 * <b>本实现基于ASM 3.2</b>
 * <p/>相对父类的method,子类的method的方法签名会有所改变
 * <li>只有访问控制符public protected 和缺省
 * <li>native等标识符无法被继承.
 * <p/>若使用空的拦截器,如new AbstractMethodInterceptor(){},则子类的行为不变.
 * <p/><b>提醒:生成的Class默认为Java 1.6的类</b>
 * <p/><b>如果需要运行在Java 1.5上,请修好CLASS_LEVEL后编译. 该行为未被验证!</b>
 * @author wendal(wendal1985@gmail.com)
 * @see org.nutz.aop.AbstractClassAgent
 * @see org.nutz.aop.AbstractMethodInterceptor
 */
public class AsmClassAgent extends AbstractClassAgent {
	
	protected static final GeneratorClassLoader generatorClassLoader = new GeneratorClassLoader();
	
	public static final int CLASS_LEVEL = Opcodes.V1_6;

	@SuppressWarnings("unchecked")
	protected <T> Class<T> generate(Pair2 [] pair2s,String newName,Class<T> klass,Constructor<T> [] constructors) {
		try {
			return (Class<T>) generatorClassLoader.loadClass(newName);
		} catch (ClassNotFoundException e3) {
		}
		Method[] methodArray = new Method[pair2s.length];
		List<MethodInterceptor>[] methodInterceptorList = new List[pair2s.length];
		for (int i = 0; i < pair2s.length; i++) {
			Pair2 pair2 = pair2s[i];
			methodArray[i] = pair2.method;
			methodInterceptorList[i] = pair2.listeners;
		}
		Class<T> newClass = (Class<T>)generatorClassLoader.defineClassFromClassFile(newName, ClassX.enhandClass(klass, newName, methodArray,constructors));
		AopToolkit.injectFieldValue(newClass, methodArray, methodInterceptorList);
		return newClass ;
	}

}