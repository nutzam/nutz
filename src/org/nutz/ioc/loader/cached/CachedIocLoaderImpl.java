package org.nutz.ioc.loader.cached;

import java.util.HashMap;
import java.util.Map;

import org.nutz.ioc.IocLoader;
import org.nutz.ioc.ObjectLoadException;
import org.nutz.ioc.meta.IocObject;

/**
 * 简单的带缓存的IocLoader <b/>仅对singleton == true的IocObject对象进行缓存,
 * 
 * @author wendal(wendal1985@gmail.com)
 * 
 */
public class CachedIocLoaderImpl implements CachedIocLoader {

	public static CachedIocLoaderImpl createCachedIocLoaderImpl(IocLoader proxyIocLoader) {
		return new CachedIocLoaderImpl(proxyIocLoader);
	}

	private IocLoader proxyIocLoader;

	private Map<String, IocObject> map;

	private CachedIocLoaderImpl(IocLoader proxyIocLoader) {
		this.proxyIocLoader = proxyIocLoader;
		this.map = new HashMap<String, IocObject>();
	}

	public void clear() {
		map.clear();
	}

	public String[] getName() {
		return proxyIocLoader.getName();
	}

	public boolean has(String name) {
		return proxyIocLoader.has(name);
	}

	public IocObject load(String name) throws ObjectLoadException {
		IocObject iocObject = map.get(name);
		if (iocObject == null) {
			iocObject = proxyIocLoader.load(name);
			if (iocObject.isSingleton())
				map.put(name, iocObject);
		}
		return iocObject;
	}
}
