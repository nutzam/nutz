package org.nutz.aop.asm;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

import org.nutz.aop.AbstractClassAgent;
import org.nutz.aop.ClassDefiner;
import org.nutz.aop.MethodInterceptor;
import org.nutz.aop.asm.org.asm.Opcodes;
import org.nutz.lang.Streams;

/**
 * <b>本实现基于ASM 3.2</b>
 * <p/>
 * 相对父类的method,子类的method的方法签名会有所改变
 * <li>只有访问控制符public protected 和缺省
 * <li>native等标识符无法被继承.
 * <p/>
 * 若使用空的拦截器,如new AbstractMethodInterceptor(){},则子类的行为不变.
 * <p/>
 * <b>提醒:如果本类的二进制代码为Java 1.5则,生成的Class为Java 1.5,相应的,如果是1.6,则生成1.6的Class数据</b>
 * <p/> 差异(一些流程相关的字节码): 
 * <li>org.asm.MethodVisitor.visitFrame 的调用
 * <li>StackMapTable or StackMap
 * 
 * <p/>在基本测试中,1.5和1.6均全部Pass.
 * 
 * @author wendal(wendal1985@gmail.com)
 * @see org.nutz.aop.AbstractClassAgent
 * @see org.nutz.aop.AbstractMethodInterceptor
 */
public class AsmClassAgent extends AbstractClassAgent {

	static int CLASS_LEVEL;

	static {
		// 判断编译等级
		InputStream is = null;
		try {
			String classFileName = AsmClassAgent.class.getName().replace('.', '/') + ".class";
			is = ClassLoader.getSystemResourceAsStream(classFileName);
			if (is != null && is.available() > 8) {
				is.skip(7);
				int major_version = is.read();
				switch (major_version) {
				case 49: // Java 1.5
					CLASS_LEVEL = Opcodes.V1_5;
					break;
				case 50: // Java 1.6
				default:
					CLASS_LEVEL = Opcodes.V1_6;
					break;
				}
			}
		} catch (Throwable e) {
			CLASS_LEVEL = Opcodes.V1_6;
		} finally {
			Streams.safeClose(is);
		}
	}

	@SuppressWarnings("unchecked")
	protected <T> Class<T> generate(ClassDefiner cd,
									Pair2[] pair2s,
									String newName,
									Class<T> klass,
									Constructor<T>[] constructors) {
		try {
			return (Class<T>) cd.load(newName);
		} catch (ClassNotFoundException e) {}
		Method[] methodArray = new Method[pair2s.length];
		List<MethodInterceptor>[] methodInterceptorList = new List[pair2s.length];
		for (int i = 0; i < pair2s.length; i++) {
			Pair2 pair2 = pair2s[i];
			methodArray[i] = pair2.method;
			methodInterceptorList[i] = pair2.listeners;
		}
		byte[] bytes = ClassX.enhandClass(klass, newName, methodArray, constructors);
		Class<T> newClass = (Class<T>) cd.define(newName, bytes);
		AopToolkit.injectFieldValue(newClass, methodArray, methodInterceptorList);
		return newClass;
	}

}