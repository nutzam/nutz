package org.nutz.aop;

/**
 * 根据字节码，定义一个 Class 文件
 * <p>
 * 它就是一种 ClassLoader，如果已经定义过的 Class，它将不再重复定义
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public interface ClassDefiner {

    /**
     * 根据字节码，定义一个 Class 文件，如果已经定义过的 Class，它将不再重复定义
     * 
     * @param className
     *            一个类的全名
     * @param bytes
     *            字节码
     * @return 类对象
     * @throws ClassFormatError
     *             字节码格式错误
     */
    Class<?> define(String className, byte[] bytes) throws ClassFormatError;

    /**
     * @param className
     *            一个类全名
     * @return 是否在缓存中存在这个类的定义
     */
    boolean has(String className);

    /**
     * @param className
     *            一个类的全名
     * @return 缓存中的类定义
     * @throws ClassNotFoundException
     *             如果缓存中没有这个类定义
     */
    Class<?> load(String className) throws ClassNotFoundException;
}
