package org.nutz.dao.entity.next;

import java.lang.reflect.Field;

import org.nutz.dao.Dao;
import org.nutz.dao.TableName;
import org.nutz.dao.sql.Sql;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.inject.Injecting;

class NextQueryImpl implements NextQuery {

	private Injecting inj;
	private String[] vars;
	private Field[] params;
	private Sql sql;

	protected NextQueryImpl(Sql sql, Field field) {
		Mirror<?> mirror = Mirror.me(field.getDeclaringClass());
		// Injecting
		this.inj = mirror.getInjecting(field.getName());

		// Dynamic Variables
		vars = sql.vars().keys().toArray(new String[sql.vars().size()]);

		// Params
		params = new Field[sql.params().size()];
		int i = 0;
		for (String nm : sql.params().keys()) {
			try {
				params[i] = mirror.getField(nm);
			} catch (NoSuchFieldException e) {
				throw Lang.makeThrow("'@%s' didn't exists, please check @Next on '%s'.'%s'", nm,
						mirror.getType().getName(), field.getName());
			}
		}

		// Store Sql
		this.sql = sql;
	}

	public void update(Dao dao, Object obj) {
		if (null == obj)
			return;
		// Duplicate SQL
		Sql sql = this.sql.duplicate();

		// Set Dynamic Variables
		if (vars.length > 0) {
			Object refer = TableName.get();
			if (null != refer) {
				if (TableName.isPrimitive(refer))
					for (String var : vars)
						sql.vars().set(var, refer);
				else {
					Mirror<?> me = Mirror.me(refer.getClass());
					for (String var : vars) {
						Object v = me.getValue(refer, var);
						sql.vars().set(var, v);
					}
				}
			}
		}
		// Set Params
		if (params.length > 0) {
			Mirror<?> me = Mirror.me(obj.getClass());
			for (Field f : params) {
				Object v = me.getValue(obj, f);
				sql.params().set(f.getName(), v);
			}
		}

		// Run
		dao.execute(sql);

		// Inject
		inj.inject(obj, sql.getResult());
	}
}
