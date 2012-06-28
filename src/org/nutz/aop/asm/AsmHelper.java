package org.nutz.aop.asm;

import org.nutz.repo.org.objectweb.asm.MethodVisitor;
import org.nutz.repo.org.objectweb.asm.Opcodes;
import org.nutz.repo.org.objectweb.asm.Type;

final class AsmHelper implements Opcodes{

    static boolean packagePrivateData(Type type, MethodVisitor mv) {
        if (type.equals(Type.BOOLEAN_TYPE)) {
            mv.visitMethodInsn(    INVOKESTATIC,
                                "java/lang/Boolean",
                                "valueOf",
                                "(Z)Ljava/lang/Boolean;");
        } else if (type.equals(Type.BYTE_TYPE)) {
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;");
        } else if (type.equals(Type.CHAR_TYPE)) {
            mv.visitMethodInsn(    INVOKESTATIC,
                                "java/lang/Character",
                                "valueOf",
                                "(C)Ljava/lang/Character;");
        } else if (type.equals(Type.SHORT_TYPE)) {
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;");
        } else if (type.equals(Type.INT_TYPE)) {
            mv.visitMethodInsn(    INVOKESTATIC,
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

    static void unpackagePrivateData(Type type, MethodVisitor mv) {
        if (type.equals(Type.BOOLEAN_TYPE)) {
            mv.visitMethodInsn(    INVOKESTATIC,
                                "org/nutz/aop/asm/Helper",
                                "valueOf",
                                "(Ljava/lang/Boolean;)Z");
        } else if (type.equals(Type.BYTE_TYPE)) {
            mv.visitMethodInsn(    INVOKESTATIC,
                                "org/nutz/aop/asm/Helper",
                                "valueOf",
                                "(Ljava/lang/Byte;)B");
        } else if (type.equals(Type.CHAR_TYPE)) {
            mv.visitMethodInsn(    INVOKESTATIC,
                                "org/nutz/aop/asm/Helper",
                                "valueOf",
                                "(Ljava/lang/Character;)C");
        } else if (type.equals(Type.SHORT_TYPE)) {
            mv.visitMethodInsn(    INVOKESTATIC,
                                "org/nutz/aop/asm/Helper",
                                "valueOf",
                                "(Ljava/lang/Short;)S");
        } else if (type.equals(Type.INT_TYPE)) {
            mv.visitMethodInsn(    INVOKESTATIC,
                                "org/nutz/aop/asm/Helper",
                                "valueOf",
                                "(Ljava/lang/Integer;)I");
        } else if (type.equals(Type.LONG_TYPE)) {
            mv.visitMethodInsn(    INVOKESTATIC,
                                "org/nutz/aop/asm/Helper",
                                "valueOf",
                                "(Ljava/lang/Long;)J");
        } else if (type.equals(Type.FLOAT_TYPE)) {
            mv.visitMethodInsn(    INVOKESTATIC,
                                "org/nutz/aop/asm/Helper",
                                "valueOf",
                                "(Ljava/lang/Float;)F");
        } else if (type.equals(Type.DOUBLE_TYPE)) {
            mv.visitMethodInsn(    INVOKESTATIC,
                                "org/nutz/aop/asm/Helper",
                                "valueOf",
                                "(Ljava/lang/Double;)D");
        }
    }

    static void checkCast(Type type, MethodVisitor mv) {
        if (type.getSort() == Type.ARRAY) {
            mv.visitTypeInsn(CHECKCAST, type.getDescriptor());
            return;
        }
        if (!type.equals(Type.getType(Object.class))) {
            if (type.getOpcode(IRETURN) != ARETURN) {
                checkCast2(type,mv);
                unpackagePrivateData(type,mv);
            } else {
                mv.visitTypeInsn(CHECKCAST, type.getClassName().replace('.', '/'));
            }
        }
    }

    static void checkCast2(Type type, MethodVisitor mv) {
        if (type.equals(Type.BOOLEAN_TYPE)) {
            mv.visitTypeInsn(CHECKCAST, "java/lang/Boolean");
        } else if (type.equals(Type.BYTE_TYPE)) {
            mv.visitTypeInsn(CHECKCAST, "java/lang/Byte");
        } else if (type.equals(Type.CHAR_TYPE)) {
            mv.visitTypeInsn(CHECKCAST, "java/lang/Character");
        } else if (type.equals(Type.SHORT_TYPE)) {
            mv.visitTypeInsn(CHECKCAST, "java/lang/Short");
        } else if (type.equals(Type.INT_TYPE)) {
            mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
        } else if (type.equals(Type.LONG_TYPE)) {
            mv.visitTypeInsn(CHECKCAST, "java/lang/Long");
        } else if (type.equals(Type.FLOAT_TYPE)) {
            mv.visitTypeInsn(CHECKCAST, "java/lang/Float");
        } else if (type.equals(Type.DOUBLE_TYPE)) {
            mv.visitTypeInsn(CHECKCAST, "java/lang/Double");
        }
    }
}
