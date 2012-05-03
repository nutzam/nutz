package org.nutz.aop.v2;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.nutz.aop.AbstractClassAgent;
import org.nutz.aop.AopCallback;
import org.nutz.aop.ClassDefiner;
import org.nutz.aop.v2.java.CP;
import org.nutz.aop.v2.java.JavaClass;
import org.nutz.aop.v2.java.JavaField;
import org.nutz.aop.v2.java.JavaMethod;
import org.nutz.aop.v2.java.JavaMethodCode;
import org.nutz.aop.v2.java.Type;

public class V2ClassAgent extends AbstractClassAgent {

	protected <T> Class<T> generate(ClassDefiner cd, Pair2[] pair2s,
			String newName, Class<T> klass, Constructor<T>[] constructors) {
		//创建类
		JavaClass javaClass = new JavaClass();
		CP cp = javaClass.cp;
		//设置类为public
		javaClass.access_flag = Modifier.PUBLIC;
		//设置当前类
		String myClassDescriptor = "L" + newName.replace('.', '/') + ";";
		javaClass.thisClass = cp.c_class(myClassDescriptor);
		//设置超类
		String superClassDescriptor = Type.descriptor(klass);
		javaClass.superClass = cp.c_class(superClassDescriptor);
		//添加AOP接口定义
		javaClass.interfaces.add(cp.c_class(Type.descriptor(AopCallback.class)));
		//添加字段
		List<AopGod> gods = new ArrayList<AopGod>();
		for (int i = 0; i < pair2s.length; i++) {
			Pair2 pair2 = pair2s[i];
			AopGod god = new AopGod();
			god.index = i;
			god.method = pair2.method;
			god.interceptors = pair2.listeners;
			gods.add(god);
			JavaField field = new JavaField();
			field.access_flag = Modifier.PUBLIC + Modifier.STATIC;
			field.name = cp.c_utf8("_aop_" + i);
			field.descriptor = cp.c_utf8(Type.descriptor(AopGod.class));
			javaClass.fields.add(field);
		}
		//添加构造方法
		for(Constructor<?> constructor : constructors)
			addConstructor(javaClass, myClassDescriptor, superClassDescriptor, constructor);
		
		//添加普通方法
		for (int i = 0; i < pair2s.length; i++) {
			Pair2 pair2 = pair2s[i];
			addMethod(javaClass, myClassDescriptor, superClassDescriptor, pair2.method);
		}
				
		//添加AOP Invoke方法
		addAopInvokeMethod(javaClass, myClassDescriptor, superClassDescriptor);
		
		//由ClassDefiner生成类对象
		
		//设置类静态变量的属性
		return null;
	}

	public void addConstructor(JavaClass javaClass, String myClassDescriptor,
			String superClassDescriptor, Constructor<?> constructor) {
		JavaMethod javaMethod = new JavaMethod();
		CP cp = javaClass.cp;
		javaMethod.access_flag = constructor.getModifiers();
		javaMethod.name = cp.c_utf8("<init>");
		javaMethod.descriptor = cp.c_utf8(Type.descriptor(constructor));
		
		//添加代码属性
		JavaMethodCode code = new JavaMethodCode();
		javaMethod.attributes.add(code);
		
		code.cp = cp;
		//开始悲催的Java字节码
		
	}

	public void addMethod(JavaClass javaClass,String myClassDescriptor,
			String superClassDescriptor, Method method) {
		
	}
	
	public void addAopInvokeMethod(JavaClass javaClass,String myClassDescriptor,
			String superClassDescriptor) {
		
	}
}
