package org.nutz.aop.asm;

import org.nutz.repo.org.objectweb.asm.MethodVisitor;
import org.nutz.repo.org.objectweb.asm.Opcodes;
import org.nutz.repo.org.objectweb.asm.Type;

final class AsmHelper implements Opcodes{

    private static final String ORG_NUTZ_AOP_ASM_HELPER = "org/nutz/aop/asm/Helper";
    private static final String VALUE_OF = "valueOf";

    static boolean packagePrivateData(Type type, MethodVisitor mv) {
        if (type.equals(Type.BOOLEAN_TYPE)) {
            mv.visitMethodInsn(    INVOKESTATIC,
                                "java/lang/Boolean",
                                VALUE_OF,
                                "(Z)Ljava/lang/Boolean;",
                                false);
        } else if (type.equals(Type.BYTE_TYPE)) {
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Byte", VALUE_OF, "(B)Ljava/lang/Byte;", false);
        } else if (type.equals(Type.CHAR_TYPE)) {
            mv.visitMethodInsn(    INVOKESTATIC,
                                "java/lang/Character",
                                VALUE_OF,
                                "(C)Ljava/lang/Character;",
                                false);
        } else if (type.equals(Type.SHORT_TYPE)) {
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Short", VALUE_OF, "(S)Ljava/lang/Short;", false);
        } else if (type.equals(Type.INT_TYPE)) {
            mv.visitMethodInsn(    INVOKESTATIC,
                                "java/lang/Integer",
                                VALUE_OF,
                                "(I)Ljava/lang/Integer;",
                                false);
        } else if (type.equals(Type.LONG_TYPE)) {
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Long", VALUE_OF, "(J)Ljava/lang/Long;", false);
        } else if (type.equals(Type.FLOAT_TYPE)) {
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", VALUE_OF, "(F)Ljava/lang/Float;", false);
        } else if (type.equals(Type.DOUBLE_TYPE)) {
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Double", VALUE_OF, "(D)Ljava/lang/Double;", false);
        } else {
            return false;
        }
        return true;
    }

    static void unpackagePrivateData(Type type, MethodVisitor mv) {
        if (type.equals(Type.BOOLEAN_TYPE)) {
            mv.visitMethodInsn(    INVOKESTATIC,
                                ORG_NUTZ_AOP_ASM_HELPER,
                                VALUE_OF,
                                "(Ljava/lang/Boolean;)Z",
                                false);
        } else if (type.equals(Type.BYTE_TYPE)) {
            mv.visitMethodInsn(    INVOKESTATIC,
                                ORG_NUTZ_AOP_ASM_HELPER,
                                VALUE_OF,
                                "(Ljava/lang/Byte;)B",
                                false);
        } else if (type.equals(Type.CHAR_TYPE)) {
            mv.visitMethodInsn(    INVOKESTATIC,
                                ORG_NUTZ_AOP_ASM_HELPER,
                                VALUE_OF,
                                "(Ljava/lang/Character;)C",
                                false);
        } else if (type.equals(Type.SHORT_TYPE)) {
            mv.visitMethodInsn(    INVOKESTATIC,
                                ORG_NUTZ_AOP_ASM_HELPER,
                                VALUE_OF,
                                "(Ljava/lang/Short;)S",
                                false);
        } else if (type.equals(Type.INT_TYPE)) {
            mv.visitMethodInsn(    INVOKESTATIC,
                                ORG_NUTZ_AOP_ASM_HELPER,
                                VALUE_OF,
                                "(Ljava/lang/Integer;)I",
                                false);
        } else if (type.equals(Type.LONG_TYPE)) {
            mv.visitMethodInsn(    INVOKESTATIC,
                                ORG_NUTZ_AOP_ASM_HELPER,
                                VALUE_OF,
                                "(Ljava/lang/Long;)J",
                                false);
        } else if (type.equals(Type.FLOAT_TYPE)) {
            mv.visitMethodInsn(    INVOKESTATIC,
                                ORG_NUTZ_AOP_ASM_HELPER,
                                VALUE_OF,
                                "(Ljava/lang/Float;)F",
                                false);
        } else if (type.equals(Type.DOUBLE_TYPE)) {
            mv.visitMethodInsn(    INVOKESTATIC,
                                ORG_NUTZ_AOP_ASM_HELPER,
                                VALUE_OF,
                                "(Ljava/lang/Double;)D",
                                false);
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
