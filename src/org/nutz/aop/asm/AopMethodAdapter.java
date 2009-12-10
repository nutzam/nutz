package org.nutz.aop.asm;

import static org.objectweb.asm.Opcodes.*;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

final class AopMethodAdapter extends NullMethodAdapter {
	private int methodIndex;

	private String myName;

	private String enhancedSuperName;

	private String methodName;
	
	private Type returnType;

	public AopMethodAdapter(MethodVisitor mv, int access,String methodName,String desc,
			int methodIndex, String myName, String enhancedSuperName) {
		super(mv,desc,access);
		this.methodIndex = methodIndex;
		this.myName = myName;
		this.enhancedSuperName = enhancedSuperName;
		this.methodName = methodName;
		this.returnType = Type.getReturnType(desc);
	}

	public void visitCode() {
		if(Type.getReturnType(desc).equals(Type.VOID_TYPE)){
			enhandMethod_Void();
		}else{
			enhandMethod_Object();
		}
	}
	
	private void enhandMethod_Void() {
		int lastIndex = getLastIndex();
		
		mv.visitCode();
		Label l0 = new Label();
		Label l1 = new Label();
		Label l2 = new Label();
		mv.visitTryCatchBlock(l0, l1, l2, "java/lang/Exception");
		Label l3 = new Label();
		mv.visitTryCatchBlock(l0, l1, l3, "java/lang/Throwable");
		mv.visitLabel(l0);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitIntInsn(SIPUSH, methodIndex);
		loadArgsAsArray();
		mv.visitMethodInsn(INVOKESPECIAL, myName, "_Nut_before", "(I[Ljava/lang/Object;)Z");
		Label l4 = new Label();
		mv.visitJumpInsn(IFEQ, l4);
		mv.visitVarInsn(ALOAD, 0);
		loadArgs();
		mv.visitMethodInsn(INVOKESPECIAL, enhancedSuperName, methodName, desc);
		mv.visitLabel(l4);
		mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitIntInsn(SIPUSH, methodIndex);
		mv.visitInsn(ACONST_NULL);
		loadArgsAsArray();
		mv.visitMethodInsn(INVOKESPECIAL, myName, "_Nut_after", "(ILjava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;");
		mv.visitInsn(POP);
		mv.visitLabel(l1);
		Label l5 = new Label();
		mv.visitJumpInsn(GOTO, l5);
		mv.visitLabel(l2);
		mv.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[] {"java/lang/Exception"});
		mv.visitVarInsn(ASTORE, lastIndex);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitIntInsn(SIPUSH, methodIndex);
		mv.visitVarInsn(ALOAD, lastIndex);
		loadArgsAsArray();
		mv.visitMethodInsn(INVOKESPECIAL, myName, "_Nut_Exception", "(ILjava/lang/Exception;[Ljava/lang/Object;)Z");
		mv.visitJumpInsn(IFEQ, l5);
		mv.visitVarInsn(ALOAD, lastIndex);
		mv.visitInsn(ATHROW);
		mv.visitLabel(l3);
		mv.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[] {"java/lang/Throwable"});
		mv.visitVarInsn(ASTORE, lastIndex);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitIntInsn(SIPUSH, methodIndex);
		mv.visitVarInsn(ALOAD, lastIndex);
		loadArgsAsArray();
		mv.visitMethodInsn(INVOKESPECIAL, myName, "_Nut_Error", "(ILjava/lang/Throwable;[Ljava/lang/Object;)Z");
		mv.visitJumpInsn(IFEQ, l5);
		mv.visitVarInsn(ALOAD, lastIndex);
		mv.visitInsn(ATHROW);
		mv.visitLabel(l5);
		mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
		mv.visitInsn(RETURN);
		mv.visitMaxs(1, 1); // 自动计算
		mv.visitEnd();
	}

	void loadArgsAsArray(){
		visitX(argumentTypes.length);
		mv.visitTypeInsn(ANEWARRAY, "java/lang/Object");
		int index = getArgIndex(0);
		for (int i = 0; i < argumentTypes.length; i++) {
			mv.visitInsn(DUP);
			visitX(i);
			Type t = argumentTypes[i];
	        loadInsn(t, index);
	        index += t.getSize();
	        packagePrivateData(t);
			mv.visitInsn(AASTORE);
		}
	}
	
	int getLastIndex(){
		int index = getArgIndex(0);
		for (int i = 0; i < argumentTypes.length; i++) {
			Type t = argumentTypes[i];
	        index += t.getSize();
		}
		return index;
	}
	
	void visitX(int i){
		if(i < 6){
			mv.visitInsn(i + ICONST_0);
		}else{
			mv.visitIntInsn(BIPUSH, i);
		}
	}
	
	boolean packagePrivateData(Type type){
		if(type.equals(Type.BOOLEAN_TYPE)){
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;");
		}else if(type.equals(Type.BYTE_TYPE)){
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;");
		}else if(type.equals(Type.CHAR_TYPE)){
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;");
		}else if(type.equals(Type.SHORT_TYPE)){
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;");
		}else if(type.equals(Type.INT_TYPE)){
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
		}else if(type.equals(Type.LONG_TYPE)){
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;");
		}else if(type.equals(Type.FLOAT_TYPE)){
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;");
		}else if(type.equals(Type.DOUBLE_TYPE)){
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;");
		}else{
			return false;
		}
		return true;
	}
	
	private void enhandMethod_Object() {
		int lastIndex = getLastIndex();
		
		mv.visitCode();
		Label l0 = new Label();
		Label l1 = new Label();
		Label l2 = new Label();
		mv.visitTryCatchBlock(l0, l1, l2, "java/lang/Exception");
		Label l3 = new Label();
		mv.visitTryCatchBlock(l0, l1, l3, "java/lang/Throwable");
		mv.visitLabel(l0);
		mv.visitInsn(ACONST_NULL);
		mv.visitVarInsn(ASTORE, lastIndex);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitIntInsn(SIPUSH, methodIndex);
		loadArgsAsArray();
		mv.visitMethodInsn(INVOKESPECIAL, myName, "_Nut_before", "(I[Ljava/lang/Object;)Z");
		Label l4 = new Label();
		mv.visitJumpInsn(IFEQ, l4);
		mv.visitVarInsn(ALOAD, 0);
		loadArgs();
		mv.visitMethodInsn(INVOKESPECIAL, enhancedSuperName, methodName, desc);
		packagePrivateData(returnType);
		mv.visitVarInsn(ASTORE, lastIndex);
		mv.visitLabel(l4);
		mv.visitFrame(Opcodes.F_APPEND,1, new Object[] {"java/lang/Object"}, 0, null);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitIntInsn(SIPUSH, methodIndex);
		mv.visitVarInsn(ALOAD, lastIndex);
		loadArgsAsArray();
		mv.visitMethodInsn(INVOKESPECIAL, myName, "_Nut_after", "(ILjava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;");
		checkCast();
		mv.visitLabel(l1);
		returnIt();
		mv.visitLabel(l2);
		mv.visitFrame(Opcodes.F_FULL, 1, new Object[] {myName}, 1, new Object[] {"java/lang/Exception"});
		mv.visitVarInsn(ASTORE, lastIndex);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitIntInsn(SIPUSH, methodIndex);
		mv.visitVarInsn(ALOAD, lastIndex);
		loadArgsAsArray();
		mv.visitMethodInsn(INVOKESPECIAL, myName, "_Nut_Exception", "(ILjava/lang/Exception;[Ljava/lang/Object;)Z");
		Label l5 = new Label();
		mv.visitJumpInsn(IFEQ, l5);
		mv.visitVarInsn(ALOAD, lastIndex);
		mv.visitInsn(ATHROW);
		mv.visitLabel(l3);
		mv.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[] {"java/lang/Throwable"});
		mv.visitVarInsn(ASTORE, lastIndex);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitIntInsn(SIPUSH, methodIndex);
		mv.visitVarInsn(ALOAD, lastIndex);
		loadArgsAsArray();
		mv.visitMethodInsn(INVOKESPECIAL, myName, "_Nut_Error", "(ILjava/lang/Throwable;[Ljava/lang/Object;)Z");
		mv.visitJumpInsn(IFEQ, l5);
		mv.visitVarInsn(ALOAD, lastIndex);
		mv.visitInsn(ATHROW);
		mv.visitLabel(l5);
		mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
		useDefault();
		returnIt();
		mv.visitMaxs(1, 1); // 自动计算
		mv.visitEnd();
	}
	
	private boolean unpackagePrivateData(Type type) {
		if(type.equals(Type.BOOLEAN_TYPE)){
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z");
		}else if(type.equals(Type.BYTE_TYPE)){
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Byte", "byteValue", "()B");
		}else if(type.equals(Type.CHAR_TYPE)){
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Character", "charValue", "()C");
		}else if(type.equals(Type.SHORT_TYPE)){
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Short", "shortValue", "()S");
		}else if(type.equals(Type.INT_TYPE)){
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I");
		}else if(type.equals(Type.LONG_TYPE)){
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Long", "longValue", "()J");
		}else if(type.equals(Type.FLOAT_TYPE)){
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Float", "floatValue", "()F");
		}else if(type.equals(Type.DOUBLE_TYPE)){
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Double", "doubleValue", "()D");
		}else{
			return false;
		}
		return true;
	}

	private boolean isObject = true;
	
	void checkCast(){
		if(returnType.getSort() == Type.ARRAY){
			String returnType_str = desc.substring(desc.lastIndexOf(")")+1);
			mv.visitTypeInsn(CHECKCAST, returnType_str);
			return;
		}
		if(returnType.equals(Type.getType(Object.class))){
			;
		}else{
			if(returnType.getOpcode(IRETURN) != ARETURN){
				checkCast2();
				unpackagePrivateData(returnType);
				isObject = false;
			}else{
				mv.visitTypeInsn(CHECKCAST, returnType.getClassName().replace('.', '/'));
			}
		}
	}
	
	void checkCast2(){
		if(returnType.equals(Type.BOOLEAN_TYPE)){
			mv.visitTypeInsn(CHECKCAST, "java/lang/Boolean");
		}else if(returnType.equals(Type.BYTE_TYPE)){
			mv.visitTypeInsn(CHECKCAST, "java/lang/Byte");
		}else if(returnType.equals(Type.CHAR_TYPE)){
			mv.visitTypeInsn(CHECKCAST, "java/lang/Character");
		}else if(returnType.equals(Type.SHORT_TYPE)){
			mv.visitTypeInsn(CHECKCAST, "java/lang/Short");
		}else if(returnType.equals(Type.INT_TYPE)){
			mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
		}else if(returnType.equals(Type.LONG_TYPE)){
			mv.visitTypeInsn(CHECKCAST, "java/lang/Long");
		}else if(returnType.equals(Type.FLOAT_TYPE)){
			mv.visitTypeInsn(CHECKCAST, "java/lang/Float");
		}else if(returnType.equals(Type.DOUBLE_TYPE)){
			mv.visitTypeInsn(CHECKCAST, "java/lang/Double");
		}
	}
	
	void returnIt(){
		mv.visitInsn(returnType.getOpcode(IRETURN));
	}
	
	void useDefault(){
		if(isObject){
			mv.visitInsn(ACONST_NULL);
		}else{
			if(returnType.equals(Type.BOOLEAN_TYPE)){
				mv.visitInsn(ICONST_0);
			}else if(returnType.equals(Type.BYTE_TYPE)){
				mv.visitInsn(ICONST_0);
			}else if(returnType.equals(Type.CHAR_TYPE)){
				mv.visitInsn(ICONST_0);
			}else if(returnType.equals(Type.SHORT_TYPE)){
				mv.visitInsn(ICONST_0);
			}else if(returnType.equals(Type.INT_TYPE)){
				mv.visitInsn(ICONST_0);
			}else if(returnType.equals(Type.LONG_TYPE)){
				mv.visitInsn(LCONST_0);
			}else if(returnType.equals(Type.FLOAT_TYPE)){
				mv.visitInsn(FCONST_0);
			}else if(returnType.equals(Type.DOUBLE_TYPE)){
				mv.visitInsn(DCONST_0);
			}
		}
	}
}