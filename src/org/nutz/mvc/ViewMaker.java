package org.nutz.mvc;

import org.nutz.ioc.Ioc;

/**
 * 这是一个扩展点，你可以通过实现这个接口，让注解 @Ok和 @Fail 支持更多的模板引擎。 这两个注解值的格式为：
 * 
 * <pre>
 * 视图类型:值
 * </pre>
 * 
 * 比如 jsp:abc.bbc.cbc
 * <p>
 * 为了支持更多的模板引擎，你可以自己实现一个 View，以及一个 ViewMaker。在默认模块类上用 '@Views' 注解声明你的 ViewMaker
 * 即可
 * <p>
 * <b>!!!请注意:</b>，你的实现类必须有一个 public 的默认构造函数，否则，框架将不知道如何实例化你的类。
 * 
 * @author zozoh
 * 
 */
public interface ViewMaker {

    /**
     * 注解 '@Ok' 和 '@Fail' 的值是个字符串，用户可以随意定义。字符串从第一个冒号处拆成两半
     * 前半部分会转换成小写，作为视图类型，后一半为视图的值。
     * 
     * @param ioc
     *            整个应用的的 Ioc。 如果默认模块没有声明 '@IocBy' 这个参数为 null
     * @param type
     *            视图的类型
     * @param value
     *            视图的值
     * 
     * @return 产生的视图对象
     */
    View make(Ioc ioc, String type, String value); //by wendal, 我很想把Ioc改成ActionInfo

}
