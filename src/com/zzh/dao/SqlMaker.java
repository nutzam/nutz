package com.zzh.dao;

import java.util.Iterator;

import com.zzh.dao.entity.Entity;
import com.zzh.dao.entity.EntityField;
import com.zzh.dao.entity.Link;
import com.zzh.lang.Strings;
import com.zzh.lang.segment.CharSegment;

import static java.lang.String.*;

public class SqlMaker {

	public SqlMaker() {}

	static <T extends AbstractSql<?>> T makeSQL(T sql, Entity<?> entity, String ptn, String... args) {
		sql.valueOf(String.format(ptn, (Object[]) args));
		sql.setEntity(entity);
		return sql;
	}

	public ExecutableSql makeClearSQL(String tableName) {
		return makeSQL(new ExecutableSql(), null, "DELETE FROM %s ${condition}", tableName);
	}

	public ExecutableSql makeResetSQL(String tableName) {
		return makeSQL(new ExecutableSql(), null, "TRUNCATE TABLE %s", tableName);
	}

	/**
	 * This method can generate DELETE sql for delete @ Many and @ One
	 * 
	 * @param ta
	 *            : Entity of the object will be deleted
	 * @param link
	 *            : the Link annotation
	 * @return
	 */
	public ExecutableSql makeClearOneManySql(Entity<?> ta, Link link, Object value) {
		EntityField ef = ta.getField(link.getTargetField().getName());
		String s = String.format("DELETE FROM %s WHERE %s=%s", ta.getTableName(), ef
				.getColumnName(), Sqls.formatFieldValue(value));
		ExecutableSql sql = new ExecutableSql();
		sql.valueOf(s);
		return sql;
	}

	public ExecutableSql makeClearManyManyRelationSql(Link link, Object value) {
		String s = String.format("DELETE FROM %s WHERE %s=%s", link.getRelation(), link.getFrom(),
				Sqls.formatFieldValue(value));
		ExecutableSql sql = new ExecutableSql();
		sql.valueOf(s);
		return sql;
	}

	public ExecutableSql makeDeleteManyManyRelationSql(Link link, Object value) {
		String s = String.format("DELETE FROM %s WHERE %s=%s", link.getRelation(), link.getTo(),
				Sqls.formatFieldValue(value));
		ExecutableSql sql = new ExecutableSql();
		sql.valueOf(s);
		return sql;
	}

	public ExecutableSql makeDeleteSQL(Entity<?> en, EntityField ef) {
		return makeSQL(new ExecutableSql(), en, "DELETE FROM %s WHERE %s=${%s}", en.getTableName(),
				ef.getColumnName(), ef.getField().getName());
	}

	public <T> FetchSql<T> makeFetchSQL(Entity<T> en, EntityField ef) {
		FieldMatcher fm = FieldFilter.get(en.getType());
		String fields = evalActivedFields(fm, en, ef.getColumnName());
		FetchSql<T> sql;
		if (ef.isCaseInsensitive()) {
			sql = makeSQL(new FetchSql<T>(), en, "SELECT %s FROM %s WHERE LOWER(%s)=LOWER(${%s})",
					fields, en.getViewName(), ef.getColumnName(), ef.getField().getName());
		} else {
			sql = makeSQL(new FetchSql<T>(), en, "SELECT %s FROM %s WHERE %s=${%s}", fields, en
					.getViewName(), ef.getColumnName(), ef.getField().getName());
		}
		sql.setMatcher(fm);
		return sql;
	}

	private <T> String evalActivedFields(FieldMatcher fm, Entity<T> en, String defFields) {
		String fields = defFields;
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
				fields = sb.toString();
			}
		} else {
			fields = "*";
		}
		return fields;
	}

	public <T> FetchSql<T> makeFetchByConditionSQL(Entity<T> en) {
		FieldMatcher fm = FieldFilter.get(en.getType());
		String fields = evalActivedFields(fm, en, "*");
		FetchSql<T> sql = makeSQL(new FetchSql<T>(), en, "SELECT %s FROM %s ${condition}", fields,
				en.getViewName());
		sql.setMatcher(fm);
		return sql;
	}

	public <T> FetchSql<T> makeFetchLuckyOneSQL(Entity<T> en) {
		FieldMatcher fm = FieldFilter.get(en.getType());
		String fields = evalActivedFields(fm, en, "*");
		FetchSql<T> sql = makeSQL(new FetchSql<T>(), en, "SELECT %s FROM %s LIMIT 1", fields, en
				.getViewName());
		sql.setMatcher(fm);
		return sql;
	}

	public <T> FetchSql<Integer> makeCountSQL(Entity<T> en, String viewName) {
		return makeSQL(new FetchSql<Integer>(), en, "SELECT COUNT(*) FROM %s ${condition}",
				viewName);
	}

	public <T> FetchSql<Integer> makeFetchMaxSQL(Entity<T> en, EntityField ef) {
		String ptn = new CharSegment("SELECT MAX(${field}) FROM %s").set("field",
				ef.getField().getName()).toString();
		return makeSQL(new FetchSql<Integer>(), en, ptn, en.getViewName());
	}

	public <T> QuerySql<T> makeQuerySQL(Entity<T> en, Pager pager) {
		FieldMatcher ptn = FieldFilter.get(en.getType());
		String fields = evalActivedFields(ptn, en, "*");
		QuerySql<T> sql = new QuerySql<T>();
		sql.setEntity(en);
		String lm = null == pager ? null : pager.getLimitString(en);
		if (null == pager || Strings.isBlank(lm)) {
			sql.valueOf(String.format("SELECT %s FROM %s ${condition}", fields, en.getViewName()));
			sql.setPager(pager);
		} else {
			String rsName = pager.getResultSetName(en);
			String st = String.format("SELECT %s FROM %s ${condition}", fields, rsName == null ? en
					.getViewName() : rsName);
			sql.valueOf(String.format(lm, st));
		}
		sql.setMatcher(ptn);
		return sql;
	}

	public ExecutableSql makeInsertSQL(Entity<?> en, Object obj) {
		StringBuilder fields = new StringBuilder();
		StringBuilder values = new StringBuilder();
		for (Iterator<EntityField> it = en.fields().iterator(); it.hasNext();) {
			EntityField ef = it.next();
			if (ef.isAutoIncrement() || ef.isReadonly())
				continue;
			if (null != obj && !ef.hasDefaultValue() && null == ef.getValue(obj))
				continue;
			// fields.append(SQLUtils.formatName(ef.getColumnName()));
			fields.append(',').append(ef.getColumnName());
			values.append(',').append("${" + ef.getField().getName() + "}");
		}
		fields.deleteCharAt(0);
		values.deleteCharAt(0);
		return makeSQL(new ExecutableSql(), en, "INSERT INTO %s(%s) VALUES(%s)", en.getTableName(),
				fields.toString(), values.toString());
	}

	public ExecutableSql makeInsertManyManySql(Link link, Object fromValue, Object toValue) {
		ExecutableSql sql = new ExecutableSql();
		sql.valueOf(String.format("INSERT INTO %s (%s,%s) VALUES(%s,%s)", link.getRelation(), link
				.getFrom(), link.getTo(), Sqls.formatFieldValue(fromValue), Sqls
				.formatFieldValue(toValue)));
		return sql;
	}

	public ExecutableSql makeUpdateSQL(Entity<?> en, Object obj) {
		StringBuilder sb = new StringBuilder();
		FieldMatcher fm = FieldFilter.get(en.getType());
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
			sb.append(',').append(ef.getColumnName()).append('=').append("${").append(fn).append(
					'}');
		}
		sb.deleteCharAt(0);
		EntityField idf = en.getIdentifiedField();
		String condition = String.format("%s=${%s}", idf.getColumnName(), idf.getField().getName());
		return makeSQL(new ExecutableSql(), en, "UPDATE %s SET %s WHERE %s", en.getTableName(), sb
				.toString(), condition);

	}

	public ExecutableSql makeBatchUpdateSQL(Entity<?> en, Chain chain) {
		return makeSQL(new ExecutableSql(), en, "UPDATE %s SET %s ${condition}", en.getTableName(),
				chain.toString(en));
	}

	public ExecutableSql makeBatchUpdateRelationSQL(Link link, Chain chain) {
		ExecutableSql sql = new ExecutableSql();
		sql.valueOf(format("UPDATE %s SET %s ${condition}", link.getRelation(), chain
				.toString(null)));
		return sql;
	}
}
