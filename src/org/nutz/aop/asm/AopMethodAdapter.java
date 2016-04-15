package org.nutz.aop.asm;

import org.nutz.repo.org.objectweb.asm.Attribute;
import org.nutz.repo.org.objectweb.asm.Label;
import org.nutz.repo.org.objectweb.asm.MethodVisitor;
import org.nutz.repo.org.objectweb.asm.Opcodes;
import org.nutz.repo.org.objectweb.asm.Type;

/**
 * 
 * @author wendal(wendal1985@gmail.com)
 *
 */
class AopMethodAdapter extends NormalMethodAdapter implements Opcodes {

    private static final String ORG_NUTZ_AOP_INTERCEPTOR_CHAIN = "org/nutz/aop/InterceptorChain";

    int methodIndex;

    String myName;

    String enhancedSuperName;

    String methodName;

    Type returnType;

    AopMethodAdapter(MethodVisitor mv,
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

    void enhandMethod_Void() {
        mv.visitCode();
        // start of fuck linenumber
        Label tmp = new Label();
        mv.visitLabel(tmp);
        mv.visitLineNumber(1, tmp);
        Label l0 = new Label();
        Label l1 = new Label();
        Label l2 = new Label();
        mv.visitTryCatchBlock(l0, l1, l2, "java/lang/Throwable");
        mv.visitLabel(l0);
        mv.visitTypeInsn(NEW, ORG_NUTZ_AOP_INTERCEPTOR_CHAIN);
        mv.visitInsn(DUP);
        visitX(methodIndex);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETSTATIC, myName, AsmClassAgent.MethodArray_FieldName, "[Ljava/lang/reflect/Method;");
        visitX(methodIndex);
        mv.visitInsn(AALOAD);
        mv.visitFieldInsn(GETSTATIC, myName, AsmClassAgent.MethodInterceptorList_FieldName, "[Ljava/util/List;");
        visitX(methodIndex);
        mv.visitInsn(AALOAD);
        loadArgsAsArray();
        mv.visitMethodInsn(    INVOKESPECIAL,
                            ORG_NUTZ_AOP_INTERCEPTOR_CHAIN,
                            "<init>",
                            "(ILjava/lang/Object;Ljava/lang/reflect/Method;Ljava/util/List;[Ljava/lang/Object;)V");
        mv.visitMethodInsn(    INVOKEVIRTUAL,
                            ORG_NUTZ_AOP_INTERCEPTOR_CHAIN,
                            "doChain",
                            "()Lorg/nutz/aop/InterceptorChain;");

        {
            if (Type.getReturnType(desc).equals(Type.VOID_TYPE)) {
                mv.visitInsn(POP);
            } else {
                mv.visitMethodInsn(    INVOKEVIRTUAL,
                                    ORG_NUTZ_AOP_INTERCEPTOR_CHAIN,
                                    "getReturn",
                                    "()Ljava/lang/Object;");
                AsmHelper.checkCast(returnType,mv);
                returnIt();
            }
        }

        mv.visitLabel(l1);
        Label l3 = new Label();
        mv.visitJumpInsn(GOTO, l3);
        mv.visitLabel(l2);
        mv.visitVarInsn(ASTORE, 3);
        mv.visitVarInsn(ALOAD, 3);
        mv.visitInsn(ATHROW);
        mv.visitLabel(l3);
        mv.visitInsn(RETURN);
        mv.visitMaxs(8, 4);
        mv.visitEnd();
    }

    void visitCode() {
        enhandMethod_Void();
    }

    void loadArgsAsArray() {
        visitX(argumentTypes.length);
        mv.visitTypeInsn(ANEWARRAY, "java/lang/Object");
        int index = 1;
        for (int i = 0; i < argumentTypes.length; i++) {
            mv.visitInsn(DUP);
            visitX(i);
            Type t = argumentTypes[i];
            loadInsn(t, index);
            index += t.getSize();
            AsmHelper.packagePrivateData(t,mv);
            mv.visitInsn(AASTORE);
        }
    }

    void visitX(int i) {
        if (i < 6) {
            mv.visitInsn(i + ICONST_0);
        } else {
            if (i < Byte.MAX_VALUE)
                mv.visitIntInsn(BIPUSH, i);
            else 
                mv.visitIntInsn(SIPUSH, i);
        }
    }

    void returnIt() {
        mv.visitInsn(returnType.getOpcode(IRETURN));
    }

    void visitAttribute() {
        Attribute attr = new Attribute("LocalVariableTable");
        attr.value= new byte[]{0,0};
        mv.visitAttribute(attr);
    }
}
