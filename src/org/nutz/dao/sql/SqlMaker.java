package org.nutz.dao.sql;

import static java.lang.String.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.nutz.dao.Chain;
import org.nutz.dao.FieldFilter;
import org.nutz.dao.FieldMatcher;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.EntityField;

public class SqlMaker {

	private static String TN = "tableName";
	private static String TNV = "$" + TN;
	private static String CND = "condition";
	private static String CNDV = "$" + CND;

	public Patterns ptn = new Patterns();

	private String L(String sql) {
		return format(sql, TNV);
	}

	private String L2(String sql) {
		return format(sql, TNV, CNDV);
	}

	public class Patterns {
		public String COUNT = L2("SELECT COUNT(*) FROM %s %s");
		public String MAX = L("SELECT MAX($field) FROM %s");
		public String CLEAR = L2("DELETE FROM %s %s");
		public String RESET = L("TRUNCATE TABLE %s");
		public String CLEARS_LINKS = L("DELETE FROM %s WHERE $field=@value");
		public String DELETE = L("DELETE FROM $%s WHERE $pk=@pk");
		public String FETCH = L("SELECT * FROM $%s WHERE $pk=@pk");
		public String FETCH_LOWER = L("SELECT $* FROM %s WHERE LOWER($pk)=LOWER(@pk)");
		public String QUERY = L2("SELECT * FROM %s %s");
		public String UPDATE = L("UPDATE $%s SET $fields WHERE $pk=@pk");
		public String UPDATE_BATCH = L2("UPDATE %s SET $fields %s");
		public String INSERT = L("INSERT INTO %s ($fields) VALUES($values)");
		public String INSERT_MANYMANY = L("INSERT INTO %s ($from,$to) VALUES(@from,@to)");
	}

	public Sql updateBatch(String tableName, Chain chain) {
		Sql sql = SQLs.create(ptn.UPDATE_BATCH);
		sql.vars().set(TN, tableName).set(CND, CNDV);
		Chain c = chain.head();
		while (c != null) {
			sql.params().set(c.name(), "@" + c.name());
			c = c.next();
		}
		return SQLs.create(sql.toString());
	}

	public Sql update(Entity<?> en, Object obj) {
		SqlImpl sql = (SqlImpl) SQLs.create(ptn.UPDATE);
		StringBuilder sb = new StringBuilder();
		FieldMatcher fm = FieldFilter.get(en.getType());
		Map<String, Object> map = new HashMap<String, Object>();
		for (Iterator<EntityField> it = en.fields().iterator(); it.hasNext();) {
			EntityField ef = it.next();
			String fn = ef.getField().getName();
			if (ef.isId() || ef.isReadonly())
				continue;
			if (null != fm) {
				if (fm.isIgnoreNull() && null == ef.getValue(obj))
					continue;
				else if (!fm.match(fn))
					continue;
			}
			sb.append(',').append(ef.getColumnName()).append('=').append("@").append(fn);
			map.put(fn, ef.getValue(obj));
		}
		sb.deleteCharAt(0);
		EntityField idf = en.getIdentifiedField();
		sql.vars().set("fields", sb);
		sql.vars().set("pk", idf.getColumnName());
		String fn = idf.getField().getName();
		sql.params().set("pk", "@" + fn);
		SqlImpl re = (SqlImpl) SQLs.create(sql.toString()).setEntity(en);
		re.params().set(fn, idf.getValue(obj)).putAll(map);
		re.vars().set(TN, en.getTableName());
		return re;
	}

	public Sql create(String sql, String tableName) {
		Sql re = SQLs.create(sql);
		re.vars().set(TN, tableName);
		return re;
	}

	public Sql fetch(Entity<?> entity,EntityField ef) {
		Sql sql;
		if (ef.isName() && ef.isCaseUnsensitive()) {
			sql = create(ptn.FETCH_LOWER, entity.getTableName());
		} else {
			sql = create(ptn.FETCH, entity.getTableName());
		}
		sql.vars().set("pk", ef.getColumnName());
		sql.params().set("pk", "@" + ef.getField().getName());
		return SQLs.create(sql.toString()).setEntity(entity).setCallback(SQLs.callback.fetch());
	}

	public Sql query(String tableName) {
		return create(ptn.QUERY, tableName).setCallback(SQLs.callback.query());
	}

	public Sql insert(Entity<?> en, Object obj) {
		SqlImpl sql = (SqlImpl) SQLs.create(ptn.INSERT);
		sql.vars().set(TN, en.getTableName());
		StringBuilder fields = new StringBuilder();
		StringBuilder values = new StringBuilder();
		FieldMatcher fm = FieldFilter.get(en.getType());
		Map<String, Object> map = new HashMap<String, Object>();
		for (Iterator<EntityField> it = en.fields().iterator(); it.hasNext();) {
			EntityField ef = it.next();
			String fn = ef.getField().getName();
			if (ef.isAutoIncrement() || ef.isReadonly())
				continue;
			if (null != fm) {
				if (fm.isIgnoreNull() && null == ef.getValue(obj))
					continue;
				else if (!fm.match(fn))
					continue;
			}
			if (null != obj && !ef.hasDefaultValue() && null == ef.getValue(obj))
				continue;
			// fields.append(SQLUtils.formatName(ef.getColumnName()));
			fields.append(',').append(ef.getColumnName());
			values.append(", @").append(fn);
			map.put(fn, ef.getValue(obj));
		}
		fields.deleteCharAt(0);
		values.deleteCharAt(0);
		sql.vars().set("fields", fields).set("values", values);
		SqlImpl re = (SqlImpl) SQLs.create(sql.toString()).setEntity(en);
		re.params().putAll(map);
		return re;
	}

}
