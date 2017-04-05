package org.nutz.lang.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.concurrent.ConcurrentHashMap;

import org.nutz.aop.DefaultClassDefiner;
import org.nutz.lang.Lang;
import org.nutz.repo.org.objectweb.asm.ClassWriter;
import org.nutz.repo.org.objectweb.asm.Label;
import org.nutz.repo.org.objectweb.asm.Opcodes;
import org.nutz.repo.org.objectweb.asm.Type;
import org.nutz.repo.org.objectweb.asm.commons.GeneratorAdapter;

public class FastMethodFactory implements Opcodes {

    protected static ConcurrentHashMap<String, FastMethod> cache = new ConcurrentHashMap<String, FastMethod>();

    protected static org.nutz.repo.org.objectweb.asm.commons.Method _M;
    protected static org.nutz.repo.org.objectweb.asm.Type Exception_TYPE;
    static {
        _M = _Method("invoke", "(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;");
        Exception_TYPE = Type.getType(Throwable.class);
    }
    
    protected static FastMethod make(Method method) {
        Class<?> klass = method.getDeclaringClass();
        String descriptor = Type.getMethodDescriptor(method);
        String key = "$FM$" + method.getName() + "$" + Lang.md5(descriptor);
        String className = klass.getName() + key;
        if (klass.getName().startsWith("java"))
            className = FastMethod.class.getPackage().getName() + ".fast." + className;
        FastMethod fm = cache.get(className);
        if (fm != null)
            return fm;
        try {
            fm = (FastMethod) klass.getClassLoader().loadClass(className).newInstance();
            cache.put(className, fm);
            return fm;
        }
        catch (Exception e) {}
        byte[] buf = _make(klass,
                           method.getModifiers(),
                           method.getParameterTypes(),
                           _Method(method),
                           method.getReturnType(),
                           className,
                           descriptor);
        Class<?> t = DefaultClassDefiner.defaultOne().define(className,
                                                             buf,
                                                             klass.getClassLoader());
        try {
            fm = (FastMethod) t.newInstance();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        cache.put(className, fm);
        return fm;
    }

    protected static FastMethod make(Constructor<?> constructor) {
        Class<?> klass = constructor.getDeclaringClass();
        String descriptor = Type.getConstructorDescriptor(constructor);
        String key = Lang.md5(descriptor);
        String className = klass.getName() + "$FC$" + key;
        if (klass.getName().startsWith("java"))
            className = FastMethod.class.getPackage().getName() + ".fast." + className;
        FastMethod fm = (FastMethod) cache.get(className);
        if (fm != null)
            return fm;
        try {
            fm = (FastMethod) klass.getClassLoader().loadClass(className).newInstance();
            cache.put(key, fm);
            return fm;
        }
        catch (Exception e) {}
        byte[] buf = _make(klass,
                           constructor.getModifiers(),
                           constructor.getParameterTypes(),
                           _Method(constructor),
                           null,
                           className,
                           descriptor);
        Class<?> t = DefaultClassDefiner.defaultOne().define(className,
                                                             buf,
                                                             klass.getClassLoader());
        try {
            fm = (FastMethod) t.newInstance();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        cache.put(className, fm);
        return fm;
    }

    public static byte[] _make(Class<?> klass,
                               int mod,
                               Class<?>[] params,
                               org.nutz.repo.org.objectweb.asm.commons.Method method,
                               Class<?> returnType,
                               String className,
                               String descriptor) {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        cw.visit(V1_5,
                 ACC_PUBLIC,
                 className.replace('.', '/'),
                 null,
                 "java/lang/Object",
                 new String[]{FastMethod.class.getName().replace('.', '/')});
        
        Type klassType = Type.getType(klass);

        // 首先, 定义默认构造方法
        addConstructor(cw, Type.getType(Object.class), org.nutz.repo.org.objectweb.asm.commons.Method.getMethod("void <init> ()"));

        // 然后定义执行方法
        GeneratorAdapter mg = new GeneratorAdapter(ACC_PUBLIC, _M, null, new Type[]{Exception_TYPE}, cw);
        if (returnType == null) {
            mg.newInstance(klassType);
            mg.dup();
        }
        else if (!Modifier.isStatic(mod)) {
            mg.loadThis();
            mg.loadArg(0);
            mg.checkCast(klassType);
        }
        if (params.length > 0) {
            for (int i = 0; i < params.length; i++) {
                mg.loadArg(1);
                mg.push(i);
                mg.arrayLoad(Type.getType(Object.class));
                Type paramType = Type.getType(params[i]);
                if (params[i].isPrimitive()) {
                    mg.unbox(paramType);
                } else {
                    mg.checkCast(paramType);
                }
            }
        }
        if (returnType == null) {
            mg.invokeConstructor(klassType, method);
        } else {
            if (Modifier.isStatic(mod)) {
                mg.invokeStatic(klassType, method);
            } else if (Modifier.isInterface(klass.getModifiers())) {
                mg.invokeInterface(klassType, method);
            } else {
                mg.invokeVirtual(klassType, method);
            }
            if (Void.class.equals(returnType)) {
                mg.visitInsn(ACONST_NULL);
            } else if (returnType.isPrimitive()) {
                mg.box(Type.getType(returnType));
            }
        }
        Label tmp = new Label();
        mg.visitLabel(tmp);
        mg.visitLineNumber(1, tmp);
        mg.returnValue();
        mg.endMethod();
        cw.visitSource(klass.getSimpleName() + ".java", null);
        cw.visitEnd();
        return cw.toByteArray();
    }

    public static org.nutz.repo.org.objectweb.asm.commons.Method _Method(String name, String desc) {
        return new org.nutz.repo.org.objectweb.asm.commons.Method(name, desc);
    }

    public static org.nutz.repo.org.objectweb.asm.commons.Method _Method(Method method) {
        return org.nutz.repo.org.objectweb.asm.commons.Method.getMethod(method);
    }

    public static org.nutz.repo.org.objectweb.asm.commons.Method _Method(String method) {
        return org.nutz.repo.org.objectweb.asm.commons.Method.getMethod(method);
    }

    private static org.nutz.repo.org.objectweb.asm.commons.Method _Method(Constructor<?> constructor) {
        return org.nutz.repo.org.objectweb.asm.commons.Method.getMethod(constructor);
    }

    public static void addConstructor(ClassWriter cw,
                                      Type parent,
                                      org.nutz.repo.org.objectweb.asm.commons.Method m) {
        GeneratorAdapter mg = new GeneratorAdapter(ACC_PUBLIC, m, null, null, cw);
        mg.loadThis();
        mg.loadArgs();
        mg.invokeConstructor(parent, m);
        mg.returnValue();
        mg.endMethod();
    }
}
