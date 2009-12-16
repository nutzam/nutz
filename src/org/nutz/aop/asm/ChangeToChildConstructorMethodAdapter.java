package org.nutz.aop.asm;

import org.nutz.aop.asm.org.asm.MethodVisitor;
import static org.nutz.aop.asm.org.asm.Opcodes.*;

/**
 * @author wendal(wendal1985@gmail.com)
 */
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