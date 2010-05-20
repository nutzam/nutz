package org.nutz.aop.asm;

import java.lang.reflect.Method;
import java.util.List;

import org.nutz.aop.MethodInterceptor;
import org.nutz.aop.asm.org.asm.ClassVisitor;
import org.nutz.aop.asm.org.asm.Opcodes;
import org.nutz.lang.Mirror;

public class AopToolKit implements Opcodes {

	public static final String MethodArray_FieldName = "_$$Nut_methodArray";
	public static final String MethodInterceptorList_FieldName = "_$$Nut_methodInterceptorList";

	public static <T> void injectFieldValue(Class<T> newClass,
											Method[] methodArray,
											List<MethodInterceptor>[] methodInterceptorList) {
		try {
			Mirror<T> mirror = Mirror.me(newClass);
			mirror.setValue(null, MethodArray_FieldName, methodArray);
			mirror.setValue(null, MethodInterceptorList_FieldName, methodInterceptorList);
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public static void addFields(ClassVisitor cv) {
		addMethodArrayFiled(cv);
		addMethodInterceptorListField(cv);
	}

	static void addMethodArrayFiled(ClassVisitor cv) {
		cv.visitField(	ACC_PRIVATE + ACC_STATIC,
						MethodArray_FieldName,
						"[Ljava/lang/reflect/Method;",
						null,
						null).visitEnd();
	}

	static void addMethodInterceptorListField(ClassVisitor cv) {
		cv.visitField(	ACC_PRIVATE + ACC_STATIC,
						MethodInterceptorList_FieldName,
						"[Ljava/util/List;",
						"[Ljava/util/List<Lorg/nutz/aop/MethodInterceptor;>;",
						null).visitEnd();
	}

}
