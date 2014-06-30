package org.nutz.lang.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.nutz.aop.DefaultClassDefiner;
import org.nutz.lang.Mirror;
import org.nutz.repo.org.objectweb.asm.ClassWriter;
import org.nutz.repo.org.objectweb.asm.MethodVisitor;
import org.nutz.repo.org.objectweb.asm.Opcodes;
import org.nutz.repo.org.objectweb.asm.Type;

public final class FastClassFactory implements Opcodes {

    private static AtomicInteger count = new AtomicInteger();

    public static final String MethodArray_FieldName = "_$$Fast_methodArray";
    public static final String ConstructorArray_FieldName = "_$$Fast_constructorArray";
    public static final String SrcClass_FieldName = "_$$Fast_srcClass";
    public static final String FieldNameArray_FieldName = "_$$Fast_fieldNames";

    public static Map<String, FastClass> cache = new ConcurrentHashMap<String, FastClass>();

    private static final Object lock = new Object();

    public static FastClass get(Class<?> klass) {
        String cacheKey = klass.getName() + "_" + klass.getClassLoader();
        FastClass fastClass = cache.get(cacheKey);
        if (fastClass != null) {
            return fastClass;
        }
        synchronized (lock) {
            fastClass = cache.get(cacheKey);
            if (fastClass != null) {
                return fastClass;
            }
            Class<?> fclass = create(klass);
            try {
                fastClass = (FastClass) fclass.newInstance();
                cache.put(cacheKey, fastClass);
                return fastClass;
            }
            catch (Exception e) {
                throw new IllegalArgumentException("Fail to create FastClass for "
                                                           + cacheKey,
                                                   e);
            }
        }
    }

    protected static synchronized Class<?> create(Class<?> classZ) {
        String myName = classZ.getName().replace('.', '/') + FastClass.CLASSNAME + count.getAndIncrement();
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        cw.visit(V1_6,
                 ACC_PUBLIC,
                 myName,
                 null,
                 "org/nutz/lang/reflect/AbstractFastClass",
                 null);
        // 添加默认构造方法
        {
            MethodVisitor mv = cw.visitMethod(ACC_PUBLIC,
                                              "<init>",
                                              "()V",
                                              null,
                                              null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL,
                               "org/nutz/lang/reflect/AbstractFastClass",
                               "<init>",
                               "()V");
            mv.visitInsn(RETURN);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }
        // 添加默认字段
        {
            cw.visitField(ACC_PUBLIC + ACC_STATIC,
                          FastClassFactory.MethodArray_FieldName,
                          "[Ljava/lang/reflect/Method;",
                          null,
                          null).visitEnd();
            cw.visitField(ACC_PUBLIC + ACC_STATIC,
                          ConstructorArray_FieldName,
                          "[Ljava/lang/reflect/Constructor;",
                          null,
                          null).visitEnd();
            cw.visitField(ACC_PUBLIC + ACC_STATIC,
                          SrcClass_FieldName,
                          "Ljava/lang/Class;",
                          "Ljava/lang/Class<*>;",
                          null).visitEnd();
        }
        // 实现默认字段的getter
        {
            MethodVisitor mv = cw.visitMethod(ACC_PROTECTED,
                                              "getMethods",
                                              "()[Ljava/lang/reflect/Method;",
                                              null,
                                              null);
            mv.visitCode();
            mv.visitFieldInsn(GETSTATIC,
                              myName,
                              FastClassFactory.MethodArray_FieldName,
                              "[Ljava/lang/reflect/Method;");
            mv.visitInsn(ARETURN);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
            // -----------------------------------------------------------------------------------------------------
            mv = cw.visitMethod(ACC_PROTECTED,
                                "getConstructors",
                                "()[Ljava/lang/reflect/Constructor;",
                                "()[Ljava/lang/reflect/Constructor<*>;",
                                null);
            mv.visitCode();
            mv.visitFieldInsn(GETSTATIC,
                              myName,
                              ConstructorArray_FieldName,
                              "[Ljava/lang/reflect/Constructor;");
            mv.visitInsn(ARETURN);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
            // -----------------------------------------------------------------------------------------------------
            mv = cw.visitMethod(ACC_PROTECTED,
                                "getSrcClass",
                                "()Ljava/lang/Class;",
                                "()Ljava/lang/Class<*>;",
                                null);
            mv.visitCode();
            mv.visitFieldInsn(GETSTATIC,
                              myName,
                              SrcClass_FieldName,
                              "Ljava/lang/Class;");
            mv.visitInsn(ARETURN);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }
        Method[] methods = classZ.getMethods();
        Arrays.sort(methods, new MethodComparator());
        // 构建_invoke方法
        {
            String[] methodNames = new String[methods.length];
            String[] descs = new String[methods.length];
            int[] modifies = new int[methods.length];
            int[] invokeOps = new int[methods.length];
            for (int i = 0; i < methods.length; i++) {
                methodNames[i] = methods[i].getName();
                descs[i] = Type.getMethodDescriptor(methods[i]);
                modifies[i] = methods[i].getModifiers();
                if (classZ.isInterface())
                    invokeOps[i] = INVOKEINTERFACE;
                else if (Modifier.isAbstract(methods[i].getModifiers()))
                    invokeOps[i] = INVOKESPECIAL;
                else if (Modifier.isStatic(methods[i].getModifiers()))
                    invokeOps[i] = INVOKESTATIC;
                else
                    invokeOps[i] = INVOKEVIRTUAL;
            }
            FastClassAdpter.createInokeMethod(cw.visitMethod(ACC_PUBLIC
                                                                     + ACC_VARARGS,
                                                             "_invoke",
                                                             "(Ljava/lang/Object;I[Ljava/lang/Object;)Ljava/lang/Object;",
                                                             null,
                                                             null),
                                              methodNames,
                                              descs,
                                              modifies,
                                              invokeOps,
                                              classZ.getName()
                                                    .replace('.', '/'));
        }
        // 添加_born方法
        Constructor<?>[] constructors = classZ.getConstructors();
        Arrays.sort(constructors, new ConstructorComparator());
        if (constructors.length > 0) {
            FastClassAdpter.createInokeConstructor(cw.visitMethod(ACC_PROTECTED
                                                                          + ACC_VARARGS,
                                                                  "_born",
                                                                  "(I[Ljava/lang/Object;)Ljava/lang/Object;",
                                                                  null,
                                                                  null),
                                                   classZ.getName()
                                                         .replace('.', '/'),
                                                   constructors);
        }

        cw.visitEnd();

        Class<?> xClass = DefaultClassDefiner.def(myName.replace('/', '.'),
                                              cw.toByteArray());
        try {
            xClass.getField(SrcClass_FieldName).set(null, classZ);
            xClass.getField(MethodArray_FieldName).set(null, methods);
            xClass.getField(ConstructorArray_FieldName).set(null, constructors);
        }
        catch (Throwable e) {
            e.printStackTrace();
        }
        return xClass;
    }

    // public static void main(String[] args) throws Throwable {
    // ASMifierClassVisitor.main(new String[]{"org.nutz.lang.reflect.XXX"});
    // //
    // System.out.println(Type.getObjectType(AbstractFastClass.class.getName().replace('.',
    // // '/')));
    // }
}

class Util {

    public static int compara(Class<?>[] mps1, Class<?>[] mps2) {
        if (mps1.length > mps2.length)
            return 1;
        if (mps1.length < mps2.length)
            return -1;
        for (int i = 0; i < mps1.length; i++) {
            if (mps1[i] == mps2[i])
                continue;
            if (mps1[i].isPrimitive() && (!mps2[i].isPrimitive()))
                return -1;
            else if (mps2[i].isPrimitive() && (!mps1[i].isPrimitive()))
                return 1;
            if (mps1[i].isPrimitive() || mps2[i].isPrimitive())
                if (Mirror.me(mps1[i]).getWrapper() == Mirror.me(mps2[i])
                                                             .getWrapper()) {
                    if (mps1[i].isPrimitive())
                        return -1;
                    else
                        return 1;
                }
            if (mps2[i].isAssignableFrom(mps1[i]))
                return 1;
        }
        return 0;
    }
}

class ConstructorComparator implements Comparator<Constructor<?>> {

    public int compare(Constructor<?> c1, Constructor<?> c2) {
        if (c1 == c2)
            return 0;
        if (!c1.getName().equals(c2.getName()))
            return c1.getName().compareTo(c2.getName());
        return Util.compara(c1.getParameterTypes(), c2.getParameterTypes());
    }

}

class MethodComparator implements Comparator<Method> {

    public int compare(Method m1, Method m2) {
        if (m1 == m2)
            return 0;
        if (!m1.getName().equals(m2.getName()))
            return m1.getName().compareTo(m2.getName());
        return Util.compara(m1.getParameterTypes(), m2.getParameterTypes());
    }

}