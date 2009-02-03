package com.zzh.dao.impl;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import com.zzh.castor.Castors;
import com.zzh.castor.FailToCastObjectException;
import com.zzh.dao.AbstractSql;
import com.zzh.dao.Condition;
import com.zzh.dao.Dao;
import com.zzh.dao.DaoException;
import com.zzh.dao.ExecutableSql;
import com.zzh.dao.FetchSql;
import com.zzh.dao.QuerySql;
import com.zzh.dao.Sql;
import com.zzh.dao.SqlMaker;
import com.zzh.dao.SqlManager;
import com.zzh.dao.Sqls;
import com.zzh.dao.callback.ConnCallback;
import com.zzh.dao.callback.QueryCallback;
import com.zzh.dao.entity.Entity;
import com.zzh.dao.entity.EntityField;
import com.zzh.dao.entity.EntityHolder;
import com.zzh.dao.entity.Link;
import com.zzh.lang.Each;
import com.zzh.lang.ExitLoop;
import com.zzh.lang.Lang;
import com.zzh.lang.Mirror;
import com.zzh.lang.meta.Pager;
import com.zzh.trans.Atom;
import com.zzh.trans.Trans;

public class NutDao implements Dao {

	private static <T> EntityField checkIdField(Entity<T> en) {
		EntityField idField = en.getIdField();
		if (idField == null) {
			throw new RuntimeException(String.format("Entity [%] need @Id field", en.getMirror()
					.getMyClass().getName()));
		}
		return idField;
	}

	private static <T> EntityField checkNameField(Entity<T> en) {
		EntityField nameField = en.getNameField();
		if (nameField == null) {
			throw new RuntimeException(String.format("Entity [%] need @Name field", en.getMirror()
					.getMyClass().getName()));
		}
		return nameField;
	}

	private static QueryCallback<Integer> evalResultSetAsInt = new QueryCallback<Integer>() {
		@Override
		public Integer invoke(ResultSet rs) throws SQLException {
			return rs.getInt(1);
		}
	};

	private DataSource dataSource;
	private SqlMaker sqlMaker;
	private SqlManager sqlManager;
	private EntityHolder entityHolder;
	private Castors castors;

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setSqlMaker(SqlMaker sqlMaker) {
		this.sqlMaker = sqlMaker;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void setSqlManager(SqlManager sqlManager) {
		this.sqlManager = sqlManager;
	}

	public void setCastors(Castors castors) {
		this.castors = castors;
		this.sqlMaker.setCastors(castors);
	}

	public Castors getCastors() {
		return castors;
	}

	public SqlMaker getSqlMaker() {
		return sqlMaker;
	}

	public SqlManager getSqlManager() {
		return sqlManager;
	}

	@Override
	public SqlMaker maker() {
		return sqlMaker;
	}

	@Override
	public SqlManager sqls() {
		return this.sqlManager;
	}

	public NutDao() {
		this.entityHolder = new EntityHolder();
		this.castors = Castors.me();
		this.sqlMaker = new SqlMaker(this.castors);
	}

	public NutDao(DataSource dataSource) {
		this();
		this.setDataSource(dataSource);
	}

	public NutDao(DataSource dataSource, FileSqlManager sqlManager) {
		this();
		this.setDataSource(dataSource);
		this.setSqlManager(sqlManager);
	}

	public NutDao(DataSource dataSource, Castors castors, FileSqlManager sqlManager) {
		this(dataSource, sqlManager);
		this.setCastors(castors);
	}

	private int evalInt(FetchSql<Integer> sql) {
		sql.setCallback(evalResultSetAsInt);
		this.execute(sql);
		return ((Integer) sql.getResult()).intValue();
	}

	@Override
	public void execute(final Sql<?>... sqls) {
		execute(new ConnCallback() {
			@Override
			public void invoke(Connection conn) throws Exception {
				for (int i = 0; i < sqls.length; i++) {
					if (null != sqls[i])
						sqls[i].execute(conn);
				}
			}
		});
	}

	@Override
	public void execute(ConnCallback callback) {
		Connection conn = Sqls.getConnection(getDataSource());
		// TODO zzh: think about Savepoint
		// Savepoint sp = null;
		try {
			callback.invoke(conn);
			if (Trans.get() == null && !conn.getAutoCommit())
				conn.commit();
		} catch (Throwable e) {
			try {
				if (null != conn) {
					conn.rollback();
				}
			} catch (SQLException e1) {
			}
			if (e instanceof RuntimeException)
				throw (RuntimeException) e;
			else
				throw Lang.wrapThrow(e);
		} finally {
			Sqls.releaseConnection(conn, getDataSource());
			conn = null;
		}

	}

	@Override
	public void executeBySqlKey(String... keys) {
		List<Sql<?>> sqlList = new ArrayList<Sql<?>>();
		for (String key : keys) {
			Sql<?> sql = sqls().createSql(key);
			sqlList.add(sql);
		}
		Sql<?> sqls[] = new Sql<?>[sqlList.size()];
		this.execute(sqlList.toArray(sqls));
	}

	@Override
	public int count(Class<?> classOfT, Condition condition) {
		FetchSql<Integer> sql = sqlMaker.makeCountSQL(getEntity(classOfT));
		sql.setCondition(condition);
		return evalInt(sql);
	}

	@Override
	public int count(Class<?> classOfT) {
		return count(classOfT, null);
	}

	@Override
	public <T> void clear(Class<T> classOfT, Condition condition) {
		Sql<?> sql = sqlMaker.makeClearSQL(getEntity(classOfT)).setCondition(condition);
		execute(sql);
	}

	@Override
	public <T> void clearMany(T obj, String fieldName) {
		try {
			Mirror<? extends Object> me = Mirror.me(obj.getClass());
			final Link link = getEntity(obj.getClass()).getManyLinks().get(fieldName);
			final Object value = Sqls.formatFieldValue(me.getValue(obj, link.getReferField()));
			clear(link.getTargetClass(), new Condition() {
				public String toString(Entity<?> entity) {
					return String.format("%s=%s", entity.getField(link.getTargetField().getName())
							.getColumnName(), value);
				}
			});
		} catch (Exception e) {
			throw DaoException.create(obj, fieldName, "clearMany", e);
		}
	}

	@Override
	public <T> void delete(Class<T> classOfT, long id) {
		Entity<T> entity = getEntity(classOfT);
		EntityField idField = checkIdField(entity);
		ExecutableSql<?> sql = sqlMaker.makeDeleteSQL(entity, idField);
		execute(sql.set(idField.getField().getName(), id));
	}

	@Override
	public <T> void delete(Class<T> classOfT, String name) {
		Entity<T> entity = getEntity(classOfT);
		EntityField nameField = checkNameField(entity);
		AbstractSql<?> sql = sqlMaker.makeDeleteSQL(entity, nameField);
		execute(sql.set(nameField.getField().getName(), name));
	}

	@Override
	public void delete(Object obj) {
		Entity<?> entity = this.getEntity(obj.getClass());
		EntityField idnf = entity.getIdentifiedField();
		if (null == idnf)
			throw DaoException.create(obj, "$IdentifiedField", "delete(Object obj)", null);
		if (idnf.isId()) {
			int id = (Integer) Mirror.me(obj.getClass()).getValue(obj, idnf.getField());
			delete(obj.getClass(), id);
		} else if (idnf.isName()) {
			String name = Mirror.me(obj.getClass()).getValue(obj, idnf.getField()).toString();
			delete(obj.getClass(), name);
		} else {
			throw DaoException.create(obj, "$IdentifiedField", "delete(Object obj)", new Exception(
					"Wrong identified field"));
		}
	}

	@Override
	public <T> void deleteOne(T obj, String fieldName) {
		try {
			Mirror<? extends Object> me = Mirror.me(obj.getClass());
			Link link = getEntity(obj.getClass()).getOneLinks().get(fieldName);
			Field ownField = link.getReferField();
			Mirror<?> ownType = Mirror.me(ownField.getType());
			if (ownType.isStringLike()) {
				String name = me.getValue(obj, ownField).toString();
				delete(link.getTargetClass(), name);
			} else {
				long id = ((Number) me.getValue(obj, ownField)).longValue();
				delete(link.getTargetClass(), id);
			}
		} catch (Exception e) {
			throw DaoException.create(obj, fieldName, "deleteOne", e);
		}
	}

	@Override
	public <T> T fetch(Class<T> classOfT, long id) {
		Entity<T> entity = getEntity(classOfT);
		EntityField idField = checkIdField(entity);
		FetchSql<T> sql = sqlMaker.makeFetchSQL(entity, idField);
		sql.set(idField.getField().getName(), id);
		sql.setCallback(new FetchCallback<T>(entity, castors));
		execute(sql);
		return sql.getResult();
	}

	@Override
	public <T> T fetch(Class<T> classOfT, String name) {
		Entity<T> entity = getEntity(classOfT);
		EntityField nameField = checkNameField(entity);
		FetchSql<T> sql = sqlMaker.makeFetchSQL(entity, nameField);
		sql.set(nameField.getField().getName(), name);
		sql.setCallback(new FetchCallback<T>(entity, castors));
		execute(sql);
		return sql.getResult();
	}

	@Override
	public <T> T fetchMany(T obj, String fieldName) {
		try {
			Mirror<? extends Object> me = Mirror.me(obj.getClass());
			final Link link = getEntity(obj.getClass()).getManyLinks().get(fieldName);
			final Object value = Sqls.formatFieldValue(me.getValue(obj, link.getReferField()));
			List<?> list = query(link.getTargetClass(), new Condition() {
				public String toString(Entity<?> entity) {
					return String.format("%s=%s", entity.getField(link.getTargetField().getName())
							.getColumnName(), value);
				}
			}, null);
			Object v;
			try {
				v = this.castors.castTo(list, link.getOwnField().getType());
			} catch (FailToCastObjectException e) {
				throw Lang.wrapThrow(e);
			}
			me.setValue(obj, link.getOwnField(), v);
			return obj;
		} catch (Exception e) {
			throw DaoException.create(obj, fieldName, "fetchMany", e);
		}
	}

	@Override
	public <T> T fetchOne(T obj, String fieldName) {
		try {
			Mirror<? extends Object> me = Mirror.me(obj.getClass());
			Link link = getEntity(obj.getClass()).getOneLinks().get(fieldName);
			Object one;
			Field ownField = link.getReferField();
			Mirror<?> ownType = Mirror.me(ownField.getType());
			if (ownType.isStringLike()) {
				String name = me.getValue(obj, ownField).toString();
				one = fetch(link.getTargetClass(), name);
			} else {
				long id = ((Number) me.getValue(obj, ownField)).longValue();
				one = fetch(link.getTargetClass(), id);
			}
			me.setValue(obj, link.getOwnField(), one);
			return obj;
		} catch (Exception e) {
			throw DaoException.create(obj, fieldName, "fetchOne", e);
		}
	}

	@Override
	public <T> List<T> query(Class<T> classOfT, Condition condition, Pager pager) {
		final Entity<T> entity = this.getEntity(classOfT);
		QuerySql<T> sql = sqlMaker.makeQuerySQL(getEntity(classOfT));
		sql.setPager(pager);
		sql.setCondition(condition);
		sql.setCallback(new FetchCallback<T>(entity, castors));
		execute(sql);
		return (List<T>) sql.getResult();
	}

	@Override
	public <T> Entity<T> getEntity(Class<T> classOfT) {
		return entityHolder.getEntity(classOfT);
	}

	@Override
	public int getMaxId(Class<?> classOfT) {
		Entity<?> entity = getEntity(classOfT);
		return evalInt(sqlMaker.makeFetchMaxSQL(entity, checkIdField(entity)));
	}

	@Override
	public <T> T getObject(Class<T> classOfT, ResultSet rs) {
		return getEntity(classOfT).getObject(rs, castors);
	}

	@Override
	public <T> T insert(T obj) {
		Entity<?> entity = getEntity(obj.getClass());
		// prepare insert SQL
		ExecutableSql<?> insertSql = sqlMaker.makeInsertSQL(entity);
		insertSql.setValue(obj);
		// Evaluate fetchId SQL
		FetchSql<Integer> fetchIdSql = null;
		if (entity.hasIdField())
			fetchIdSql = sqlMaker.makeFetchMaxSQL(entity, checkIdField(entity)).setCallback(
					evalResultSetAsInt);
		// Execute SQL
		execute(insertSql, fetchIdSql);
		// Update Id field if need
		// @ TODO update all entity ai fields
		if (null != fetchIdSql)
			try {
				entity.getMirror().setValue(obj, entity.getIdentifiedField().getField(),
						fetchIdSql.getResult());
			} catch (Exception e) {
				throw Lang.wrapThrow(e);
			}
		return obj;
	}

	@Override
	public <T> T insertOne(final T obj, String fieldName) {
		try {
			// get link
			final Mirror<?> me = Mirror.me(obj.getClass());
			final Link link = getEntity(obj.getClass()).getOneLinks().get(fieldName);
			final Object target = me.getValue(obj, link.getOwnField());
			Trans.exec(new Atom() {
				public void run() throws Exception {
					insert(target);
					Mirror<?> ta = Mirror.me(target.getClass());
					Object value = ta.getValue(target, link.getTargetField());
					me.setValue(obj, link.getReferField(), value);
				}
			});
			return obj;
		} catch (Exception e) {
			throw DaoException.create(obj, fieldName, "insertOne", e);
		}
	}

	@Override
	public <T> T insertMany(final T obj, final String fieldName) {
		final Dao dao = this;
		Trans.exec(new Atom() {
			public void run() throws Exception {
				try { // get link
					Mirror<?> me = Mirror.me(obj.getClass());
					final Link link = getEntity(obj.getClass()).getManyLinks().get(fieldName);
					// update all refer field by own field
					final Object value = me.getValue(obj, link.getReferField());
					Object many = me.getValue(obj, link.getOwnField());
					Lang.each(many, new Each<Object>() {
						public void invoke(int index, Object ta, int size) throws ExitLoop {
							Mirror.me(ta.getClass()).setValue(ta, link.getTargetField(), value);
							dao.insert(ta);
						}
					});
				} catch (Exception e) {
					throw DaoException.create(obj, fieldName, "insertMany", e);
				}
			}
		});
		return obj;
	}

	@Override
	public <T> T update(T obj) {
		return update(obj, false);
	}

	@Override
	public <T> T update(T obj, boolean ignoreNull) {
		Sql<?> sql = sqlMaker.makeUpdateSQL(getEntity(obj.getClass()), ignoreNull ? obj : null,
				null, null);
		execute(sql.setValue(obj));
		return obj;
	}

	@Override
	public <T> T update(T obj, String ignoredFieldsPattern, String activedFieldsPattern) {
		Sql<?> sql = sqlMaker.makeUpdateSQL(getEntity(obj.getClass()), null, ignoredFieldsPattern,
				activedFieldsPattern);
		execute(sql.setValue(obj));
		return obj;
	}

}
