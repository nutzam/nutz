package org.nutz.ioc;

import org.nutz.ioc.meta.IocValue;

/**
 * 本接口设计用来提供容器支持更多种类的值的类型。
 * <p>
 * 你可以通过 Ioc 接口，增加你自定义的 ValueProxyMaker，你自定义的 ValueProxyMaker 会 比容器内内置的
 * ValueProxyMaker 更优先。即，后加入优先
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * 
 * @see org.nutz.ioc.Ioc2
 */
public interface ValueProxyMaker {

    /**
     * 通过这个函数， NutIoc 会传递给 Loader 的 IocLoading 类会集中你的类型，帮助你从一个 Map 获得 一个
     * IocObject
     * 
     * @return 一个字符串数组，表示你支持的 Value类型
     */
    String[] supportedTypes();

    /**
     * 如果返回的是 null，则表示当前的实现不能解释这个 IocValue，容器会调用下一个 ValueProxyMaker 来做解析
     * 
     * @param iv
     *            字段装配信息
     * @param ing
     *            对象装配时
     * 
     * @return 值代理对象
     */
    ValueProxy make(IocMaking ing, IocValue iv);

}
