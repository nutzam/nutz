package org.nutz.ioc.loader.cached;

import org.nutz.ioc.IocLoader;

/**
 * 带缓存的IocLoader，不用考虑线程安全性
 * 
 */
public interface CachedIocLoader extends IocLoader {

    void clear();

}
