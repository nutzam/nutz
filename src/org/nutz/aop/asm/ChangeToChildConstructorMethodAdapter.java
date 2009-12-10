package org.nutz.aop.asm;

import org.objectweb.asm.MethodVisitor;
import static org.objectweb.asm.Opcodes.*;

class ChangeToChildConstructorMethodAdapter extends NullMethodAdapter {

	private String superClassName;

	public ChangeToChildConstructorMethodAdapter(MethodVisitor mv,String desc,int access,
			String superClassName) {
		super(mv,desc,access);
		this.superClassName = superClassName;
	}

	public void visitCode() {
		mv.visitCode();
		mv.visitVarInsn(ALOAD, 0);
		loadArgs();
		mv.visitMethodInsn(INVOKESPECIAL, superClassName, "<init>", desc);
		mv.visitInsn(RETURN);
		mv.visitMaxs(2, 2);
		mv.visitEnd();
	}
}