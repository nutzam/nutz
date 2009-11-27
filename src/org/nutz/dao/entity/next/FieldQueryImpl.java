package org.nutz.dao.entity.next;

import java.lang.reflect.Field;
import java.util.Set;

import org.nutz.dao.Dao;
import org.nutz.dao.TableName;
import org.nutz.dao.entity.EntityField;
import org.nutz.dao.sql.Sql;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;

class FieldQueryImpl implements FieldQuery {

	private String[] vars;
	private Field[] params;
	private Sql sql;
	private EntityField ef;

	protected FieldQueryImpl(Sql sql, EntityField ef) {
		this.ef = ef;
		Mirror<?> mirror = ef.getEntity().getMirror();
		// Dynamic Variables
		Set<String> set = sql.varIndex().names();
		vars = set.toArray(new String[set.size()]);

		// Params
		set = sql.paramIndex().names();
		params = new Field[set.size()];
		int i = 0;
		for (String nm : set) {
			try {
				params[i] = mirror.getField(nm);
			} catch (NoSuchFieldException e) {
				throw Lang.makeThrow("'@%s' didn't exists, please check @Next on '%s'.'%s'", nm,
						mirror.getType().getName(), ef.getName());
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
			sql.vars().set("view", ef.getEntity().getViewName());
			sql.vars().set("field", ef.getColumnName());
			Object refer = TableName.get();
			if (null != refer) {
				if (TableName.isPrimitive(refer))
					for (String var : vars)
						if (var.equals("view") || var.equals("field"))
							continue;
						else
							sql.vars().set(var, refer);
				else {
					Mirror<?> me = Mirror.me(refer.getClass());
					for (String var : vars) {
						if (var.equals("view") || var.equals("field"))
							continue;
						else {
							Object v = me.getValue(refer, var);
							sql.vars().set(var, v);
						}
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
		ef.setValue(obj, sql.getResult());
	}
}
