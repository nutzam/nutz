package org.nutz.lang.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Map;

import org.nutz.lang.Lang;
import org.nutz.repo.org.objectweb.asm.Type;

public class FastClassImpl implements FastClass {

    protected Class<?> klass;
    protected Map<String, FastMethod> constructors;
    protected Map<String, FastMethod> methods;
    protected Map<String, FastMethod> fields;
    
    public FastClassImpl(Class<?> klass,
                      Map<String, FastMethod> constructors,
                      Map<String, FastMethod> methods,
                      Map<String, FastMethod> fields) {
        this.klass = klass;
        this.constructors = constructors;
        this.methods = methods;
        this.fields = fields;
    }
    
    public Object invoke(Object obj, Method method, Object... args) {
        try {
            FastMethod fm = fast(method);
            if (fm != null)
                return fm.invoke(obj, args);
            if (!method.isAccessible())
                method.setAccessible(true);
            return method.invoke(obj, args);
        }
        catch (Exception e) {
            throw Lang.wrapThrow(e);
        }
    }

    public Object invoke(Object obj, String methodName, Class<?>[] types, Object... args) {
        try {
            return invoke(obj, obj.getClass().getDeclaredMethod(methodName, types), args);
        }
        catch (Exception e) {
            throw Lang.wrapThrow(e);
        }
    }

    public Object born(Constructor<?> constructor, Object... args) {
        try {
            return fast(constructor).invoke(null, args);
        }
        catch (Exception e) {
            throw Lang.wrapThrow(e);
        }
    }

    @Override
    public Object born(Class<?>[] types, Object... args) {
        try {
            return born(klass.getDeclaredConstructor(types), args);
        }
        catch (Exception e) {
            throw Lang.wrapThrow(e);
        }
    }

    @Override
    public Object born() {
        try {
            return constructors.get("()V").invoke(null);
        }
        catch (Exception e) {
            throw Lang.wrapThrow(e);
        }
    }

    public Object setField(Object obj, String fieldName, Object value) {
        return null;
    }

    public Object getField(Object obj, String fieldName) {
        return null;
    }
    
    public FastMethod fast(Method method) {
        return methods.get(method.getName() + "$" + Type.getMethodDescriptor(method));
    }
    
    public FastMethod fast(final Constructor<?> constructor) {
        FastMethod fm = constructors.get(Type.getConstructorDescriptor(constructor));
        if (fm == null)
            fm = new FastMethodFactory.FallbackFastMethod(constructor);
       return fm;
    }
}
