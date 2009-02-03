package com.zzh.ioc.db;

import com.zzh.dao.Dao;
import com.zzh.ioc.Assemble;
import com.zzh.ioc.Mapping;
import com.zzh.service.NutEntityService;

public class DatabaseAssemble implements Assemble {

	private NutEntityService<ObjectBean> objsvc;

	public DatabaseAssemble(Dao dao) {
		objsvc = new NutEntityService<ObjectBean>(dao) {
		};
	}

	@Override
	public Mapping getMapping(String name) {
		ObjectBean obj = objsvc.fetch(name);
		objsvc.dao().fetchMany(obj, "fields");
		return new ObjectMapping(obj);
	}

}
