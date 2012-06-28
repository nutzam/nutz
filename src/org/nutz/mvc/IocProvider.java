package org.nutz.mvc;

import org.nutz.ioc.Ioc;

/**
 * 为了更灵活的支持更多种类的反转注入容器，特创建此接口。
 * <p>
 * Nutz.Mvc 框架通过这个接口来创建 Ioc 容器。通过主模块的 '@IocBy' 注解，框架能找到这个接口的 实现类，以及提供给这个接口
 * create 函数所需的必要参数。
 * <p>
 * 这是一个重要的扩展点，通过这个扩展点， Nutz.Mvc 可以和 Spring, Guice 等其他 Ioc 容器很好的结合， 只要实现 Ioc
 * 接口，以及添加一个 IocProvider 即可。
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public interface IocProvider {

    Ioc create(NutConfig config, String[] args);

}
