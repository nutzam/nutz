package org.nutz.lang.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import org.nutz.repo.org.objectweb.asm.Label;
import org.nutz.repo.org.objectweb.asm.MethodVisitor;
import org.nutz.repo.org.objectweb.asm.Opcodes;
import org.nutz.repo.org.objectweb.asm.Type;

public class FastClassAdpter implements Opcodes {

	protected final MethodVisitor mv;

	protected String enhancedSuperName;

	protected Type returnType;

	protected String[] descs;

	protected String[] methodNames;

	protected int[] modifies;

	protected int[] invokeOps;

	private FastClassAdpter(MethodVisitor mv, String enhancedSuperName) {
		this.mv = mv;
		this.enhancedSuperName = enhancedSuperName;
	}

	public void createInvokeMethod() {
		mv.visitCode();
		for (int i = 0; i < methodNames.length; i++) {
            // start of fuck linenumber
            Label tmp = new Label();
            mv.visitLabel(tmp);
            mv.visitLineNumber(1, tmp);
            // end of Linenumber
			mv.visitVarInsn(ILOAD, 2);
			visitX(i);
			Label l0 = new Label();
			mv.visitJumpInsn(IF_ICMPNE, l0);
			if (!Modifier.isStatic(modifies[i])) {
				mv.visitVarInsn(ALOAD, 1);
				returnType = Type.getObjectType(enhancedSuperName);
				checkCast();
			}
			Type args[] = Type.getArgumentTypes(descs[i]);
			for (int j = 0; j < args.length; j++) {
				mv.visitVarInsn(ALOAD, 3);
				visitX(j);
				mv.visitInsn(AALOAD);
				returnType = args[j];
				checkCast();
			}
			mv.visitMethodInsn(	invokeOps[i],
								enhancedSuperName,
								methodNames[i],
								Type.getMethodDescriptor(	Type.getReturnType(descs[i]),
															Type.getArgumentTypes(descs[i])));
			returnType = Type.getReturnType(descs[i]);
			if (returnType.equals(Type.VOID_TYPE))
				mv.visitInsn(ACONST_NULL);
			else if (returnType.getOpcode(IRETURN) != ARETURN)
				packagePrivateData(returnType);
			mv.visitInsn(ARETURN);
			mv.visitLabel(l0);
		}

		mv.visitMethodInsn(INVOKESTATIC, "org/nutz/lang/Lang", "impossible", "()Ljava/lang/RuntimeException;");
		mv.visitInsn(ATHROW);
		mv.visitMaxs(4, 3);
		mv.visitEnd();
	}

	public void createInvokeConstructor(Constructor<?>[] constructors) {
		mv.visitCode();
		for (int i = 0; i < constructors.length; i++) {
			mv.visitVarInsn(ILOAD, 1);
			visitX(i);
			Label l0 = new Label();
			mv.visitJumpInsn(IF_ICMPNE, l0);
			mv.visitTypeInsn(NEW, enhancedSuperName);
			mv.visitInsn(DUP);

			Type args[] = Type.getArgumentTypes(Type.getConstructorDescriptor(constructors[i]));
			for (int j = 0; j < args.length; j++) {
				mv.visitVarInsn(ALOAD, 2);
				visitX(j);
				mv.visitInsn(AALOAD);
				returnType = args[j];
				checkCast();
			}
			mv.visitMethodInsn(	INVOKESPECIAL,
								enhancedSuperName,
								"<init>",
								Type.getConstructorDescriptor(constructors[i]));
			mv.visitInsn(ARETURN);
			mv.visitLabel(l0);
		}
		mv.visitMethodInsn(INVOKESTATIC, "org/nutz/lang/Lang", "impossible", "()Ljava/lang/RuntimeException;");
		mv.visitInsn(ATHROW);
		mv.visitMaxs(4, 3);
		mv.visitEnd();
	}

	protected void loadInsn(final Type type, final int index) {
		mv.visitVarInsn(type.getOpcode(Opcodes.ILOAD), index);
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

	public final static void createInokeMethod(	MethodVisitor mv,
												String[] methodNames,
												String[] descs,
												int[] modifies,
												int[] invokeOps,
												String enhancedSuperName) {
		FastClassAdpter adpter = new FastClassAdpter(mv, enhancedSuperName);
		adpter.descs = descs;
		adpter.methodNames = methodNames;
		adpter.modifies = modifies;
		adpter.invokeOps = invokeOps;
		adpter.createInvokeMethod();
	}

	public final static void createInokeConstructor(MethodVisitor mv,
													String enhancedSuperName,
													Constructor<?>[] constructors) {
		FastClassAdpter adpter = new FastClassAdpter(mv, enhancedSuperName);
		adpter.createInvokeConstructor(constructors);
	}
}
