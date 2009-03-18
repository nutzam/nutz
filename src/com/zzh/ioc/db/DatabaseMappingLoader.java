package com.zzh.ioc.db;

import java.util.Iterator;

import com.zzh.dao.Dao;
import com.zzh.ioc.Mapping;
import com.zzh.ioc.MappingLoader;
import com.zzh.service.IdNameEntityService;

public class DatabaseMappingLoader implements MappingLoader {

	public DatabaseMappingLoader(Dao dao) {
		objsv = new IdNameEntityService<ObjectBean>(dao) {};
	}

	private IdNameEntityService<ObjectBean> objsv;

	@Override
	public Mapping load(String name) {
		ObjectBean ob = objsv.fetch(name);
		if (null == ob)
			return null;
		objsv.dao().fetchManyMany(ob, "args");
		objsv.dao().fetchMany(ob, "fields");
		if(null!=ob.getFields()){
			for(Iterator<FieldBean> it = ob.getFields().iterator();it.hasNext();){
				FieldBean fb = it.next();
				objsv.dao().fetchOne(fb, "value");
			}
		}
		return new ObjectMapping(ob);
	}

}
