package com.zzh.mvc;

import com.zzh.ioc.Mapping;
import com.zzh.ioc.MappingLoader;

public class MvcMappingLoader implements MappingLoader {

	private MappingLoader loader;

	public MvcMappingLoader(MappingLoader loader) {
		this.loader = loader;
	}

	@Override
	public Mapping load(String name) {
		Mapping re = loader.load(name);
		if (null != re.getObjectType() && Action.class.isAssignableFrom(re.getObjectType()))
			re.setSingleton(false);
		return re;
	}

}
