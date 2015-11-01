package org.nutz.lang.reflect;

import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;

@SuppressWarnings({"unchecked", "rawtypes"})
public class ReflectTool {

    private static Method DEFINE_CLASS;
    private static final ProtectionDomain PROTECTION_DOMAIN;

    static {
        PROTECTION_DOMAIN = getProtectionDomain(ReflectTool.class);

        AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
                try {
                    Class loader = Class.forName("java.lang.ClassLoader"); // JVM
                                                                           // crash
                                                                           // w/o
                                                                           // this
                    DEFINE_CLASS = loader.getDeclaredMethod("defineClass",
                                                            new Class[]{String.class,
                                                                        byte[].class,
                                                                        Integer.TYPE,
                                                                        Integer.TYPE,
                                                                        ProtectionDomain.class});
                    DEFINE_CLASS.setAccessible(true);
                }
                catch (ClassNotFoundException e) {
                    // Lang.impossible();
                }
                catch (NoSuchMethodException e) {
                    // Lang.impossible();
                }
                return null;
            }
        });
    }

    public static ProtectionDomain getProtectionDomain(final Class source) {
        if (source == null) {
            return null;
        }
        return (ProtectionDomain) AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
                return source.getProtectionDomain();
            }
        });
    }

    public static Class defineClass(String className, byte[] b, ClassLoader loader)
            throws Exception {
        return defineClass(className, b, loader, PROTECTION_DOMAIN);
    }

    public static Class defineClass(String className,
                                    byte[] b,
                                    ClassLoader loader,
                                    ProtectionDomain protectionDomain) throws Exception {
        Object[] args = new Object[]{className,
                                     b,
                                     new Integer(0),
                                     new Integer(b.length),
                                     protectionDomain};
        if (loader == null)
            loader = ReflectTool.class.getClassLoader();
        Class c = (Class) DEFINE_CLASS.invoke(loader, args);
        // Force static initializers to run.
        Class.forName(className, true, loader);
        return c;
    }
}
