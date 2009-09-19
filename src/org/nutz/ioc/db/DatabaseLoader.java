package org.nutz.ioc.db;

import java.util.List;

import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.ioc.ObjLoader;
import org.nutz.ioc.meta.Obj;
import org.nutz.ioc.meta.ObjService;

public class DatabaseLoader implements ObjLoader {

	public DatabaseLoader(Dao dao) {
		service = new ObjService(dao);
		if (!dao.exists(Obj.class))
			synchronized (dao) {
				if (!dao.exists(Obj.class)) {
					Sqls.executeDefinitionFile(dao, "org/nutz/ioc/meta/ioc.dod");
				}
			}
	}

	private ObjService service;

	public String[] keys() {
		List<Obj> objs = service.objs().query(null, null);
		String[] re = new String[objs.size()];
		int i = 0;
		for (Obj obj : objs) {
			re[i++] = obj.getName();
		}
		return re;
	}

	public Obj load(String name) {
		return service.fetchObject(name);
	}

	public boolean hasObj(String name) {
		return service.objs().count(Cnd.where("name", "=", name)) > 0;
	}

}
