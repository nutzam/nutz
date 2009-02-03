package com.zzh.dao;

import java.util.Iterator;

import com.zzh.castor.Castors;
import com.zzh.dao.entity.Entity;
import com.zzh.dao.entity.EntityField;
import com.zzh.lang.Lang;
import com.zzh.lang.segment.CharSegment;

public class SqlMaker {

	private Castors castors;

	public SqlMaker() {
		this.castors = Castors.me();
	}

	public SqlMaker(Castors castors) {
		this.castors = castors;
	}

	public Castors getCastors() {
		return castors;
	}

	public void setCastors(Castors castors) {
		this.castors = castors;
	}

	protected static <T extends ConditionSql<?>> T makeSQL(T sql,
			Entity<?> entity, String ptn, String... args) {
		return makeSQLByString(sql, entity, String.format(ptn, (Object[]) Lang
				.join(entity.getTableName(), args)));
	}

	protected static <T extends ConditionSql<?>> T makeSQLByString(T sql,
			Entity<?> entity, String str) {
		sql.valueOf(str);
		sql.setEntity(entity);
		return sql;
	}

	public ExecutableSql<?> makeClearSQL(Entity<?> en) {
		return makeSQL(new ExecutableSql<Object>(castors), en,
				"DELETE FROM %s ${condition};");
	}

	public ExecutableSql<?> makeDeleteSQL(Entity<?> en, EntityField field) {
		return makeSQL(new ExecutableSql<Object>(castors), en,
				"DELETE FROM %s WHERE %s=${%s};", field.getColumnName(), field
						.getField().getName());
	}

	public <T> FetchSql<T> makeFetchSQL(Entity<T> en, EntityField field) {
		return makeSQL(new FetchSql<T>(castors), en,
				"SELECT * FROM %s WHERE %s=${%s};", field.getColumnName(),
				field.getField().getName());
	}

	public <T> FetchSql<Integer> makeCountSQL(Entity<T> en) {
		return makeSQL(new FetchSql<Integer>(castors), en,
				"SELECT COUNT(*) FROM %s ${condition};");
	}

	public <T> FetchSql<Integer> makeFetchMaxSQL(Entity<T> en, EntityField ef) {
		// TODO maybe escape % is better way
		String ptn = new CharSegment("SELECT MAX(${field}) FROM %s;").set(
				"field", ef.getField().getName()).toString();
		return makeSQL(new FetchSql<Integer>(castors), en, ptn);
	}

	public <T> QuerySql<T> makeQuerySQL(Entity<T> en) {
		return makeSQL(new QuerySql<T>(castors), en,
				"SELECT * FROM %s ${condition};");
	}

	public ExecutableSql<?> makeInsertSQL(Entity<?> en) {
		StringBuffer fields = new StringBuffer();
		StringBuffer values = new StringBuffer();
		for (Iterator<EntityField> it = en.fields().iterator(); it.hasNext();) {
			EntityField ef = it.next();
			if (ef.isAutoIncrement())
				continue;
			// fields.append(SQLUtils.formatName(ef.getColumnName()));
			fields.append(ef.getColumnName());
			values.append("${" + ef.getField().getName() + "}");
			if (it.hasNext()) {
				fields.append(',');
				values.append(',');
			}
		}
		return makeSQL(new ExecutableSql<Object>(castors), en,
				"INSERT INTO %s(%s) VALUES(%s);", fields.toString(), values
						.toString());
	}

	public ExecutableSql<?> makeUpdateSQL(Entity<?> en, Object obj,
			String ignoredFieldsPattern, String activedFieldsPattern) {
		StringBuffer sb = new StringBuffer();
		for (Iterator<EntityField> it = en.fields().iterator(); it.hasNext();) {
			EntityField ef = it.next();
			if (ef.isId())
				continue;
			if (null != obj
					&& null == en.getMirror().getValue(obj, ef.getField()))
				continue;
			if (null != ignoredFieldsPattern
					&& ignoredFieldsPattern.indexOf("["
							+ ef.getField().getName() + "]") != -1)
				continue;
			if (null != activedFieldsPattern
					&& activedFieldsPattern.indexOf("["
							+ ef.getField().getName() + "]") == -1)
				continue;
			sb.append(ef.getColumnName()).append('=').append(
					"${" + ef.getField().getName() + "}");
			if (it.hasNext()) {
				sb.append(',');
			}
		}
		EntityField idf = en.getIdentifiedField();
		String condition = String.format("%s=${%s}", idf.getColumnName(), idf
				.getField().getName());
		return makeSQL(new ExecutableSql<Object>(castors), en,
				"UPDATE %s SET %s WHERE %s;", sb.toString(), condition);

	}
}
