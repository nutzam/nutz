package com.zzh.dao.impl;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import com.zzh.castor.Castors;
import com.zzh.dao.AbstractSql;
import com.zzh.dao.Condition;
import com.zzh.dao.Dao;
import com.zzh.dao.DaoException;
import com.zzh.dao.ExecutableSql;
import com.zzh.dao.FetchSql;
import com.zzh.dao.FieldMatcher;
import com.zzh.dao.Pager;
import com.zzh.dao.QuerySql;
import com.zzh.dao.Sql;
import com.zzh.dao.SqlMaker;
import com.zzh.dao.SqlManager;
import com.zzh.dao.Sqls;
import com.zzh.dao.TableName;
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
import com.zzh.trans.Atom;
import com.zzh.trans.Trans;
import com.zzh.trans.Transaction;

public class NutDao implements Dao {

	private static <T> EntityField checkIdField(Entity<T> en) {
		EntityField idField = en.getIdField();
		if (idField == null) {
			throw Lang.makeThrow("Entity [%] need @Id field", en.getMirror().getType().getName());
		}
		return idField;
	}

	private static <T> EntityField checkNameField(Entity<T> en) {
		EntityField nameField = en.getNameField();
		if (nameField == null) {
			throw Lang.makeThrow("Entity [%] need @Name field", en.getMirror().getType().getName());
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
	private Class<? extends Pager> pagerType;

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setSqlMaker(SqlMaker sqlMaker) {
		this.sqlMaker = sqlMaker;
	}

	/**
	 * 'databaseProductName' | 'driverName'
	 * 
	 * <pre>
	 * psql:	'PostgreSQL'	|'PostgreSQL Native Driver'
	 * MySQL:	'MySQL'			|'MySQL-AB JDBC Driver'
	 * Oracle:	'Oracle'		|'Oracle JDBC driver'
	 * db2:		'DB2/NT'		|'IBM DB2 JDBC Universal Driver Architecture'
	 * SQLServer:	'Microsoft SQL Serve'	|'SQL Serve'
	 * </pre>
	 */
	@SuppressWarnings("unchecked")
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		final Class<? extends Pager>[] holder = new Class[1];
		this.execute(new ConnCallback() {
			public void invoke(Connection conn) throws Exception {
				DatabaseMetaData dmd = conn.getMetaData();
				String proName = dmd.getDatabaseProductName().toLowerCase();
				if (proName.startsWith("postgresql")) {
					holder[0] = Pager.Postgresql;
				} else if (proName.startsWith("mysql")) {
					holder[0] = Pager.MySQL;
				} else if (proName.startsWith("oracle")) {
					holder[0] = Pager.Postgresql;
				} else if (proName.startsWith("db2")) {
					holder[0] = Pager.DB2;
				} else if (proName.startsWith("microsoft sql")) {
					holder[0] = Pager.SQLServer;
				}
			}
		});
		pagerType = holder[0];
	}

	@Override
	public Pager createPager(int pageNumber, int pageSize) {
		return Pager.create(pagerType, pageNumber, pageSize);
	}

	public void setSqlManager(SqlManager sqlManager) {
		this.sqlManager = sqlManager;
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
		this.sqlMaker = new SqlMaker();
	}

	public NutDao(DataSource dataSource) {
		this();
		this.setDataSource(dataSource);
	}

	public NutDao(DataSource dataSource, SqlManager sqlManager) {
		this();
		this.setDataSource(dataSource);
		this.setSqlManager(sqlManager);
	}

	private int evalInt(FetchSql<Integer> sql) {
		sql.setCallback(evalResultSetAsInt);
		this.execute(sql);
		return ((Integer) sql.getResult()).intValue();
	}

	@Override
	public void execute(final Sql<?>... sqls) {
		execute(new ConnCallback() {
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
		Transaction trans = Trans.get();
		// TODO zzh: think about Savepoint
		// Savepoint sp = null;
		try {
			callback.invoke(conn);
			if (trans == null && !conn.getAutoCommit())
				conn.commit();
		} catch (Throwable e) {
			try {
				if (null != conn) {
					conn.rollback();
				}
			} catch (SQLException e1) {}
			if (e instanceof RuntimeException)
				throw (RuntimeException) e;
			else
				throw Lang.wrapThrow(e);
		} finally {
			if (trans == null)
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
	public int count(Class<?> classOfT) {
		return count(classOfT, null);
	}

	@Override
	public int count(Class<?> classOfT, Condition condition) {
		Entity<?> entity = getEntity(classOfT);
		FetchSql<Integer> sql = sqlMaker.makeCountSQL(entity, entity.getViewName());
		sql.setCondition(condition);
		return evalInt(sql);
	}

	@Override
	public int count(String tableName) {
		return count(tableName, null);
	}

	@Override
	public int count(String tableName, Condition condition) {
		FetchSql<Integer> sql = sqlMaker.makeCountSQL(null, tableName);
		sql.setCondition(condition);
		return evalInt(sql);
	}

	@Override
	public <T> void clear(Class<T> classOfT, Condition condition) {
		Entity<T> entity = getEntity(classOfT);
		ExecutableSql sql = sqlMaker.makeClearSQL(entity.getTableName());
		sql.setCondition(condition);
		sql.setEntity(entity);
		execute(sql);
	}

	@Override
	public void clear(String tableName, Condition condition) {
		execute(sqlMaker.makeClearSQL(tableName).setCondition(condition));
	}

	@Override
	public <T> void clearMany(final T obj, final String... fieldNames) {
		if (null == obj || null == fieldNames || fieldNames.length == 0)
			return;
		final Dao dao = this;
		final TableNameSnapshot snapshot = new TableNameSnapshot();
		try {
			Trans.exec(new Atom() {
				public void run() {
					Mirror<? extends Object> me = Mirror.me(obj.getClass());
					for (String fieldName : fieldNames) {
						final Link link = checkManyField(dao, obj, "clearMany", fieldName);
						if (link.isDynamicTarget()) {
							Object refer = me.getValue(obj, link.getReferField());
							snapshot.changeRefer(refer);
							clear(link.getTargetClass(), null);
						} else {
							Object value = Sqls.formatFieldValue(me.getValue(obj, link
									.getReferField()));
							clear(link.getTargetClass(), new ManyCondition(link, value));
						}
					}
				}
			});
		} catch (Exception e) {
			throw DaoException.create(obj, Lang.concat(fieldNames).toString(), "clearMany", e);
		} finally {
			snapshot.restore();
		}
	}

	@Override
	public <T> void delete(Class<T> classOfT, long id) {
		Entity<T> entity = getEntity(classOfT);
		EntityField idField = checkIdField(entity);
		ExecutableSql sql = sqlMaker.makeDeleteSQL(entity, idField);
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
	public <T> void delete(T obj) {
		Entity<?> entity = this.getEntity(obj.getClass());
		EntityField idnf = entity.getIdentifiedField();
		if (null == idnf)
			throw DaoException.create(obj, "$IdentifiedField", "delete(Object obj)", null);
		if (idnf.isId()) {
			int id = (Integer) idnf.getValue(obj);
			delete(obj.getClass(), id);
		} else if (idnf.isName()) {
			String name = idnf.getValue(obj).toString();
			delete(obj.getClass(), name);
		} else {
			throw DaoException.create(obj, "$IdentifiedField", "delete(Object obj)", new Exception(
					"Wrong identified field"));
		}
	}

	@Override
	public <T> void deleteOne(final T obj, final String... fieldNames) {
		if (null == obj || null == fieldNames || fieldNames.length == 0)
			return;
		final Dao dao = this;
		try {
			Trans.exec(new Atom() {
				public void run() {
					Mirror<? extends Object> me = Mirror.me(obj.getClass());
					for (String fieldName : fieldNames) {
						Link link = NutDao.checkOneField(dao, obj, "deleteOne", fieldName);
						Field ownField = link.getReferField();
						Mirror<?> ownType = Mirror.me(ownField.getType());
						if (ownType.isStringLike()) {
							String name = me.getValue(obj, ownField).toString();
							delete(link.getTargetClass(), name);
						} else {
							long id = ((Number) me.getValue(obj, ownField)).longValue();
							delete(link.getTargetClass(), id);
						}
					}
				}
			});
		} catch (Exception e) {
			throw DaoException.create(obj, Lang.concat(fieldNames).toString(), "deleteOne", e);
		}
	}

	@Override
	public <T> T fetch(Class<T> classOfT, long id) {
		Entity<T> entity = getEntity(classOfT);
		EntityField idField = checkIdField(entity);
		FetchSql<T> sql = sqlMaker.makeFetchSQL(entity, idField);
		sql.set(idField.getField().getName(), id);
		sql.setCallback(new FetchCallback<T>(entity));
		execute(sql);
		return sql.getResult();
	}

	@Override
	public <T> T fetch(Class<T> classOfT, String name) {
		Entity<T> entity = getEntity(classOfT);
		EntityField nameField = checkNameField(entity);
		FetchSql<T> sql = sqlMaker.makeFetchSQL(entity, nameField);
		sql.set(nameField.getField().getName(), name);
		sql.setCallback(new FetchCallback<T>(entity));
		execute(sql);
		return sql.getResult();
	}

	@Override
	public <T> T fetch(Class<T> classOfT, Condition condition) {
		Entity<T> entity = getEntity(classOfT);
		FetchSql<T> sql = sqlMaker.makeFetchByConditionSQL(entity);
		sql.setCondition(condition);
		sql.setCallback(new FetchCallback<T>(entity));
		execute(sql);
		return sql.getResult();
	}

	private static class ManyCondition implements Condition {

		private Object value;
		private Link link;

		private ManyCondition(Link link, Object value) {
			this.link = link;
			this.value = value;
		}

		@Override
		public String toString(Entity<?> entity) {
			return String.format("%s=%s", entity.getField(link.getTargetField().getName())
					.getColumnName(), value);
		}

	}

	@Override
	public <T> T fetchMany(T obj, String... fieldNames) {
		if (null == obj || null == fieldNames || fieldNames.length == 0)
			return obj;
		TableNameSnapshot snapshot = new TableNameSnapshot();
		try {
			Mirror<? extends Object> me = Mirror.me(obj.getClass());
			for (String fieldName : fieldNames) {
				final Link link = checkManyField(this, obj, "fetchMany", fieldName);
				List<?> list;
				if (link.isDynamicTarget()) {
					Object refer = me.getValue(obj, link.getReferField());
					snapshot.changeRefer(refer);
					list = query(link.getTargetClass(), null, null);
				} else {
					final Object value = Sqls.formatFieldValue(me.getValue(obj, link
							.getReferField()));
					list = query(link.getTargetClass(), new ManyCondition(link, value), null);
				}
				Object value = Castors.me().cast(list, list.getClass(),
						link.getOwnField().getType(), link.getMapKeyField());
				me.setValue(obj, link.getOwnField(), value);
			}
			return obj;
		} catch (Exception e) {
			throw DaoException.create(obj, Lang.concat(fieldNames).toString(), "fetchMany", e);
		} finally {
			snapshot.restore();
		}
	}

	static class TableNameSnapshot {
		private Object oldRefer;
		private boolean stored;

		public void changeRefer(Object newRefer) {
			if (!stored) {
				oldRefer = TableName.get();
				stored = true;
			}
			TableName.set(newRefer);
		}

		void restore() {
			if (stored)
				TableName.set(oldRefer);
		}

	}

	@Override
	public <T> T fetchOne(T obj, String... fieldNames) {
		if (null == obj || null == fieldNames || fieldNames.length == 0)
			return obj;
		try {
			Mirror<? extends Object> me = Mirror.me(obj.getClass());
			for (String fieldName : fieldNames) {
				Link link = NutDao.checkOneField(this, obj, "fetchOne", fieldName);
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
			}
			return obj;
		} catch (Exception e) {
			throw DaoException.create(obj, Lang.concat(fieldNames).toString(), "fetchOne", e);
		}
	}

	@Override
	public <T> List<T> query(Class<T> classOfT, Condition condition, Pager pager) {
		final Entity<T> entity = this.getEntity(classOfT);
		QuerySql<T> sql = sqlMaker.makeQuerySQL(getEntity(classOfT), pager);
		sql.setCondition(condition);
		sql.setCallback(new FetchCallback<T>(entity));
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
	public <T> T getObject(Class<T> classOfT, ResultSet rs, FieldMatcher fm) {
		return getEntity(classOfT).getObject(rs, fm);
	}

	@Override
	public <T> T insert(T obj) {
		Entity<?> entity = getEntity(obj.getClass());
		// prepare insert SQL
		ExecutableSql insertSql = sqlMaker.makeInsertSQL(entity, obj);
		insertSql.setValue(obj);
		// Evaluate fetchId SQL
		FetchSql<Integer> fetchIdSql = null;
		if (null != entity.getIdField() && entity.getIdField().isAutoIncrement()) {
			fetchIdSql = entity.getIdField().getFetchSql();
			if (null == fetchIdSql)
				fetchIdSql = sqlMaker.makeFetchMaxSQL(entity, checkIdField(entity)).setCallback(
						evalResultSetAsInt);
		}
		// Execute SQL
		execute(insertSql, fetchIdSql);
		// Update Id field if need
		// @ TODO update all entity ai fields
		if (null != fetchIdSql)
			try {
				entity.getIdField().setValue(obj, fetchIdSql.getResult());
			} catch (Exception e) {
				throw Lang.wrapThrow(e);
			}
		return obj;
	}

	@Override
	public <T> T insertOne(final T obj, final String... fieldNames) {
		if (null == obj || null == fieldNames || fieldNames.length == 0)
			return obj;
		final Dao dao = this;
		try {
			Trans.exec(new Atom() {
				public void run() {
					Mirror<?> mirror = Mirror.me(obj.getClass());
					for (String fieldName : fieldNames) {
						Link link = NutDao.checkOneField(dao, obj, "insertOne", fieldName);
						Object target = mirror.getValue(obj, link.getOwnField());
						if (null == target)
							continue;
						dao.insert(target);
						Mirror<?> ta = Mirror.me(target.getClass());
						Object value = ta.getValue(target, link.getTargetField());
						mirror.setValue(obj, link.getReferField(), value);
					}
				}
			});
			return obj;
		} catch (Exception e) {
			throw DaoException.create(obj, Lang.concat(fieldNames).toString(), "insertOne", e);
		}
	}

	@Override
	public <T> T insertMany(final T obj, final String... fieldNames) {
		if (null == obj || null == fieldNames || fieldNames.length == 0)
			return obj;
		final Dao dao = this;
		Trans.exec(new Atom() {
			public void run() {
				final TableNameSnapshot snapshot = new TableNameSnapshot();
				try { // get link
					Mirror<?> me = Mirror.me(obj.getClass());
					for (String fieldName : fieldNames) {
						final Link link = checkManyField(dao, obj, "insertMany", fieldName);
						Object many = me.getValue(obj, link.getOwnField());
						final Mirror<?> mirror = Mirror.me(link.getTargetClass());
						if (link.isDynamicTarget()) {
							Object refer = mirror.getValue(obj, link.getReferField());
							snapshot.changeRefer(refer);
							Lang.each(many, new Each<Object>() {
								public void invoke(int index, Object ta, int size) throws ExitLoop {
									dao.insert(ta);
								}
							});
						} else { // update all refer field
							// by own field
							final Object value = me.getValue(obj, link.getReferField());
							Lang.each(many, new Each<Object>() {
								public void invoke(int index, Object ta, int size) throws ExitLoop {
									if (null == ta)
										return;
									mirror.setValue(ta, link.getTargetField(), value);
									dao.insert(ta);
								}
							});
						}
					}
				} catch (Exception e) {
					throw DaoException.create(obj, Lang.concat(fieldNames).toString(),
							"insertMany", e);
				} finally {
					snapshot.restore();
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
		Sql<?> sql = sqlMaker.makeUpdateSQL(getEntity(obj.getClass()), ignoreNull ? obj : null);
		execute(sql.setValue(obj));
		return obj;
	}

	private static class MMCondition implements Condition {

		private NutDao dao;
		private Link link;
		private Object obj;

		private MMCondition(NutDao dao, Link link, Object obj) {
			this.dao = dao;
			this.link = link;
			this.obj = obj;
		}

		@Override
		public String toString(Entity<?> me) {
			return String.format("%s IN (SELECT %s FROM %s WHERE %s=%s)", dao.getEntity(
					link.getTargetClass()).getField(link.getTargetField().getName())
					.getColumnName(), link.getTo(), link.getRelation(), link.getFrom(),
					evalValue(me));
		}

		private Object evalValue(Entity<?> me) {
			return Sqls.formatFieldValue(me.getMirror().getValue(obj, link.getReferField()));
		}

		Sql<?> getClearRelationSql(Class<?> me) {
			String s = String.format("DELETE FROM %s WHERE %s=%s", link.getRelation(), link
					.getFrom(), evalValue(dao.getEntity(me)));
			return new ExecutableSql().valueOf(s);
		}
	}

	private static RuntimeException makeManyOneError(String objName, String methodName,
			String fieldName, String annName) {
		return Lang.makeThrow("Error happend when '%s', for the reason: field '%s.%s' is not @%s",
				methodName, objName, fieldName, annName);
	}

	private static <T> Link checkOneField(Dao dao, T obj, String methodName, String fieldName) {
		Link link = dao.getEntity(obj.getClass()).getOnes().get(fieldName);
		if (null == link) {
			throw makeManyOneError(obj.getClass().getName(), methodName, fieldName, "One");
		}
		return link;
	}

	private static <T> Link checkManyField(Dao dao, T obj, String methodName, String fieldName) {
		Link link = dao.getEntity(obj.getClass()).getManys().get(fieldName);
		if (null == link) {
			throw makeManyOneError(obj.getClass().getName(), methodName, fieldName, "Many");
		}
		return link;
	}

	private static Link checkManyManyField(Dao dao, Mirror<?> mirror, String methodName,
			String fieldName) {
		Link link = dao.getEntity(mirror.getType()).getManyManys().get(fieldName);
		if (null == link) {
			throw makeManyOneError(mirror.getType().getName(), methodName, fieldName, "ManyMany");
		}
		return link;
	}

	@Override
	public <T> void clearManyMany(final T obj, final String... fieldNames) {
		if (null == obj || null == fieldNames || fieldNames.length == 0)
			return;
		final NutDao dao = this;
		try {
			Trans.exec(new Atom() {
				public void run() {
					Mirror<?> me = Mirror.me(obj.getClass());
					for (String fieldName : fieldNames) {
						Link link = checkManyManyField(dao, me, "clearManyMany", fieldName);
						MMCondition condition = new MMCondition(dao, link, obj);
						dao.execute(condition.getClearRelationSql(me.getType()));
					}
				}
			});
		} catch (Exception e) {
			throw DaoException.create(obj, Lang.concat(fieldNames).toString(), "clearManyMany", e);
		}
	}

	@Override
	public <T> T fetchManyMany(T obj, String... fieldNames) {
		if (null == obj || null == fieldNames || fieldNames.length == 0)
			return obj;
		try {
			Mirror<?> me = Mirror.me(obj.getClass());
			for (String fieldName : fieldNames) {
				Link link = checkManyManyField(this, me, "fetchManyMany", fieldName);
				List<?> list = query(link.getTargetClass(), new MMCondition(this, link, obj), null);
				me.setValue(obj, link.getOwnField(), Castors.me().cast(list, list.getClass(),
						link.getOwnField().getType(), link.getMapKeyField()));
			}
			return obj;
		} catch (Exception e) {
			throw DaoException.create(obj, Lang.concat(fieldNames).toString(), "fetchManyMany", e);
		}
	}

	@Override
	public <T> T insertManyMany(final T obj, final String... fieldNames) {
		if (null == obj || null == fieldNames || fieldNames.length == 0)
			return obj;
		final NutDao dao = this;
		try {
			Trans.exec(new Atom() {
				public void run() {
					try { // get link
						final Mirror<?> me = Mirror.me(obj.getClass());
						for (String fieldName : fieldNames) {
							final Link link = checkManyManyField(dao, me, "insertManyMany",
									fieldName);
							Object many = me.getValue(obj, link.getOwnField());
							Lang.each(many, new Each<Object>() {
								public void invoke(int index, Object ta, int size) throws ExitLoop {
									if (null == ta)
										return;
									try {
										dao.insert(ta);
									} catch (DaoException e) {
										// if existed this
										// object already,
										// fetch
										// it out
										Entity<?> entity = dao.getEntity(ta.getClass());
										long id = 0;
										;
										try {
											if (entity.getIdentifiedField().isId()) {
												id = Castors.me().castTo(
														entity.getMirror().getValue(ta,
																entity.getIdField().getField()),
														Long.class);
											}
										} catch (Exception e1) {}
										if (id <= 0) {
											if (null == entity.getNameField())
												throw e;
											String name = Castors.me().castToString(
													entity.getMirror().getValue(ta,
															entity.getNameField().getField()));
											if (null == name)
												throw e;
											ta = dao.fetch(ta.getClass(), name);
										} else {
											ta = dao.fetch(ta.getClass(), id);
										}
										if (null == ta)
											throw e;
									}
									ExecutableSql sql = new ExecutableSql();
									sql.valueOf(String.format(
											"INSERT INTO %s (%s,%s) VALUES(%s,%s);", link
													.getRelation(), link.getFrom(), link.getTo(),
											Sqls.formatFieldValue(me.getValue(obj, link
													.getReferField())), Sqls.formatFieldValue(me
													.getValue(ta, link.getTargetField()))));
									dao.execute(sql);
								}
							});
						}
					} catch (Exception e) {
						throw DaoException.create(obj, Lang.concat(fieldNames).toString(),
								"insertManyMany", e);
					}
				}
			});
			return obj;
		} catch (Exception e) {
			throw DaoException.create(obj, Lang.concat(fieldNames).toString(), "insertManyMany", e);
		}
	}

}
