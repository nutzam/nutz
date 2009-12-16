package org.nutz.aop.asm;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.nutz.aop.asm.org.asm.ClassWriter;
import org.nutz.aop.asm.org.asm.MethodVisitor;
import org.nutz.aop.asm.org.asm.Opcodes;
import org.nutz.aop.asm.org.asm.Type;

/**
 * 通过Asm生成字节码
 * @author Wendal(wendal1985@gmail.com)
 */
public final class ClassX<T> implements Opcodes{
	
	private Class<T> klass;
	
	private ClassWriter cw;
	
	private String myName;
	
	private String enhancedSuperName;
	
	private Method[] methodArray ;
	
	private Constructor<T> [] constructors;
	
	protected ClassX(Class<T> kclass,String myName,Method[] methodArray,Constructor<T> [] constructors){
		this.klass = kclass;
		this.myName = myName.replace('.', '/');
		this.enhancedSuperName = klass.getName().replace('.', '/');
		this.cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		cw.visit(AsmClassAgent.CLASS_LEVEL, ACC_PUBLIC, this.myName, "", enhancedSuperName, new String[]{});
		this.methodArray = methodArray;
		this.constructors = constructors;
	}
	
	protected void addField() {
		AopToolkit.addFields(cw);
	}
	
	protected void addConstructors(){
		for (Constructor<T> constructor : constructors) {
			String [] expClasses = convertExp(constructor.getExceptionTypes());
			String desc = Type.getConstructorDescriptor(constructor);
			int access = getAccess(constructor.getModifiers());
			MethodVisitor mv = cw.visitMethod(access, "<init>", desc,null, expClasses);
			new ChangeToChildConstructorMethodAdapter(mv,desc,access,enhancedSuperName).visitCode();
		}
	}
	
	private String [] convertExp(Class<?> [] expClasses){
		if(expClasses.length == 0) return null;
		String [] results = new String[expClasses.length];
		for (int i = 0; i < results.length; i++)
			results[i] = expClasses[i].getName().replace('.', '/');
		return results;
	}
	
	protected void addAopMethods() {
		AopToolkit.addMethods(cw, myName);
	}
	
	protected void enhandMethod() {
		for (Method method : methodArray) {
			String methodName = method.getName();
			String methodDesc = Type.getMethodDescriptor(method);
			int methodAccess = getAccess(method.getModifiers());
			MethodVisitor mv = cw.visitMethod(methodAccess, methodName, 
					methodDesc,null, convertExp(method.getExceptionTypes()));
			int methodIndex = findMethodIndex(methodName, methodDesc, methodArray);
			new AopMethodAdapter(mv,methodAccess,methodName,
					methodDesc,methodIndex,
					myName,enhancedSuperName).visitCode();
		}
	}
	
	protected int getAccess(int modify) {
		if(Modifier.isProtected(modify))
			return ACC_PROTECTED;
		if(Modifier.isPublic(modify))
			return ACC_PUBLIC;
		return 0x00;
	}
	
	protected static int findMethodIndex(String name, String desc, Method[] methods) {
		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			if (Type.getMethodDescriptor(method).equals(desc) && method.getName().equals(name))
				return i;
		}
		return -1;//是否应该抛出异常呢?应该不可能发生的
	}

	protected byte[] toByteArray(){
		addField();
		addConstructors();
		addAopMethods();
		enhandMethod();
		return cw.toByteArray();
	}
	
	@SuppressWarnings("unchecked")
	public static <T> byte [] enhandClass(Class<T> kclass,String myName,Method[] methodArray,Constructor<?> [] constructors){
		return new ClassX<T>(kclass,myName,methodArray,(Constructor<T> [])constructors).toByteArray();
	}
	
}
