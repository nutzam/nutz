package org.nutz.aop;

import java.security.AccessController;
import java.security.PrivilegedAction;

import org.nutz.lang.Lang;

/**
 * 一个默认的类加载器
 * 
 * @author Wendal(wendal1985@gmail.com)
 */
public class DefaultClassDefiner extends ClassLoader implements ClassDefiner {
	
	protected static ClassDefiner one;
	
	protected static ClassLoader moreClassLoader;
	
	public static void init(ClassLoader cd) {
		if (one == null) {
			synchronized (DefaultClassDefiner.class) {
				if (one == null) {
					AccessController.doPrivileged(new PrivilegedAction<DefaultClassDefiner>() {
			            public DefaultClassDefiner run() {
			            	one = new DefaultClassDefiner(DefaultClassDefiner.class.getClassLoader());
			                return (DefaultClassDefiner) DefaultClassDefiner.defaultOne();
			            }
			        });
				}
			}
		}
		if (moreClassLoader == null)
			moreClassLoader = cd;
	}

	public static ClassDefiner defaultOne() {
		if (one == null)
			init(null);
		return one;
	}
	
	public static final Class<?> def(String className, byte[] bytes) {
		try {
			return defaultOne().load(className);
		} catch (Throwable e) {
			// TODO: handle exception
		}
		return defaultOne().define(className, bytes);
	}
	
	/**
	 * 虽然是public的,但一般情况下不需要用哦. 用默认的全局ClassDefiner就很好.
	 */
    public DefaultClassDefiner(ClassLoader parent) {
        super(parent);
    }

    public Class<?> define(String className, byte[] bytes) throws ClassFormatError {
        try {
            return load(className);
        }
        catch (ClassNotFoundException e) {}
        // If not found ...
        return defineClass(className, bytes, 0, bytes.length);
    }

    public boolean has(String className) {
        try {
            load(className);
            return true;
        }
        catch (ClassNotFoundException e) {}
        return false;
    }

    public Class<?> load(String className) throws ClassNotFoundException {
        try {
            return Lang.loadClass(className);
        }
        catch (ClassNotFoundException e) {
            try {
                return ClassLoader.getSystemClassLoader().loadClass(className);
            }
            catch (ClassNotFoundException e2) {
                try {
                    return getParent().loadClass(className);
                }
                catch (ClassNotFoundException e3) {}
            }
            catch (SecurityException e2) {// Fix for GAE 1.3.7, Fix issue 296
                try {
                    return getParent().loadClass(className);
                }
                catch (ClassNotFoundException e3) {}
            }
        }
        if (moreClassLoader != null) {
        	try {
				return moreClassLoader.loadClass(className);
			} catch (Throwable e) {
			}
        }
        return super.loadClass(className);
    }
    
    public static void reset() {
    	one = null;
    	defaultOne();
    }
}
