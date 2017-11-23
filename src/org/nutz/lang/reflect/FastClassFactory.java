package org.nutz.lang.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
        FastMethodFactory.cache.clear();
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
    
    public static FastMethod get(Method method) {
        return get(method.getDeclaringClass()).fast(method);
    }
    
    public static FastMethod get(Constructor<?> constructor) {
        return get(constructor.getDeclaringClass()).fast(constructor);
    }

    protected static synchronized FastClass create(Class<?> klass) {
        Map<String, FastMethod> constructors = new HashMap<String, FastMethod>();
        Map<String, FastMethod> methods = new HashMap<String, FastMethod>();
        Map<String, FastMethod> fields = new HashMap<String, FastMethod>();
        for (Constructor<?> constructor : klass.getConstructors()) {
            String key = Type.getConstructorDescriptor(constructor);
            FastMethod fm = FastMethodFactory.make(constructor);
            constructors.put(key, fm);
        }
        for (Method method : klass.getMethods()) {
            if (method.getName().contains("$"))
                continue;
            String key = method.getName() + "$" + Type.getMethodDescriptor(method);
            FastMethod fm = FastMethodFactory.make(method);
            methods.put(key, fm);
        }
        return new FastClassImpl(klass, constructors, methods, fields);
    }
}