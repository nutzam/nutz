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
import org.nutz.lang.Lang;

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

	public Sql insertChain(String table, Chain chain, Entity<?> en) {
		StringBuilder flds = new StringBuilder();
		StringBuilder vals = new StringBuilder();
		Chain c = chain.head();
		while (c != null) {
			String colName = getColumnNameOfChain(en, c);
			flds.append(",").append(colName);
			vals.append(",@").append(c.name());
			c = c.next();
		}
		flds.deleteCharAt(0);
		vals.deleteCharAt(0);
		Sql sql = Sqls.create(format("INSERT INTO %s(%s) VALUES(%s)", table, flds, vals));
		storeChainToSql(chain, sql);
		return sql;
	}

	private static String getColumnNameOfChain(Entity<?> en, Chain c) {
		if (null != en) {
			EntityField ef = en.getField(c.name());
			if (null != ef)
				return ef.getColumnName();
		}
		return c.name();
	}

	public Sql updateBatch(String table, Chain chain, Entity<?> en) {
		StringBuilder sb = new StringBuilder();
		Chain c = chain.head();
		while (c != null) {
			String colName = getColumnNameOfChain(en, c);
			sb.append(',').append(colName).append("=@").append(c.name());
			c = c.next();
		}
		sb.deleteCharAt(0);
		Sql sql = Sqls.create(format("UPDATE %s SET %s $condition", table, sb));
		storeChainToSql(chain, sql);
		return sql;
	}

	public Sql update(Entity<?> en, Object obj) {
		StringBuilder sb = new StringBuilder();

		// 获取需要更新的字段
		FieldMatcher fm = FieldFilter.get(en.getType());
		Map<String, Object> map = new HashMap<String, Object>();
		for (Iterator<EntityField> it = en.fields().iterator(); it.hasNext();) {
			EntityField ef = it.next();
			String fn = ef.getFieldName();
			if (ef == en.getIdentifiedField() || ef.isPk() || ef.isReadonly())
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

		// 评估 WHERE 子句
		EntityField idf = en.getIdentifiedField();
		// 这个 POJO 用的是单一主键
		if (null != idf) {
			String fmt = format("UPDATE %s SET %s WHERE %s=@%s", en.getTableName(), sb, idf
					.getColumnName(), idf.getFieldName());
			Sql sql = Sqls.create(fmt).setEntity(en);
			sql.params().putAll(map).set(idf.getFieldName(), idf.getValue(obj));
			return sql;
		}

		// 这个 POJO 用的是复合主键
		EntityField[] pks = en.getPkFields();
		// 错误检查
		if (null == pks || pks.length <= 1) {
			throw Lang.makeThrow("You should define @Id or @Name or @PK for POJO '%s'", en
					.getType());
		}
		// 从 POJO 中获取复合主键的值
		Object[] args = new Object[pks.length];
		for (int i = 0; i < args.length; i++) {
			args[i] = pks[i].getValue(obj);
		}
		// 生成 SQL
		String fmt = format("UPDATE %s SET %s $condition", en.getTableName(), sb);
		Sql sql = Sqls.create(fmt).setEntity(en);
		sql.params().putAll(map);
		sql.setCondition(new PkCondition(args));
		return sql;
	}

	public Sql delete(Entity<?> entity, EntityField ef) {
		return Sqls.create(
				format("DELETE FROM %s WHERE %s=@%s", entity.getTableName(), ef.getColumnName(), ef
						.getFieldName())).setEntity(entity);
	}

	public Sql deletex(Entity<?> entity, Object[] pks) {
		String sql = format("DELETE FROM %s $condition", entity.getViewName());
		Condition condition = new PkCondition(pks);
		return Sqls.fetchEntity(sql).setEntity(entity).setCondition(condition);
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
					.getViewName(), ef.getColumnName(), ef.getFieldName());
		} else {
			fmt = format("SELECT %s FROM %s WHERE %s=@%s", fields, entity.getViewName(), ef
					.getColumnName(), ef.getFieldName());
		}
		return Sqls.fetchEntity(fmt).setEntity(entity);
	}

	public Sql fetchx(Entity<?> entity, Object[] pks) {
		String fields = evalActivedFields(entity);
		String sql = format("SELECT %s FROM %s $condition", fields, entity.getViewName());
		Condition condition = new PkCondition(pks);
		return Sqls.fetchEntity(sql).setEntity(entity).setCondition(condition);
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
