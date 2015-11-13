package org.nutz.lang.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.nutz.aop.DefaultClassDefiner;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.repo.org.objectweb.asm.ClassWriter;
import org.nutz.repo.org.objectweb.asm.Label;
import org.nutz.repo.org.objectweb.asm.MethodVisitor;
import org.nutz.repo.org.objectweb.asm.Opcodes;
import org.nutz.repo.org.objectweb.asm.Type;

public final class FastClassFactory implements Opcodes {

    public static Map<String, FastClass> cache = new ConcurrentHashMap<String, FastClass>();

    private static final Object lock = new Object();
    
    protected static boolean useCache = true;
    
    public static boolean isUseCache() {
        return useCache;
    }
    
    public static void setUseCache(boolean useCache) {
        FastClassFactory.useCache = useCache;
    }
    
    public static void clearCache() {
        cache.clear();
    }

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
            try {
                fastClass = create(klass);
                if (useCache)
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
    
    public static Object invoke(Object obj, Method method, Object ... args) {
        return get(method.getDeclaringClass()).invoke(obj, method, args);
    }

    protected static synchronized FastClass create(Class<?> classZ) {
        String myName = classZ.getName().replace('.', '/');
        if (myName.startsWith("java")) {
            myName = "org/nutz/lang/reflect" + '/' + myName;
        }
        myName += FastClass.CLASSNAME;
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        cw.visit(V1_5,
                 ACC_PUBLIC,
                 myName,
                 null,
                 "org/nutz/lang/reflect/AbstractFastClass",
                 null);
        // 添加默认构造方法
        {
            MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", "(Ljava/lang/Class;[Ljava/lang/reflect/Constructor;[Ljava/lang/reflect/Method;[Ljava/lang/reflect/Field;)V", "(Ljava/lang/Class<*>;[Ljava/lang/reflect/Constructor<*>;[Ljava/lang/reflect/Method;[Ljava/lang/reflect/Field;)V", null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitVarInsn(ALOAD, 4);
            mv.visitMethodInsn(INVOKESPECIAL, "org/nutz/lang/reflect/AbstractFastClass", "<init>", "(Ljava/lang/Class;[Ljava/lang/reflect/Constructor;[Ljava/lang/reflect/Method;[Ljava/lang/reflect/Field;)V");
            mv.visitInsn(RETURN);
            mv.visitMaxs(5, 5);
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
                //else if (Modifier.isAbstract(methods[i].getModifiers()))
                //    invokeOps[i] = INVOKESPECIAL;
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
            String enhancedSuperName = classZ.getName().replace('.', '/');
            FastClassAdpter.createInokeConstructor(cw.visitMethod(ACC_PROTECTED
                                                                          + ACC_VARARGS,
                                                                  "_born",
                                                                  "(I[Ljava/lang/Object;)Ljava/lang/Object;",
                                                                  null,
                                                                  null),
                                                                  enhancedSuperName,
                                                   constructors);
            for (Constructor<?> constructor : constructors) {
                if (constructor.getParameterTypes().length == 0) {
                    MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "born", "()Ljava/lang/Object;", null, null);
                    mv.visitCode();
                    // start of fuck linenumber
                    Label tmp = new Label();
                    mv.visitLabel(tmp);
                    mv.visitLineNumber(1, tmp);
                    // end of Linenumber
                    mv.visitTypeInsn(NEW, enhancedSuperName);
                    mv.visitInsn(DUP);
                    mv.visitMethodInsn( INVOKESPECIAL,
                                        enhancedSuperName,
                                        "<init>",
                                        Type.getConstructorDescriptor(constructor));
                    mv.visitInsn(ARETURN);
                    mv.visitMaxs(2, 1);
                    mv.visitEnd();
                    break;
                }
            }
        }

        cw.visitSource(classZ.getSimpleName() + ".java", null);
        cw.visitEnd();

        Class<?> xClass = DefaultClassDefiner.defaultOne().define(myName.replace('/', '.'),
                                              cw.toByteArray(),
                                              classZ.getClassLoader());
        try {
            return (FastClass)xClass.getConstructor(Class.class, Constructor[].class, Method[].class, Field[].class).newInstance(classZ, constructors, methods, null);
        }
        catch (Exception e) {
            throw Lang.impossible();
        }
    }
}

class Util {

    public static int compare(Class<?>[] mps1, Class<?>[] mps2) {
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
        return Util.compare(c1.getParameterTypes(), c2.getParameterTypes());
    }

}

class MethodComparator implements Comparator<Method> {

    public int compare(Method m1, Method m2) {
        if (m1 == m2)
            return 0;
        if (!m1.getName().equals(m2.getName()))
            return m1.getName().compareTo(m2.getName());
        return Util.compare(m1.getParameterTypes(), m2.getParameterTypes());
    }

}