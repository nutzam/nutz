package org.nutz.dao.sql;

import static java.lang.String.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.nutz.dao.Chain;
import org.nutz.dao.Condition;
import org.nutz.dao.FieldFilter;
import org.nutz.dao.FieldMatcher;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.EntityField;
import org.nutz.dao.entity.Link;

public class SqlMaker {

	public Sql insert_manymany(Link link) {
		return Sqls.create(format("INSERT INTO %s (%s,%s) VALUES(@%s,@%s)", link.getRelation(),
				link.getFrom(), link.getTo(), link.getFrom(), link.getTo()));
	}

	private static String evalActivedFields(Entity<?> en) {
		FieldMatcher fm = FieldFilter.get(en.getType());
		if (null != fm) {
			StringBuilder sb = new StringBuilder();
			for (Iterator<EntityField> it = en.fields().iterator(); it.hasNext();) {
				EntityField enf = it.next();
				if (fm.match(enf.getField().getName())) {
					sb.append(enf.getColumnName()).append(',');
				}
			}
			if (sb.length() > 0) {
				sb.deleteCharAt(sb.length() - 1);
				return sb.toString();
			}
		}
		return "*";
	}

	public Sql insert(Entity<?> en, Object obj) {
		StringBuilder fields = new StringBuilder();
		StringBuilder values = new StringBuilder();
		FieldMatcher fm = FieldFilter.get(en.getType());
		Map<String, Object> map = new HashMap<String, Object>();
		for (Iterator<EntityField> it = en.fields().iterator(); it.hasNext();) {
			EntityField ef = it.next();
			String fn = ef.getFieldName();
			if (ef.isSerial() || ef.isReadonly())
				continue;
			Object value = ef.getValue(obj);
			// match FieldMatcher
			if (null != fm) {
				if (fm.isIgnoreNull() && null == value)
					continue;
				else if (!fm.match(fn))
					continue;
			}
			// Auto generate value
			else if (null != ef.getNextIntQuerySql()) {
				fields.append(',').append(ef.getColumnName());
				values.append(",(").append(ef.getNextIntQuerySql().toString()).append(')');
				continue;
			}
			// for Null
			else if (null == value) {
				if (ef.hasDefaultValue())
					value = ef.getDefaultValue(obj);
				else
					continue;
			}
			fields.append(',').append(ef.getColumnName());
			values.append(", @").append(fn);
			map.put(fn, value);
		}
		fields.deleteCharAt(0);
		values.deleteCharAt(0);
		Sql sql = Sqls.create(
				format("INSERT INTO %s(%s) VALUES(%s)", en.getTableName(), fields, values))
				.setEntity(en);
		sql.params().putAll(map);
		return sql;
	}

	private static void storeChainToSql(Chain chain, Sql sql) {
		Chain c;
		c = chain.head();
		while (c != null) {
			sql.params().set(c.name(), c.value());
			c = c.next();
		}
	}

	public Sql insertChain(String table, Chain chain) {
		StringBuilder flds = new StringBuilder();
		StringBuilder vals = new StringBuilder();
		Chain c = chain.head();
		while (c != null) {
			flds.append(",").append(c.name());
			vals.append(",@").append(c.name());
			c = c.next();
		}
		flds.deleteCharAt(0);
		vals.deleteCharAt(0);
		Sql sql = Sqls.create(format("INSERT INTO %s(%s) VALUES(%s)", table, flds, vals));
		storeChainToSql(chain, sql);
		return sql;
	}

	public Sql updateBatch(String table, Chain chain) {
		StringBuilder sb = new StringBuilder();
		Chain c = chain.head();
		while (c != null) {
			sb.append(',').append(c.name()).append("=@").append(c.name());
			c = c.next();
		}
		sb.deleteCharAt(0);
		Sql sql = Sqls.create(format("UPDATE %s SET %s $condition", table, sb));
		storeChainToSql(chain, sql);
		return sql;
	}

	public Sql update(Entity<?> en, Object obj) {
		StringBuilder sb = new StringBuilder();
		FieldMatcher fm = FieldFilter.get(en.getType());
		Map<String, Object> map = new HashMap<String, Object>();
		for (Iterator<EntityField> it = en.fields().iterator(); it.hasNext();) {
			EntityField ef = it.next();
			String fn = ef.getFieldName();
			if (ef.isId() || ef.isReadonly())
				continue;
			Object value = ef.getValue(obj);
			if (null != fm) {
				if (fm.isIgnoreNull() && null == value)
					continue;
				else if (!fm.match(fn))
					continue;
			}
			sb.append(',').append(ef.getColumnName()).append('=').append("@").append(fn);
			map.put(fn, value);
		}
		sb.deleteCharAt(0);
		EntityField idf = en.getIdentifiedField();
		String fmt = format("UPDATE %s SET %s WHERE %s=@%s", en.getTableName(), sb, idf
				.getColumnName(), idf.getFieldName());
		Sql sql = Sqls.create(fmt).setEntity(en);
		sql.params().putAll(map).set(idf.getFieldName(), idf.getValue(obj));
		return sql;
	}

	public Sql delete(Entity<?> entity, EntityField ef) {
		return Sqls.create(
				format("DELETE FROM %s WHERE %s=@%s", entity.getTableName(), ef.getColumnName(), ef
						.getFieldName())).setEntity(entity);
	}

	public Sql clear_links(Entity<?> ta, Link link, Object value) {
		EntityField tafld = ta.getField(link.getTargetField().getName());
		String fldnm = tafld.getFieldName();
		Sql sql = clear_links(ta.getTableName(), tafld.getColumnName(), fldnm).setEntity(ta);
		sql.params().set(fldnm, value);
		return sql;
	}

	public Sql clear_links(String table, String dbfld, String javafld) {
		return Sqls.create(format("DELETE FROM %s WHERE %s=@%s", table, dbfld, javafld));
	}

	public Sql clear(Entity<?> entity) {
		return clear(entity.getTableName()).setEntity(entity);
	}

	public Sql clear(String table) {
		return Sqls.create(format("DELETE FROM %s $condition", table));
	}

	public Sql truncate(String table) {
		return Sqls.create("TRUNCATE TABLE " + table);
	}

	public Sql func(String table, String type, String field) {
		String fmt = format("SELECT %s(%s) FROM %s $condition", type, field, table);
		return Sqls.fetchInt(fmt);
	}

	public Sql fetch(Entity<?> entity, EntityField ef) {
		String fields = evalActivedFields(entity);
		String fmt;
		if (ef.isName() && !ef.isCasesensitive()) {
			fmt = format("SELECT %s FROM %s WHERE LOWER(%s)=LOWER(@%s)", fields, entity
					.getTableName(), ef.getColumnName(), ef.getFieldName());
		} else {
			fmt = format("SELECT %s FROM %s WHERE %s=@%s", fields, entity.getTableName(), ef
					.getColumnName(), ef.getFieldName());
		}
		return Sqls.fetchEntity(fmt).setEntity(entity);
	}

	public Sql query(Entity<?> entity, Condition condition, Pager pager) {
		String s;
		// 评估所有需要查询的字段
		String fields = evalActivedFields(entity);

		// 获得条件及排序字符串
		String cnd = Sqls.getConditionString(entity, condition);

		// 如果用户没有设置 Pager
		if (null == pager) {
			// 如果没有过滤条件
			if (null == cnd) {
				s = format("SELECT %s FROM %s", fields, entity.getViewName());
			}
			// 如果设置了过滤条件
			else {
				s = format("SELECT %s FROM %s %s", fields, entity.getViewName(), cnd);
			}
		}
		// 如果用户设置了 Pager，用 Pager 生成 SQL
		else {
			s = pager.toSql(entity, fields, null == cnd ? "" : cnd);
		}
		// 生成 Sql 对象并返回
		Sql sql = Sqls.queryEntity(s).setEntity(entity);
		sql.getContext().setPager(pager);
		return sql;
	}

}
