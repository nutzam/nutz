package org.nutz.aop;

/**
 * 类定义的代理
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public interface ClassAgent {

    /**
     * 定义一个新的类对象
     * 
     * @param cd
     *            字节码生成器
     * @param klass
     *            参照类对象
     * @return 新的类对象
     */
    <T> Class<T> define(ClassDefiner cd, Class<T> klass);

    /**
     * 添加拦截器
     * 
     * @param matcher
     *            方法匹配器
     * @param inte
     *            拦截器
     * @return 添加完成后的ClassAgent
     */
    ClassAgent addInterceptor(MethodMatcher matcher, MethodInterceptor inte);

    String CLASSNAME_SUFFIX = "$$NUTZAOP";
}
