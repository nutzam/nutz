package org.nutz.ioc;

import org.nutz.ioc.meta.IocObject;

/**
 * 这个接口封装了对象注入逻辑。生成对象代理。
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * 
 * @see org.nutz.ioc.ObjectProxy
 */
public interface ObjectMaker {

    /**
     * 根据 IocObject 制作一个对象代理，如果对象是 singleton，则保存在上下文环境中
     */
    ObjectProxy make(IocMaking ing, IocObject iobj);

}
