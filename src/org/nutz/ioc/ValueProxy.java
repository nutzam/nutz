package org.nutz.ioc;

/**
 * 注入值的代理。
 * <p>
 * 对象的注入值可能是对象的构造函数，或者字段属性。通过 ValueProxyMaker （用户可以自定义） 框架可以解释多种 Val。
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * 
 * @see org.nutz.ioc.ValueProxyMaker
 * @see org.nutz.ioc.meta.IocValue
 * 
 */
public interface ValueProxy {

    Object get(IocMaking ing);

}
