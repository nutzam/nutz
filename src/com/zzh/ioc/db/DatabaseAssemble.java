package com.zzh.ioc.db;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

	@Override
	public List<String> names() {
		List<ObjectBean> beans = objsvc.query(null, null);
		List<String> re = new ArrayList<String>(beans.size());
		for (Iterator<ObjectBean> it = beans.iterator(); it.hasNext();) {
			re.add(it.next().getName());
		}
		return re;
	}

}
