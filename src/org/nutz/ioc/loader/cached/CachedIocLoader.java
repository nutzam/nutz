package org.nutz.ioc.loader.cached;

import org.nutz.ioc.IocLoader;

/**
 * 带缓存的IocLoader
 * 
 */
public interface CachedIocLoader extends IocLoader {

	void clear();

}
