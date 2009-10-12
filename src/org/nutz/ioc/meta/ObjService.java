package org.nutz.ioc.meta;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;

import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.json.Json;
import org.nutz.lang.Lang;
import org.nutz.service.EntityService;
import org.nutz.service.IdEntityService;
import org.nutz.service.IdNameEntityService;
import org.nutz.service.Service;
import org.nutz.trans.Atom;
import org.nutz.trans.Trans;

public class ObjService extends Service {

	public ObjService(Dao dao) {
		super(dao);
		objs = new IdNameEntityService<Obj>(dao) {};
		flds = new IdNameEntityService<Fld>(dao) {};
		vals = new IdEntityService<Val>(dao) {};
		lifecycles = new IdEntityService<Lifecycle>(dao) {};
	}

	private IdNameEntityService<Obj> objs;
	private IdEntityService<Val> vals;
	private EntityService<Fld> flds;
	private IdEntityService<Lifecycle> lifecycles;

	public IdNameEntityService<Obj> objs() {
		return objs;
	}

	public Obj fetchObject(int id) {
		return fetchObject(objs.fetch(id));
	}

	public Obj fetchObject(String name) {
		return fetchObject(objs.fetch(name));
	}

	private static Val evalVal(Val val) {
		if (null == val || val.getValue() == null) {
			return Val.make(Val.Null, null);
		} else if (val.isNull()) {
			val.setValue(null);
		} else if (val.isMap()) {
			Map<?, ?> map = (Map<?, ?>) Json.fromJson(val.getValue().toString());
			val.setValue(Map2Obj.parseMap(map));
		} else if (val.isArray()
				&& !(val.getValue() instanceof Collection<?> || val.getValue().getClass().isArray())) {
			Collection<?> coll = (Collection<?>) Json.fromJson(val.getValue().toString());
			val.setValue(Map2Obj.parseCollection(coll));
		} else if (val.isInner() && !(val.getValue() instanceof Obj)) {
			Map<?, ?> map = (Map<?, ?>) Json.fromJson(val.getValue().toString());
			val.setValue(Map2Obj.parse(map));
		} else if (val.isBoolean() && !(val.getValue() instanceof Boolean)) {
			val.setValue(Lang.parseBoolean(val.getValue().toString()));
		}
		return val;
	}

	private void evalObj(Obj obj) {
		if (null != obj.getArgs())
			for (int i = 0; i < obj.getArgs().length; i++) {
				Val v = obj.getArgs()[i];
				Val nv = evalVal(v);
				if (v != nv)
					obj.getArgs()[i] = nv;
			}
		if (null != obj.getFields())
			for (Fld f : obj.getFields()) {
				if (null == f.getVal())
					dao().fetchLinks(f, "val");
				Val nv = evalVal(f.getVal());
				if (f.getVal() != nv)
					f.setVal(nv);
			}
	}

	public Obj fetchObject(Obj obj) {
		if (null != obj) {
			dao().fetchLinks(obj, "args|fields|lifecycle");
			Arrays.sort(obj.getArgs(), new Comparator<Val>() {
				public int compare(Val o1, Val o2) {
					return o1.getId() == o2.getId() ? 0 : (o1.getId() < o2.getId() ? -1 : 1);
				}
			});
			Arrays.sort(obj.getFields(), new Comparator<Fld>() {
				public int compare(Fld o1, Fld o2) {
					return o1.getValueId() == o2.getValueId() ? 0 : (o1.getValueId() < o2
							.getValueId() ? -1 : 1);
				}
			});
			evalObj(obj);
		}
		return obj;
	}

	public Obj insertObj(final Obj obj) {
		Trans.exec(new Atom() {
			public void run() {
				dao().insert(obj);
				if (null != obj.getLifecycle())
					obj.getLifecycle().setId(obj.getId());
				dao().insertLinks(obj, "args|lifecycle");
				if (null != obj.getFields())
					for (Fld f : obj.getFields()) {
						f.setObjectId(obj.getId());
						dao().insertWith(f, "val");
					}
			}
		});
		return obj;
	}

	public Obj updateObj(final Obj obj) {
		Trans.exec(new Atom() {
			public void run() {
				emptyObj(obj.getId());
				if (null != obj.getLifecycle()) {
					obj.getLifecycle().setId(obj.getId());
					dao().insert(obj.getLifecycle());
				}
				dao().update(obj);
				if (null != obj.getFields())
					for (Fld f : obj.getFields()) {
						f.setObjectId(obj.getId());
						dao().insertWith(f, "val");
					}
				dao().insertLinks(obj, "args");
			}
		});
		return obj;
	}

	private void emptyObj(final int id) {
		Trans.exec(new Atom() {
			public void run() {
				vals.clear(Cnd.format("(id IN (SELECT vid FROM nut_obj_args WHERE oid=%d)"
						+ "OR id IN (SELECT vid FROM nut_field WHERE oid=%d))", id, id));
				dao().clear("nut_obj_args", Cnd.format("oid=%d", id));
				flds.clear(Cnd.where("objectId", "=", id));
				lifecycles.delete(id);
			}
		});
	}

	public void deleteObj(final int id) {
		Trans.exec(new Atom() {
			public void run() {
				emptyObj(id);
				objs.delete(id);
			}
		});
	}
}
