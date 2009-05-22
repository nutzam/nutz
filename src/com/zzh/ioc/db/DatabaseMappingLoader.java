package com.zzh.ioc.db;

import java.util.List;

import com.zzh.dao.Dao;
import com.zzh.ioc.Mapping;
import com.zzh.ioc.MappingLoader;
import com.zzh.ioc.meta.MappingBean;
import com.zzh.ioc.meta.Obj;
import com.zzh.ioc.meta.ObjService;

public class DatabaseMappingLoader implements MappingLoader {

	public DatabaseMappingLoader(Dao dao) {
		service = new ObjService(dao);
	}

	private ObjService service;

	@Override
	public Mapping load(String name) {
		Obj obj = service.fetchObject(name);
		if (null == obj)
			throw new RuntimeException("Fail to find in DB!");
		return new MappingBean(obj);
	}

	@Override
	public String[] keys() {
		List<Obj> objs = service.objs().query(null, null);
		String[] re = new String[objs.size()];
		int i = 0;
		for (Obj obj : objs) {
			re[i++] = obj.getName();
		}
		return re;
	}

}
