package org.nutz.lang.util;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通过读取Class文件,获得方法形参名称列表
 * @author wendal(wendal1985@gmail.com)
 * 
 */
public class MethodParamNamesScaner {
    
    /**
     * 获取Method的形参名称列表
     * @param method 需要解析的方法
     * @return 形参名称列表,如果没有调试信息,将返回null
     */
    public static List<String> getParamNames(Method method) {
        try {
            int size = method.getParameterTypes().length;
            if (size == 0)
                return new ArrayList<String>(0);
            List<String> list = ClassMetaReader.getParamNames(method.getDeclaringClass()).get(ClassMetaReader.getKey(method));
            if (list == null)
                return null;
            if (list.size() == size)
                return list;
            if (list.size() > size)
                return list.subList(0, size);
            return null;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * 获取Constructor的形参名称列表
     * @param constructor 需要解析的构造函数
     * @return 形参名称列表,如果没有调试信息,将返回null
     */
    public static List<String> getParamNames(Constructor<?> constructor) {
        try {
            int size = constructor.getParameterTypes().length;
            if (size == 0)
                return new ArrayList<String>(0);
            List<String> list =  ClassMetaReader.getParamNames(constructor.getDeclaringClass()).get(ClassMetaReader.getKey(constructor));
            if (list != null && list.size() != size)
                return list.subList(0, size);
            return list;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
    
    public static Map<String, List<String>> getParamNames(Class<?> klass) throws IOException {
        String key = klass.getName();
        if (caches.containsKey(key))
            return caches.get(key);
        InputStream in = klass.getResourceAsStream("/" + klass.getName().replace('.', '/') + ".class");        
        Map<String, List<String>> names = getParamNames(in);
        caches.put(key, names);
        return names;
    }
    
    public static Map<String, List<String>> getParamNames(InputStream ins) throws IOException {
        if (ins == null)
            return new HashMap<String, List<String>>();
        return ClassMetaReader.build(ins).paramNames;
    }
    
    protected static Map<String, Map<String, List<String>>> caches = new HashMap<String, Map<String,List<String>>>();
}
