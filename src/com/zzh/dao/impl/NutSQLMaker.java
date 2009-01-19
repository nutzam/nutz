package com.zzh.dao.impl;

import java.util.Iterator;

import com.zzh.dao.ExecutableSQL;
import com.zzh.dao.FailToMakeSQLException;
import com.zzh.dao.FetchSQL;
import com.zzh.dao.QuerySQL;
import com.zzh.dao.SQLMaker;
import com.zzh.dao.entity.Entity;
import com.zzh.dao.entity.EntityField;
import com.zzh.dao.entity.EntityHolder;

public class NutSQLMaker implements SQLMaker {

	private EntityHolder mappingHolder;

	public NutSQLMaker(EntityHolder entityMappings) {
		this.mappingHolder = entityMappings;
	}

	@Override
	public ExecutableSQL<?> makeClearSQL(Class<?> klass) throws FailToMakeSQLException {
		Entity<?> en = getEntity(klass);
		ExecutableSQL<?> sql = new ExecutableSQL<Object>(String.format(
				"DELETE FROM %s ${condition};", en.getTableName()));
		sql.setEntityMapping(en);
		return sql;
	}

	@Override
	public <T> FetchSQL<Integer> makeCountSQL(Class<T> klass) throws FailToMakeSQLException {
		Entity<?> en = getEntity(klass);
		FetchSQL<Integer> sql = new FetchSQL<Integer>(String.format(
				"SELECT COUNT(*) FROM %s ${condition};", en.getTableName()));
		sql.setEntityMapping(en);
		return sql;
	}

	@Override
	public ExecutableSQL<?> makeDeleteByIdSQL(Class<?> klass, long id)
			throws FailToMakeSQLException {
		Entity<?> en = getEntity(klass);
		EntityField ef = en.getIdField();
		ExecutableSQL<?> sql = new ExecutableSQL<Object>(String.format(
				"DELETE FROM %s WHERE %s=%d;", en.getTableName(), ef.getColumnName(), id));
		sql.setEntityMapping(en);
		return sql;
	}

	@Override
	public ExecutableSQL<?> makeDeleteByNameSQL(Class<?> klass, String name)
			throws FailToMakeSQLException {
		Entity<?> en = getEntity(klass);
		EntityField ef = en.getNameField();
		ExecutableSQL<?> sql = new ExecutableSQL<Object>(String.format(
				"DELETE FROM %s WHERE %s='%s';", en.getTableName(), ef.getColumnName(), name));
		sql.setEntityMapping(en);
		return sql;
	}

	@Override
	public <T> FetchSQL<T> makeFetchByIdSQL(Class<T> klass, long id) throws FailToMakeSQLException {
		Entity<?> en = getEntity(klass);
		EntityField ef = en.getIdField();
		FetchSQL<T> sql = new FetchSQL<T>(String.format("SELECT * FROM %s WHERE %s=%d;", en
				.getTableName(), ef.getColumnName(), id));
		sql.setEntityMapping(en);
		return sql;
	}

	@Override
	public <T> FetchSQL<T> makeFetchByNameSQL(Class<T> klass, String name)
			throws FailToMakeSQLException {
		Entity<?> en = getEntity(klass);
		EntityField ef = en.getNameField();
		FetchSQL<T> sql = new FetchSQL<T>(String.format("SELECT * FROM %s WHERE %s='%s';", en
				.getTableName(), ef.getColumnName(), name));
		sql.setEntityMapping(en);
		return sql;
	}

	@Override
	public <T> FetchSQL<Integer> makeFetchMaxIdSQL(Class<T> klass) throws FailToMakeSQLException {
		Entity<?> en = getEntity(klass);
		EntityField ef = en.getIdField();
		if (null == ef)
			throw new FailToMakeSQLException(String.format("Can not find @Id field in entity [%s]",
					en.getClassOfT().getName()));
		FetchSQL<Integer> sql = new FetchSQL<Integer>(String.format("SELECT MAX(%s) FROM %s;", ef
				.getColumnName(), en.getTableName()));
		sql.setEntityMapping(en);
		return sql;
	}

	@Override
	public ExecutableSQL<?> makeInsertSQL(Class<?> klass) throws FailToMakeSQLException {
		Entity<?> en = getEntity(klass);
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

		String s = String.format("INSERT INTO %s(%s) VALUES(%s);", en.getTableName(), fields,
				values);
		ExecutableSQL<?> sql = new ExecutableSQL<Object>(s);
		sql.setEntityMapping(en);
		return sql;
	}

	@Override
	public <T> QuerySQL<T> makeQuerySQL(Class<T> klass) throws FailToMakeSQLException {
		Entity<?> en = getEntity(klass);
		QuerySQL<T> sql = new QuerySQL<T>(String.format("SELECT * FROM %s ${condition};", en
				.getTableName()));
		sql.setEntityMapping(en);
		return sql;
	}

	@Override
	public ExecutableSQL<?> makeUpdateSQL(Class<?> klass) throws FailToMakeSQLException {
		Entity<?> en = getEntity(klass);
		StringBuffer sb = new StringBuffer();
		for (Iterator<EntityField> it = en.fields().iterator(); it.hasNext();) {
			EntityField ef = it.next();
			if (ef.isId())
				continue;
			sb.append(ef.getColumnName()).append('=').append("${" + ef.getField().getName() + "}");
			if (it.hasNext()) {
				sb.append(',');
			}
		}

		EntityField idf = en.getIdentifiedField();
		String condition = String.format("%s=${%s}", idf.getColumnName(), idf.getField().getName());

		String s = String.format("UPDATE %s SET %s WHERE %s;", en.getTableName(), sb, condition);
		ExecutableSQL<?> sql = new ExecutableSQL<Object>(s);
		sql.setEntityMapping(en);
		return sql;
	}

	private Entity<?> getEntity(Class<?> klass) {
		Entity<?> en = mappingHolder.getEntity(klass);
		if (null == en)
			throw new FailToMakeSQLException("Entity Mapping for '" + klass.toString()
					+ "' not found!!!");
		return en;
	}

}
