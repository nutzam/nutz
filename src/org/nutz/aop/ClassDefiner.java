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
    Class<?> define(String className, byte[] bytes, ClassLoader c);
}
