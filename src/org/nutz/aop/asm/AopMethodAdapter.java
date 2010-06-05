package org.nutz.aop.asm;

import org.nutz.repo.org.objectweb.asm.Label;
import org.nutz.repo.org.objectweb.asm.MethodVisitor;
import org.nutz.repo.org.objectweb.asm.Opcodes;
import org.nutz.repo.org.objectweb.asm.Type;

/**
 * 
 * @author wendal(wendal1985@gmail.com)
 *
 */
public class AopMethodAdapter extends NullMethodAdapter implements Opcodes {

	protected int methodIndex;

	protected String myName;

	protected String enhancedSuperName;

	protected String methodName;

	protected Type returnType;

	public AopMethodAdapter(MethodVisitor mv,
							int access,
							String methodName,
							String desc,
							int methodIndex,
							String myName,
							String enhancedSuperName) {
		super(mv, desc, access);
		this.methodIndex = methodIndex;
		this.myName = myName;
		this.enhancedSuperName = enhancedSuperName;
		this.methodName = methodName;
		this.returnType = Type.getReturnType(desc);
	}

	protected void enhandMethod_Void() {
		mv.visitCode();
		Label l0 = new Label();
		Label l1 = new Label();
		Label l2 = new Label();
		mv.visitTryCatchBlock(l0, l1, l2, "java/lang/Throwable");
		mv.visitLabel(l0);
		mv.visitTypeInsn(NEW, "org/nutz/aop/InterceptorChain");
		mv.visitInsn(DUP);
		visitX(methodIndex);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETSTATIC, myName, "_$$Nut_methodArray", "[Ljava/lang/reflect/Method;");
		visitX(methodIndex);
		mv.visitInsn(AALOAD);
		mv.visitFieldInsn(GETSTATIC, myName, "_$$Nut_methodInterceptorList", "[Ljava/util/List;");
		visitX(methodIndex);
		mv.visitInsn(AALOAD);
		loadArgsAsArray();
		mv.visitMethodInsn(	INVOKESPECIAL,
							"org/nutz/aop/InterceptorChain",
							"<init>",
							"(ILjava/lang/Object;Ljava/lang/reflect/Method;Ljava/util/List;[Ljava/lang/Object;)V");
		mv.visitMethodInsn(	INVOKEVIRTUAL,
							"org/nutz/aop/InterceptorChain",
							"doChain",
							"()Lorg/nutz/aop/InterceptorChain;");

		{
			if (Type.getReturnType(desc).equals(Type.VOID_TYPE)) {
				mv.visitInsn(POP);
			} else {
				mv.visitMethodInsn(	INVOKEVIRTUAL,
									"org/nutz/aop/InterceptorChain",
									"getReturn",
									"()Ljava/lang/Object;");
				checkCast();
				returnIt();
			}
		}

		mv.visitLabel(l1);
		Label l3 = new Label();
		mv.visitJumpInsn(GOTO, l3);
		mv.visitLabel(l2);
		mv.visitVarInsn(ASTORE, 3);
		mv.visitVarInsn(ALOAD, 3);
		// mv.visitMethodInsn(INVOKESTATIC, "org/nutz/lang/Lang", "wrapThrow",
		// "(Ljava/lang/Throwable;)Ljava/lang/RuntimeException;");
		mv.visitInsn(ATHROW);
		mv.visitLabel(l3);
		mv.visitInsn(RETURN);
		mv.visitMaxs(8, 4);
		mv.visitEnd();
	}

	public void visitCode() {
		enhandMethod_Void();
	}

	protected void loadArgsAsArray() {
		visitX(argumentTypes.length);
		mv.visitTypeInsn(ANEWARRAY, "java/lang/Object");
		int index = 1;
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

	protected int getLastIndex() {
		int index = 1;
		for (int i = 0; i < argumentTypes.length; i++) {
			Type t = argumentTypes[i];
			index += t.getSize();
		}
		return index;
	}

	protected void visitX(int i) {
		if (i < 6) {
			mv.visitInsn(i + ICONST_0);
		} else {
			mv.visitIntInsn(BIPUSH, i);
		}
	}

	protected boolean packagePrivateData(Type type) {
		if (type.equals(Type.BOOLEAN_TYPE)) {
			mv.visitMethodInsn(	INVOKESTATIC,
								"java/lang/Boolean",
								"valueOf",
								"(Z)Ljava/lang/Boolean;");
		} else if (type.equals(Type.BYTE_TYPE)) {
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;");
		} else if (type.equals(Type.CHAR_TYPE)) {
			mv.visitMethodInsn(	INVOKESTATIC,
								"java/lang/Character",
								"valueOf",
								"(C)Ljava/lang/Character;");
		} else if (type.equals(Type.SHORT_TYPE)) {
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;");
		} else if (type.equals(Type.INT_TYPE)) {
			mv.visitMethodInsn(	INVOKESTATIC,
								"java/lang/Integer",
								"valueOf",
								"(I)Ljava/lang/Integer;");
		} else if (type.equals(Type.LONG_TYPE)) {
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;");
		} else if (type.equals(Type.FLOAT_TYPE)) {
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;");
		} else if (type.equals(Type.DOUBLE_TYPE)) {
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;");
		} else {
			return false;
		}
		return true;
	}

	protected void unpackagePrivateData(Type type) {
		if (type.equals(Type.BOOLEAN_TYPE)) {
			mv.visitMethodInsn(	INVOKESTATIC,
								"org/nutz/aop/asm/Helper",
								"valueOf",
								"(Ljava/lang/Boolean;)Z");
		} else if (type.equals(Type.BYTE_TYPE)) {
			mv.visitMethodInsn(	INVOKESTATIC,
								"org/nutz/aop/asm/Helper",
								"valueOf",
								"(Ljava/lang/Byte;)B");
		} else if (type.equals(Type.CHAR_TYPE)) {
			mv.visitMethodInsn(	INVOKESTATIC,
								"org/nutz/aop/asm/Helper",
								"valueOf",
								"(Ljava/lang/Character;)C");
		} else if (type.equals(Type.SHORT_TYPE)) {
			mv.visitMethodInsn(	INVOKESTATIC,
								"org/nutz/aop/asm/Helper",
								"valueOf",
								"(Ljava/lang/Short;)S");
		} else if (type.equals(Type.INT_TYPE)) {
			mv.visitMethodInsn(	INVOKESTATIC,
								"org/nutz/aop/asm/Helper",
								"valueOf",
								"(Ljava/lang/Integer;)I");
		} else if (type.equals(Type.LONG_TYPE)) {
			mv.visitMethodInsn(	INVOKESTATIC,
								"org/nutz/aop/asm/Helper",
								"valueOf",
								"(Ljava/lang/Long;)J");
		} else if (type.equals(Type.FLOAT_TYPE)) {
			mv.visitMethodInsn(	INVOKESTATIC,
								"org/nutz/aop/asm/Helper",
								"valueOf",
								"(Ljava/lang/Float;)F");
		} else if (type.equals(Type.DOUBLE_TYPE)) {
			mv.visitMethodInsn(	INVOKESTATIC,
								"org/nutz/aop/asm/Helper",
								"valueOf",
								"(Ljava/lang/Double;)D");
		}
	}

	protected boolean isObject = true;

	protected void checkCast() {
		if (returnType.getSort() == Type.ARRAY) {
			mv.visitTypeInsn(CHECKCAST, returnType.getDescriptor());
			return;
		}
		if (returnType.equals(Type.getType(Object.class))) {
			;
		} else {
			if (returnType.getOpcode(IRETURN) != ARETURN) {
				checkCast2();
				unpackagePrivateData(returnType);
				isObject = false;
			} else {
				mv.visitTypeInsn(CHECKCAST, returnType.getClassName().replace('.', '/'));
			}
		}
	}

	protected void checkCast2() {
		if (returnType.equals(Type.BOOLEAN_TYPE)) {
			mv.visitTypeInsn(CHECKCAST, "java/lang/Boolean");
		} else if (returnType.equals(Type.BYTE_TYPE)) {
			mv.visitTypeInsn(CHECKCAST, "java/lang/Byte");
		} else if (returnType.equals(Type.CHAR_TYPE)) {
			mv.visitTypeInsn(CHECKCAST, "java/lang/Character");
		} else if (returnType.equals(Type.SHORT_TYPE)) {
			mv.visitTypeInsn(CHECKCAST, "java/lang/Short");
		} else if (returnType.equals(Type.INT_TYPE)) {
			mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
		} else if (returnType.equals(Type.LONG_TYPE)) {
			mv.visitTypeInsn(CHECKCAST, "java/lang/Long");
		} else if (returnType.equals(Type.FLOAT_TYPE)) {
			mv.visitTypeInsn(CHECKCAST, "java/lang/Float");
		} else if (returnType.equals(Type.DOUBLE_TYPE)) {
			mv.visitTypeInsn(CHECKCAST, "java/lang/Double");
		}
	}

	protected void returnIt() {
		mv.visitInsn(returnType.getOpcode(IRETURN));
	}

	protected void useDefault() {
		if (isObject) {
			mv.visitInsn(ACONST_NULL);
		} else {
			if (returnType.equals(Type.BOOLEAN_TYPE)) {
				mv.visitInsn(ICONST_0);
			} else if (returnType.equals(Type.BYTE_TYPE)) {
				mv.visitInsn(ICONST_0);
			} else if (returnType.equals(Type.CHAR_TYPE)) {
				mv.visitInsn(ICONST_0);
			} else if (returnType.equals(Type.SHORT_TYPE)) {
				mv.visitInsn(ICONST_0);
			} else if (returnType.equals(Type.INT_TYPE)) {
				mv.visitInsn(ICONST_0);
			} else if (returnType.equals(Type.LONG_TYPE)) {
				mv.visitInsn(LCONST_0);
			} else if (returnType.equals(Type.FLOAT_TYPE)) {
				mv.visitInsn(FCONST_0);
			} else if (returnType.equals(Type.DOUBLE_TYPE)) {
				mv.visitInsn(DCONST_0);
			}
		}
	}
}
