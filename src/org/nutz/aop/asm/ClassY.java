package org.nutz.aop.asm;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.nutz.aop.AopCallback;
import org.nutz.repo.org.objectweb.asm.ClassWriter;
import org.nutz.repo.org.objectweb.asm.MethodVisitor;
import org.nutz.repo.org.objectweb.asm.Opcodes;
import org.nutz.repo.org.objectweb.asm.Type;

/**
 * 
 * @author wendal(wendal1985@gmail.com)
 *
 */
class ClassY implements Opcodes {

    ClassWriter cw;

    String myName;

    String enhancedSuperName;

    Method[] methodArray;

    Constructor<?>[] constructors;
    
    private Class<?> superClass;

    ClassY(Class<?> klass, String myName, Method[] methodArray, Constructor<?>[] constructors) {
        this.myName = myName.replace('.', '/');
        this.enhancedSuperName = klass.getName().replace('.', '/');
        this.cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        cw.visit(    AsmClassAgent.CLASS_LEVEL,
                    ACC_PUBLIC,
                    this.myName,
                    null,
                    enhancedSuperName,
                    getParentInterfaces(klass));
        this.methodArray = methodArray;
        this.constructors = constructors;
        this.superClass = klass;
    }

    String[] getParentInterfaces(Class<?> xClass) {
        Class<?>[] its = xClass.getInterfaces();
        if (its == null || its.length == 0)
            return new String[]{AopCallback.class.getName().replace('.', '/')};
        else {
            String[] iii = new String[its.length + 1];
            for (int i = 0; i < its.length; i++)
                iii[i] = its[i].getName().replace('.', '/');
            iii[its.length] = AopCallback.class.getName().replace('.', '/');
            return iii;
        }
    }

    String[] convertExp(Class<?>[] expClasses) {
        if (expClasses.length == 0)
            return null;
        String[] results = new String[expClasses.length];
        for (int i = 0; i < results.length; i++)
            results[i] = expClasses[i].getName().replace('.', '/');
        return results;
    }

    int getAccess(int modify) {
        if (Modifier.isProtected(modify))
            return ACC_PROTECTED;
        if (Modifier.isPublic(modify))
            return ACC_PUBLIC;
        return 0x00;
    }

    static int findMethodIndex(String name, String desc, Method[] methods) {
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            if (Type.getMethodDescriptor(method).equals(desc) && method.getName().equals(name))
                return i;
        }
        return -1;// 是否应该抛出异常呢?应该不可能发生的
    }

    void addConstructors() {
        for (Constructor<?> constructor : constructors) {
            String[] expClasses = convertExp(constructor.getExceptionTypes());
            String desc = Type.getConstructorDescriptor(constructor);
            int access = getAccess(constructor.getModifiers());
            MethodVisitor mv = cw.visitMethod(access, "<init>", desc, null, expClasses);
            new ChangeToChildConstructorMethodAdapter(mv, desc, access, enhancedSuperName).visitCode();
        }
    }

    byte[] toByteArray() {
        addField();
        addConstructors();
        addAopMethods();
        enhandMethod();
        cw.visitSource(superClass.getSimpleName() + ".java", null);
        return cw.toByteArray();
    }

    private void enhandMethod() {
        for (Method method : methodArray) {
            String methodName = method.getName();
            String methodDesc = Type.getMethodDescriptor(method);
            int methodAccess = getAccess(method.getModifiers());
            MethodVisitor mv = cw.visitMethod(    methodAccess,
                                                methodName,
                                                methodDesc,
                                                null,
                                                convertExp(method.getExceptionTypes()));
            int methodIndex = findMethodIndex(methodName, methodDesc, methodArray);
            AopMethodAdapter adapter = new AopMethodAdapter(    mv,
                                    methodAccess,
                                    methodName,
                                    methodDesc,
                                    methodIndex,
                                    myName,
                                    enhancedSuperName);
            adapter.visitCode();
            adapter.visitAttribute();
        }
    }

    private void addAopMethods() {
        new AopInvokeAdpter(methodArray,
                            cw.visitMethod(    ACC_PUBLIC,
                                            "_aop_invoke",
                                            "(I[Ljava/lang/Object;)Ljava/lang/Object;",
                                            null,
                                            null),
                            ACC_PUBLIC,
                            "invoke",
                            "(I[Ljava/lang/Object;)Ljava/lang/Object;",
                            0,
                            myName,
                            enhancedSuperName).visitCode();
    }

    private void addField() {
        cw.visitField(    ACC_PRIVATE + ACC_STATIC,
                AsmClassAgent.MethodArray_FieldName,
                "[Ljava/lang/reflect/Method;",
                null,
                null).visitEnd();
        cw.visitField(    ACC_PRIVATE + ACC_STATIC,
                AsmClassAgent.MethodInterceptorList_FieldName,
                "[Ljava/util/List;",
                "[Ljava/util/List<Lorg/nutz/aop/MethodInterceptor;>;",
                null).visitEnd();
    }

    static <T> byte[] enhandClass(    Class<T> kclass,
                                            String myName,
                                            Method[] methodArray,
                                            Constructor<?>[] constructors) {
        return new ClassY(kclass, myName, methodArray, constructors).toByteArray();
    }
}
