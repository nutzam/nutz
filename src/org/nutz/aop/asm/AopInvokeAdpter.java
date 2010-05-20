package org.nutz.aop.asm;

import java.lang.reflect.Method;

import org.nutz.aop.asm.org.asm.Label;
import org.nutz.aop.asm.org.asm.MethodVisitor;
import org.nutz.aop.asm.org.asm.Type;

public class AopInvokeAdpter extends AopMethodAdapter {

	protected Method[] methodArray;

	public AopInvokeAdpter(	Method[] methodArray,
							MethodVisitor mv,
							int access,
							String methodName,
							String desc,
							int methodIndex,
							String myName,
							String enhancedSuperName) {
		super(mv, access, methodName, desc, methodIndex, myName, enhancedSuperName);
		this.methodArray = methodArray;
	}

	public void visitCode() {
		mv.visitCode();

		for (int i = 0; i < methodArray.length; i++) {
			Method method = methodArray[i];
			mv.visitVarInsn(ILOAD, 1);
			visitX(i);
			Label l0 = new Label();
			mv.visitJumpInsn(IF_ICMPNE, l0);
			mv.visitVarInsn(ALOAD, 0);
			Type args[] = Type.getArgumentTypes(method);
			for (int j = 0; j < args.length; j++) {
				mv.visitVarInsn(ALOAD, 2);
				visitX(j);
				mv.visitInsn(AALOAD);
				returnType = args[j];
				checkCast();
			}
			mv.visitMethodInsn(	INVOKESPECIAL,
								enhancedSuperName,
								method.getName(),
								Type.getMethodDescriptor(method));
			{
				returnType = Type.getReturnType(method);
				if (returnType.equals(Type.VOID_TYPE))
					mv.visitInsn(ACONST_NULL);
				else if (returnType.getOpcode(IRETURN) != ARETURN)
					packagePrivateData(returnType);
				mv.visitInsn(ARETURN);
			}
			mv.visitInsn(ACONST_NULL);
			mv.visitInsn(ARETURN);
			mv.visitLabel(l0);
		}

		mv.visitInsn(ACONST_NULL);
		mv.visitInsn(ARETURN);
		mv.visitMaxs(4, 3);
		mv.visitEnd();
	}

}
