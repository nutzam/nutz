package org.nutz.lang.reflect.impl;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nutz.aop.AopCallback;
import org.nutz.aop.ClassDefiner;
import org.nutz.aop.DefaultClassDefiner;
import org.nutz.aop.asm.AsmHelper;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.reflect.FastBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.repo.org.objectweb.asm.ClassWriter;
import org.nutz.repo.org.objectweb.asm.Label;
import org.nutz.repo.org.objectweb.asm.MethodVisitor;
import org.nutz.repo.org.objectweb.asm.Opcodes;
import org.nutz.repo.org.objectweb.asm.Type;

public final class FastBeanFactory implements Opcodes {

	private ClassDefiner definer = new DefaultClassDefiner(FastBeanFactory.class.getClassLoader());
	private static final Log logger = Logs.getLog(FastBeanFactory.class);
	
	private Map<Class<?>, FastBean> beans = new HashMap<Class<?>, FastBean>();
	
	public FastBean get(Class<?> klass){
		if (AopCallback.class.isAssignableFrom(klass))
			klass = klass.getSuperclass();
		FastBean bean = beans.get(klass);
		if (bean == null) {
			bean = makeFastBean(klass);
		}
		return bean;
	}
	
	public void reset(){
		definer = new DefaultClassDefiner(FastBeanFactory.class.getClassLoader());
		beans = new HashMap<Class<?>, FastBean>();
	}
	
	protected FastBean makeFastBean(Class<?> klass) {
		Method[] methods = klass.getMethods();
		//获取类的信息
		FastBeanInfo info = new FastBeanInfo();
		for (Method method : methods) {
			if (Modifier.isStatic(method.getModifiers()))
				continue;
			//是否为getter
			String name = method.getName();
			if (name.startsWith("get") || name.startsWith("is")){
				if (method.getParameterTypes().length == 0)
					if (!"void".equals(method.getReturnType().toString()))
						info.getters.add(method);
			} else if (name.startsWith("set")){//是否为setter
				if (method.getParameterTypes().length == 1)
					if ("void".equals(method.getReturnType().toString()))
						info.setters.add(method);
			}
		}
		if (logger.isInfoEnabled()) {
			logger.infof("Found %d getter for class %s",info.getters.size(),klass);
			logger.infof("Found %d setter for class %s",info.setters.size(),klass);
		}
		try {
			klass.getConstructor();
			info.hasDefaultConstructor = true;
		} catch (Throwable e) {
		}
		//开始构建Class字节码
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		String fastBeanMeClassName = klass.getName().replace('.', '/')+"FastBean";
		String fastBeanClassName = FastBean.class.getName().replace('.', '/');
		String klassName = klass.getName().replace('.', '/');
		cw.visit(FastBeanInfo.LEVEL, ACC_PUBLIC + ACC_SUPER, 
				fastBeanMeClassName, null, 
				fastBeanClassName, null);
		//添加FastBean的默认构造方法
		MethodVisitor mv;
		{
			mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
			mv.visitCode();
			mv.visitVarInsn(ALOAD, 0);
			mv.visitMethodInsn(INVOKESPECIAL, "org/nutz/lang/reflect/FastBean", "<init>", "()V");
			mv.visitInsn(RETURN);
			mv.visitMaxs(1, 1);
			mv.visitEnd();
		}
		//看看是否需要覆写FastBean的_newInstance方法
		if (info.hasDefaultConstructor){
			mv = cw.visitMethod(ACC_PUBLIC, "_newInstance", "()Ljava/lang/Object;", null, new String[] { "java/lang/Throwable" });
			mv.visitCode();
			mv.visitTypeInsn(NEW, klassName);
			mv.visitInsn(DUP);
			mv.visitMethodInsn(INVOKESPECIAL, klassName, "<init>", "()V");
			mv.visitInsn(ARETURN);
			mv.visitMaxs(2, 1);
			mv.visitEnd();
		}
		//构建_getter
		{
			mv = cw.visitMethod(ACC_PROTECTED, "_getter", "(Ljava/lang/Object;I)Ljava/lang/Object;", null, new String[] { "java/lang/Throwable" });
			mv.visitCode();
			for (Method getter : info.getters) {
			
				mv.visitVarInsn(ILOAD, 2);
				mv.visitLdcInsn(Integer.valueOf(hashcode(getter)));
				Label l0 = new Label();
				mv.visitJumpInsn(IF_ICMPNE, l0);
				mv.visitVarInsn(ALOAD, 1);
				mv.visitTypeInsn(CHECKCAST, klassName);
				mv.visitMethodInsn(INVOKEVIRTUAL, klassName, getter.getName(), Type.getMethodDescriptor(getter));
				AsmHelper.packagePrivateData(Type.getReturnType(getter), mv);
				mv.visitInsn(ARETURN);
				mv.visitLabel(l0);
				mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
			}
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitVarInsn(ILOAD, 2);
			mv.visitMethodInsn(INVOKESPECIAL, "org/nutz/lang/reflect/FastBean", "_getter", "(Ljava/lang/Object;I)Ljava/lang/Object;");
			mv.visitInsn(ARETURN);
			mv.visitMaxs(3, 3);
			mv.visitEnd();
		}
		//构建_setter
		if (info.setters.size() > 0) {
			mv = cw.visitMethod(ACC_PROTECTED, "_setter", "(Ljava/lang/Object;ILjava/lang/Object;)V", null, new String[] { "java/lang/Throwable" });
			mv.visitCode();
			for (Method setter : info.setters) {
				mv.visitVarInsn(ILOAD, 2);
				mv.visitLdcInsn(Integer.valueOf(hashcode(setter)));
				Label l0 = new Label();
				mv.visitJumpInsn(IF_ICMPNE, l0);
				mv.visitVarInsn(ALOAD, 1);
				mv.visitTypeInsn(CHECKCAST, klassName);
				mv.visitVarInsn(ALOAD, 3);
				AsmHelper.checkCast(Type.getArgumentTypes(setter)[0], mv);
				mv.visitMethodInsn(INVOKEVIRTUAL, klassName, setter.getName(), Type.getMethodDescriptor(setter));
				mv.visitInsn(RETURN);
				mv.visitLabel(l0);
				mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
			}
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitVarInsn(ILOAD, 2);
			mv.visitVarInsn(ALOAD, 3);
			mv.visitMethodInsn(INVOKESPECIAL, "org/nutz/lang/reflect/FastBean", "_setter", "(Ljava/lang/Object;ILjava/lang/Object;)V");
			mv.visitInsn(RETURN);
			mv.visitMaxs(4, 4);
			mv.visitEnd();
		}
		try {
			return (FastBean)definer.define(fastBeanMeClassName.replace('/', '.'), cw.toByteArray()).newInstance();
		} catch (Throwable e) {
			throw Lang.wrapThrow(e);
		}
	}
	
	static class FastBeanInfo {
		List<Method> getters = new ArrayList<Method>();
		List<Method> setters = new ArrayList<Method>();
		private boolean hasDefaultConstructor;
		private static final int LEVEL = Lang.isJDK6() ? V1_6 : V1_5;
	}
	
	protected int hashcode(Method method) {
		String name = method.getName(); //必然以is/get/set,故仅需要判断截断的位置
		int pos = name.startsWith("is") ? 2 : 3;
		//看看有没有真正的字段名
		if (name.length() > pos) {
			//System.out.println(name +" --> " + Strings.lowerFirst(name.substring(pos)).hashCode());
			return Strings.lowerFirst(name.substring(pos)).hashCode();
		}
		return "".hashCode();
	}
}
