package org.nutz.aop;

import org.nutz.lang.Files;
import org.nutz.lang.reflect.ReflectTool;

/**
 * 一个默认的类定义实现
 * 
 * @author Wendal(wendal1985@gmail.com)
 */
public class DefaultClassDefiner implements ClassDefiner {

    public static String debugDir;

    private static ClassDefiner me = new DefaultClassDefiner();

    public static ClassDefiner defaultOne() {
        return me;
    }

    public Class<?> define(String className, byte[] bytes, ClassLoader loader) {
        try {
            if (debugDir != null)
                Files.write(debugDir + className.replace('.', '/') + ".class", bytes);
            return ReflectTool.defineClass(className, bytes, loader);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
