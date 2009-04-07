package com.zzh.dao;

import java.util.Iterator;
import java.util.regex.Pattern;

import com.zzh.dao.entity.Entity;
import com.zzh.dao.entity.EntityField;
import com.zzh.lang.Strings;
import com.zzh.lang.segment.CharSegment;

public class SqlMaker {

	public SqlMaker() {
	}

	static <T extends ConditionSql<?>> T makeSQL(T sql, Entity<?> entity,
			String ptn, String... args) {
		return makeSQLByString(sql, entity, String.format(ptn, (Object[]) args));
	}

	protected static <T extends ConditionSql<?>> T makeSQLByString(T sql,
			Entity<?> entity, String str) {
		sql.valueOf(str);
		sql.setEntity(entity);
		return sql;
	}

	public ExecutableSql makeClearSQL(String tableName) {
		return makeSQL(new ExecutableSql(), null,
				"DELETE FROM %s ${condition};", tableName);
	}

	public ExecutableSql makeDeleteSQL(Entity<?> en, EntityField ef) {
		return makeSQL(new ExecutableSql(), en,
				"DELETE FROM %s WHERE %s=${%s};", en.getTableName(), ef
						.getColumnName(), ef.getField().getName());
	}

	public <T> FetchSql<T> makeFetchSQL(Entity<T> en, EntityField ef) {
		return makeSQL(new FetchSql<T>(), en,
				"SELECT * FROM %s WHERE %s=${%s};", en.getViewName(), ef
						.getColumnName(), ef.getField().getName());
	}

	public <T> FetchSql<T> makeFetchByConditionSQL(Entity<T> en) {
		return makeSQL(new FetchSql<T>(), en, "SELECT * FROM %s ${condition};",
				en.getViewName());
	}

	public <T> FetchSql<Integer> makeCountSQL(Entity<T> en, String viewName) {
		return makeSQL(new FetchSql<Integer>(), en,
				"SELECT COUNT(*) FROM %s ${condition};", viewName);
	}

	public <T> FetchSql<Integer> makeFetchMaxSQL(Entity<T> en, EntityField ef) {
		// TODO maybe escape % is better way
		String ptn = new CharSegment("SELECT MAX(${field}) FROM %s;").set(
				"field", ef.getField().getName()).toString();
		return makeSQL(new FetchSql<Integer>(), en, ptn, en.getViewName());
	}

	public <T> QuerySql<T> makeQuerySQL(Entity<T> en, Pager pager) {
		QuerySql<T> sql = new QuerySql<T>();
		sql.setEntity(en);
		String lm = null == pager ? null : pager.getLimitString(en);
		if (null == pager || Strings.isBlank(lm)) {
			sql.valueOf(String.format("SELECT * FROM %s ${condition}", en
					.getViewName()));
			sql.setPager(pager);
		} else {
			String rsName = pager.getResultSetName(en);
			String st = String.format("SELECT * FROM %s ${condition}",
					rsName == null ? en.getViewName() : rsName);
			sql.valueOf(String.format(lm, st));
		}
		return sql;
	}

	public ExecutableSql makeInsertSQL(Entity<?> en, Object obj) {
		StringBuffer fields = new StringBuffer();
		StringBuffer values = new StringBuffer();
		for (Iterator<EntityField> it = en.fields().iterator(); it.hasNext();) {
			EntityField ef = it.next();
			if (ef.isAutoIncrement() || ef.isReadonly())
				continue;
			if (null != obj && !ef.hasDefaultValue()
					&& null == ef.getValue(obj))
				continue;
			// fields.append(SQLUtils.formatName(ef.getColumnName()));
			fields.append(',').append(ef.getColumnName());
			values.append(',').append("${" + ef.getField().getName() + "}");
		}
		fields.deleteCharAt(0);
		values.deleteCharAt(0);
		return makeSQL(new ExecutableSql(), en,
				"INSERT INTO %s(%s) VALUES(%s);", en.getTableName(), fields
						.toString(), values.toString());
	}

	public ExecutableSql makeUpdateSQL(Entity<?> en, Object obj,
			String ignored, String actived) {
		StringBuffer sb = new StringBuffer();
		Pattern ign = null == ignored ? null : Pattern.compile(ignored);
		Pattern act = null == actived ? null : Pattern.compile(actived);
		for (Iterator<EntityField> it = en.fields().iterator(); it.hasNext();) {
			EntityField ef = it.next();
			String fn = ef.getField().getName();
			if (ef.isId() || ef.isReadonly())
				continue;
			if (null != obj && null == ef.getValue(obj))
				continue;
			if (null != ign)
				if (ign.matcher(fn).find())
					continue;
			if (null != actived)
				if (!act.matcher(fn).find())
					continue;
			sb.append(',').append(ef.getColumnName()).append('=').append("${")
					.append(fn).append('}');
		}
		sb.deleteCharAt(0);
		EntityField idf = en.getIdentifiedField();
		String condition = String.format("%s=${%s}", idf.getColumnName(), idf
				.getField().getName());
		return makeSQL(new ExecutableSql(), en, "UPDATE %s SET %s WHERE %s;",
				en.getTableName(), sb.toString(), condition);

	}
}
