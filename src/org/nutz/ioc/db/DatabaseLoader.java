package org.nutz.ioc.db;

import java.util.List;

import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.ObjLoader;
import org.nutz.ioc.meta.Obj;
import org.nutz.ioc.meta.ObjService;

public class DatabaseLoader implements ObjLoader {

	public DatabaseLoader(Dao dao) {
		service = new ObjService(dao);
	}

	private ObjService service;

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

	@Override
	public Obj load(String name) {
		return service.fetchObject(name);
	}

	@Override
	public boolean hasObj(String name) {
		return service.objs().count(Cnd.where("name", "=", name)) > 0;
	}

}
