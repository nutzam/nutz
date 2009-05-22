package com.zzh.ioc.meta;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;

import com.zzh.dao.Cnd;
import com.zzh.dao.Dao;
import com.zzh.json.Json;
import com.zzh.mvc.Return;
import com.zzh.service.EntityService;
import com.zzh.service.IdEntityService;
import com.zzh.service.IdNameEntityService;
import com.zzh.service.Service;
import com.zzh.trans.Atom;
import com.zzh.trans.Trans;

public class ObjService extends Service {

	public ObjService(Dao dao) {
		super(dao);
		objs = new IdNameEntityService<Obj>(dao) {};
		flds = new IdNameEntityService<Fld>(dao) {};
		vals = new IdEntityService<Val>(dao) {};
	}

	private IdNameEntityService<Obj> objs;
	private IdEntityService<Val> vals;
	private EntityService<Fld> flds;

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
				&& !(val.getValue() instanceof Collection || val.getValue().getClass().isArray())) {
			Collection<?> coll = (Collection<?>) Json.fromJson(val.getValue().toString());
			val.setValue(Map2Obj.parseCollection(coll));
		} else if (val.isInner() && !(val.getValue() instanceof Obj)) {
			Map<?, ?> map = (Map<?, ?>) Json.fromJson(val.getValue().toString());
			val.setValue(Map2Obj.parse(map));
		} else if (val.isBoolean() && !(val.getValue() instanceof Boolean)) {
			val.setValue(Boolean.parseBoolean(val.getValue().toString()));
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
			dao().fetchLinks(obj, "args|fields");
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
				dao().insertWith(obj, "args");
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

	public void emptyObj(final int id) {
		Trans.exec(new Atom() {
			public void run() {
				vals.clear(Cnd.format("(id IN (SELECT vid FROM nut_obj_args WHERE oid=%d)"
						+ "OR id IN (SELECT vid FROM nut_field WHERE oid=%d))", id, id));
				dao().clear("nut_obj_args", Cnd.format("oid=%d", id));
				flds.clear(Cnd.where("objectId", "=", id));
			}
		});
	}

	public Return deleteObj(final int id) {
		try {
			Trans.exec(new Atom() {
				public void run() {
					emptyObj(id);
					objs.delete(id);
				}
			});
		} catch (Exception e) {
			return Return.fail("%s", e.getMessage());
		}
		return Return.OK();
	}
}
