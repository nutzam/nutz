package org.nutz.dao.impl;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import javax.sql.DataSource;

import org.nutz.castor.Castors;
import org.nutz.dao.Condition;
import org.nutz.dao.ConnCallback;
import org.nutz.dao.ConnectionHolder;
import org.nutz.dao.Dao;
import org.nutz.dao.DaoException;
import org.nutz.dao.DatabaseMeta;
import org.nutz.dao.FieldMatcher;
import org.nutz.dao.Pager;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.sql.SqlMaker;
import org.nutz.dao.SqlManager;
import org.nutz.dao.DaoUtils;
import org.nutz.dao.Chain;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.EntityField;
import org.nutz.dao.entity.EntityHolder;
import org.nutz.dao.entity.Link;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.trans.Atom;
import org.nutz.trans.Trans;

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

	private DataSource dataSource;
	private SqlMaker maker;
	private SqlManager sqls;
	private EntityHolder entities;
	private DatabaseMeta meta;

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setMaker(SqlMaker sqlMaker) {
		this.maker = sqlMaker;
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
	public void setDataSource(DataSource dataSource) {
		entities = new EntityHolder();
		this.dataSource = dataSource;
	}

	private synchronized void checkDatabase() {
		meta = new DatabaseMeta();
		this.run(new ConnCallback() {
			public void invoke(Connection conn) throws Exception {
				DatabaseMetaData dmd = conn.getMetaData();
				String proName = dmd.getDatabaseProductName().toLowerCase();
				if (proName.startsWith("postgresql")) {
					meta.setPagerType(Pager.Postgresql);
					meta.setProductName(DatabaseMeta.PSQL);
				} else if (proName.startsWith("mysql")) {
					meta.setPagerType(Pager.MySQL);
					meta.setProductName(DatabaseMeta.MYSQL);
				} else if (proName.startsWith("oracle")) {
					meta.setPagerType(Pager.Oracle);
					meta.setProductName(DatabaseMeta.ORACLE);
				} else if (proName.startsWith("db2")) {
					meta.setPagerType(Pager.DB2);
					meta.setProductName(DatabaseMeta.DB2);
				} else if (proName.startsWith("microsoft sql")) {
					meta.setPagerType(Pager.SQLServer);
					meta.setProductName(DatabaseMeta.SQLSERVER);
				}
				meta.setUrl(dmd.getURL());
				meta.setUserName(dmd.getUserName());
			}
		});
	}

	public DatabaseMeta meta() {
		if (null == meta) {
			checkDatabase();
		}
		return meta;
	}

	public Pager createPager(int pageNumber, int pageSize) {
		return meta().createPager(pageNumber, pageSize);
	}

	public Class<? extends Pager> getPagerType() {
		return meta().getPagerType();
	}

	public void setSqlManager(SqlManager sqlManager) {
		this.sqls = sqlManager;
	}

	public SqlMaker getMaker() {
		return maker;
	}

	public SqlManager getSqls() {
		return sqls;
	}

	public SqlMaker maker() {
		return maker;
	}

	public SqlManager sqls() {
		return this.sqls;
	}

	public NutDao() {
		this.maker = new SqlMaker();
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

	public void execute(final Sql... sqls) {
		run(new ConnCallback() {
			public void invoke(Connection conn) throws Exception {
				for (int i = 0; i < sqls.length; i++) {
					if (null != sqls[i])
						sqls[i].execute(conn);
				}
			}
		});
	}

	public void run(ConnCallback callback) {
		ConnectionHolder ch = DaoUtils.getConnection(getDataSource());
		try {
			ch.invoke(callback);
		} catch (Throwable e) {
			try {
				ch.rollback();
			} catch (SQLException e1) {}
			if (e instanceof RuntimeException)
				throw (RuntimeException) e;
			else
				throw new RuntimeException(e);
		} finally {
			DaoUtils.releaseConnection(ch);
		}

	}

	public int count(Class<?> classOfT) {
		return count(classOfT, null);
	}

	public int count(Class<?> classOfT, Condition condition) {
		Entity<?> entity = getEntity(classOfT);
		Sql sql = maker.func(entity.getTableName(), "COUNT", "*").setCondition(condition);
		execute(sql);
		return sql.getInt();
	}

	public int count(String tableName) {
		return count(tableName, null);
	}

	public int count(String tableName, Condition condition) {
		Sql sql = maker.func(tableName, "COUNT", "*").setCondition(condition);
		execute(sql);
		return sql.getInt();
	}

	public <T> void clear(Class<T> classOfT) {
		this.clear(classOfT, null);
	}

	public void clear(String tableName) {
		this.clear(tableName, null);
	}

	public <T> void clear(Class<T> classOfT, Condition condition) {
		Entity<T> entity = getEntity(classOfT);
		Sql sql;
		if (null == condition) {
			sql = maker.truncate(entity.getTableName());
		} else {
			sql = maker.clear(entity).setCondition(condition);
		}
		execute(sql);
	}

	public void clear(String tableName, Condition condition) {
		Sql sql;
		if (null == condition) {
			sql = maker.truncate(tableName);
		} else {
			sql = maker.clear(tableName).setCondition(condition);
		}
		execute(sql);
	}

	public <T> T clearLinks(final T obj, String regex) {
		if (null != obj) {
			final Entity<? extends Object> entity = getEntity(obj.getClass());
			final Links lns = new Links(obj, entity, regex);
			final NutDao dao = this;
			Trans.exec(new Atom() {
				public void run() {
					lns.walkManys(new LinkWalker() {
						void walk(Link link) {
							if (link.getReferField() == null) {
								dao.clear(link.getTargetClass(), null);
							} else {
								Object value = entity.getMirror().getValue(obj,
										link.getReferField());
								Entity<?> ta = dao.getEntity(link.getTargetClass());
								Sql sql = dao.maker().clear_links(ta, link, value);
								dao.execute(sql);
							}
						}
					});
					lns.walkManyManys(new LinkWalker() {
						void walk(Link link) {
							Object value = entity.getMirror().getValue(obj, link.getReferField());
							Sql sql = dao.maker().clear_links(link.getRelation(), link.getFrom(),
									link.getFrom());
							sql.params().set(link.getFrom(), value);
							dao.execute(sql);
						}
					});
					lns.walkOnes(new LinkWalker() {
						void walk(Link link) {
							Object value = entity.getMirror().getValue(obj, link.getReferField());
							Entity<?> ta = dao.getEntity(link.getTargetClass());
							Sql sql = dao.maker().clear_links(ta, link, value);
							dao.execute(sql);
						}
					});
				}
			});
		}
		return obj;
	}

	public <T> void delete(Class<T> classOfT, long id) {
		Entity<T> entity = getEntity(classOfT);
		EntityField ef = checkIdField(entity);
		Sql sql = maker.delete(entity, ef);
		sql.params().set(ef.getFieldName(), id);
		execute(sql);
	}

	public <T> void delete(Class<T> classOfT, String name) {
		Entity<T> entity = getEntity(classOfT);
		EntityField ef = checkNameField(entity);
		Sql sql = maker.delete(entity, ef);
		sql.params().set(ef.getFieldName(), name);
		execute(sql);
	}

	void _deleteSelf(Entity<?> entity, Object obj) {
		if (null != obj) {
			EntityField idnf = entity.getIdentifiedField();
			if (null == idnf)
				throw DaoException.create(obj, "$IdentifiedField", "delete(Object obj)", null);
			if (idnf.isId()) {
				int id = Castors.me().castTo(idnf.getValue(obj), Integer.class);
				delete(obj.getClass(), id);
			} else if (idnf.isName()) {
				String name = idnf.getValue(obj).toString();
				delete(obj.getClass(), name);
			} else {
				throw DaoException.create(obj, "$IdentifiedField", "delete(Object obj)",
						new Exception("Wrong identified field"));
			}
		}
	}

	public void delete(Object obj) {
		if (null != obj) {
			Entity<?> entity = getEntity(obj.getClass());
			_deleteSelf(entity, obj);
		}
	}

	public <T> void deleteWith(final T obj, String regex) {
		if (null != obj) {
			final Entity<? extends Object> entity = getEntity(obj.getClass());
			final Links lns = new Links(obj, entity, regex);
			final NutDao dao = this;
			Trans.exec(new Atom() {
				public void run() {
					lns.invokeManys(new DeleteManyInvoker(dao));
					lns.invokeManyManys(new DeleteManyManyInvoker(dao));
					_deleteSelf(entity, obj);
					lns.invokeOnes(new DeleteOneInvoker(dao));
				}
			});
		}
	}

	public <T> void deleteLinks(final T obj, String regex) {
		if (null != obj) {
			final Entity<? extends Object> entity = getEntity(obj.getClass());
			final Links lns = new Links(obj, entity, regex);
			final NutDao dao = this;
			Trans.exec(new Atom() {
				public void run() {
					lns.invokeManys(new DeleteManyInvoker(dao));
					lns.invokeManyManys(new DeleteManyManyInvoker(dao));
					lns.invokeOnes(new DeleteOneInvoker(dao));
				}
			});
		}
	}

	public <T> T fetch(Class<T> classOfT, long id) {
		Entity<T> entity = getEntity(classOfT);
		return fetch(entity, id);
	}

	public <T> T fetch(Entity<T> entity, long id) {
		EntityField ef = checkIdField(entity);
		Sql sql = maker.fetch(entity, ef);
		sql.params().set(ef.getFieldName(), id);
		execute(sql);
		return sql.getObject(entity.getType());
	}

	public <T> T fetch(Class<T> classOfT, String name) {
		Entity<T> entity = getEntity(classOfT);
		return fetch(entity, name);
	}

	public <T> T fetch(Entity<T> entity, String name) {
		EntityField ef = checkNameField(entity);
		Sql sql = maker.fetch(entity, ef);
		sql.params().set(ef.getFieldName(), name);
		execute(sql);
		return sql.getObject(entity.getType());
	}

	public <T> T fetch(Class<T> classOfT, Condition condition) {
		Entity<T> entity = getEntity(classOfT);
		return fetch(entity, condition);
	}

	public <T> T fetch(Entity<T> entity, Condition condition) {
		List<T> list = this.query(entity, condition, this.createPager(1, 1));
		if (list.size() == 0)
			return null;
		return list.get(0);
	}

	public <T> T fetch(Class<T> classOfT) {
		return fetch(classOfT, (Condition) null);
	}

	@SuppressWarnings("unchecked")
	public <T> T fetch(T obj) {
		if (null != obj) {
			Entity<T> entity = (Entity<T>) getEntity(obj.getClass());
			EntityField ef = entity.getIdentifiedField();
			Sql sql = maker.fetch(entity, ef);
			sql.params().set(ef.getFieldName(), ef.getValue(obj));
			execute(sql);
			return sql.getObject(entity.getType());
		}
		return null;
	}

	public <T> T fetchLinks(final T obj, String regex) {
		if (null != obj && null != regex) {
			final Entity<?> entity = getEntity(obj.getClass());
			final Links lns = new Links(obj, entity, regex);
			final Mirror<?> mirror = Mirror.me(obj.getClass());
			final Dao dao = this;
			// Many
			lns.walkManys(new LinkWalker() {
				void walk(Link link) {
					Condition c = null;
					if (link.getReferField() != null) {
						Object value = mirror.getValue(obj, link.getReferField());
						c = new ManyCondition(link, value);
					}
					List<?> list = query(link.getTargetClass(), c, null);
					mirror.setValue(obj, link.getOwnField(), Castors.me().cast(list,
							list.getClass(), link.getOwnField().getType(), link.getMapKeyField()));
				}
			});
			// ManyMany
			lns.walkManyManys(new LinkWalker() {
				void walk(Link link) {
					ManyManyCondition mmc = new ManyManyCondition(dao, link, obj);
					List<?> list = query(link.getTargetClass(), mmc, null);
					mirror.setValue(obj, link.getOwnField(), Castors.me().cast(list,
							list.getClass(), link.getOwnField().getType(), link.getMapKeyField()));
				}
			});
			// one
			lns.walkOnes(new LinkWalker() {
				void walk(Link link) {
					Object one;
					Field ownField = link.getReferField();
					Mirror<?> ownType = Mirror.me(ownField.getType());
					if (ownType.isStringLike()) {
						String name = mirror.getValue(obj, ownField).toString();
						one = fetch(link.getTargetClass(), name);
					} else {
						long id = ((Number) mirror.getValue(obj, ownField)).longValue();
						one = fetch(link.getTargetClass(), id);
					}
					mirror.setValue(obj, link.getOwnField(), one);
				}
			});
		}
		return obj;
	}

	public <T> Entity<T> getEntity(Class<T> classOfT) {
		return entities.getEntity(classOfT, meta());
	}

	public int getMaxId(Class<?> classOfT) {
		Entity<?> entity = getEntity(classOfT);
		EntityField ef = checkIdField(entity);
		// Sql sql = maker.create(maker.ptn.MAX,
		// entity.getTableName()).setEntity(entity);
		Sql sql = maker.func(entity.getTableName(), "MAX", ef.getColumnName());
		execute(sql);
		return sql.getInt();
	}

	public <T> T getObject(Class<T> classOfT, ResultSet rs, FieldMatcher fm) {
		return getEntity(classOfT).getObject(rs, fm);
	}

	private <T> T _insertSelf(Entity<?> entity, T obj) {
		Sql sql = maker.insert(entity, obj);
		// Evaluate fetchId SQL
		Sql fetchIdSql = null;
		if (null != entity.getIdField() && entity.getIdField().isAutoIncrement()) {
			fetchIdSql = entity.getIdField().getFetchSql();
			if (null == fetchIdSql)
				throw Lang.makeThrow("Nutz.Dao: fail to find 'nextId' SQL in entity <%s>", entity
						.getType().getName());
		}
		// Execute SQL
		execute(sql, fetchIdSql);
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

	public <T> T insert(T obj) {
		if (null != obj) {
			Entity<?> entity = getEntity(obj.getClass());
			return _insertSelf(entity, obj);
		}
		return null;
	}

	public void insert(String tableName, Chain chain) {
		if (null != chain) {
			Sql sql = maker.insertChain(tableName, chain);
			execute(sql);
		}
	}

	public void insert(Class<?> classOfT, Chain chain) {
		Entity<?> en = getEntity(classOfT);
		insert(en.getTableName(), chain);
	}

	public <T> T insertWith(final T obj, String regex) {
		if (null != obj) {
			final Entity<?> entity = getEntity(obj.getClass());
			final Links lns = new Links(obj, entity, regex);
			final Mirror<?> mirror = Mirror.me(obj.getClass());
			final Dao dao = this;
			Trans.exec(new Atom() {
				public void run() {
					lns.invokeOnes(new InsertOneInvoker(dao, obj, mirror));
					_insertSelf(entity, obj);
					lns.invokeManys(new InsertManyInvoker(dao, obj, mirror));
					lns.invokeManyManys(new InsertManyManyInvoker(dao, obj, mirror));
				}
			});
		}
		return obj;
	}

	public <T> T insertLinks(final T obj, String regex) {
		if (null != obj) {
			final Entity<?> entity = getEntity(obj.getClass());
			final Links lns = new Links(obj, entity, regex);
			final Mirror<?> mirror = Mirror.me(obj.getClass());
			final Dao dao = this;
			Trans.exec(new Atom() {
				public void run() {
					lns.invokeOnes(new InsertOneInvoker(dao, obj, mirror));
					lns.invokeManys(new InsertManyInvoker(dao, obj, mirror));
					lns.invokeManyManys(new InsertManyManyInvoker(dao, obj, mirror));
				}
			});
		}
		return obj;
	}

	public <T> List<T> query(Class<T> classOfT, Condition condition, Pager pager) {
		return query(getEntity(classOfT), condition, pager);
	}

	public <T> List<T> query(Entity<T> entity, Condition condition, Pager pager) {
		// QuerySql<T> sql = maker.makeQuerySQL(entity, pager);
		Sql sql = maker.query(entity);
		sql.getContext().setPager(pager);
		sql.setCondition(condition);
		execute(sql);
		return sql.getList(entity.getType());
	}

	public int update(Object obj) {
		if (null == obj)
			return -1;
		Sql sql = maker.update(getEntity(obj.getClass()), obj);
		execute(sql);
		return sql.getUpdateCount();
	}

	public int update(Class<?> classOfT, Chain chain, Condition condition) {
		Entity<?> en = getEntity(classOfT);
		return update(en.getTableName(), chain, condition);
	}

	public int update(String tableName, Chain chain, Condition condition) {
		Sql sql = maker.updateBatch(tableName, chain).setCondition(condition);
		execute(sql);
		return sql.getUpdateCount();
	}

	public void updateRelation(	Class<?> classOfT,
								String regex,
								final Chain chain,
								final Condition condition) {
		final Links lns = new Links(null, (Entity<?>) getEntity(classOfT), regex);
		Trans.exec(new Atom() {
			public void run() {
				lns.walkManyManys(new LinkWalker() {
					void walk(Link link) {
						Sql sql = maker.updateBatch(link.getRelation(), chain).setCondition(
								condition);
						execute(sql);
					}
				});
			}
		});
	}

	public <T> T updateWith(final T obj, String regex) {
		if (null != obj) {
			final Entity<?> entity = getEntity(obj.getClass());
			final Links lns = new Links(obj, entity, regex);
			final Dao dao = this;
			Trans.exec(new Atom() {
				public void run() {
					update(obj);
					lns.invokeAll(new UpdateInvokder(dao));
				}
			});
		}
		return obj;
	}

	public <T> T updateLinks(T obj, String regex) {
		if (null != obj) {
			final Entity<?> entity = getEntity(obj.getClass());
			final Links lns = new Links(obj, entity, regex);
			final Dao dao = this;
			Trans.exec(new Atom() {
				public void run() {
					lns.invokeAll(new UpdateInvokder(dao));
				}
			});
		}
		return obj;
	}

	public boolean exists(Class<?> classOfT) {
		return exists(getEntity(classOfT).getTableName());
	}

	public boolean exists(final String tableName) {
		final boolean[] ee = { false };
		this.run(new ConnCallback() {
			public void invoke(Connection conn) {
				Statement stat = null;
				ResultSet rs = null;
				try {
					stat = conn.createStatement();
					String sql = "SELECT COUNT(*) FROM " + tableName;
					rs = stat.executeQuery(sql);
					if (rs.next())
						ee[0] = true;
				} catch (SQLException e) {} finally {
					try {
						if (null != rs)
							rs.close();
					} catch (SQLException e) {}
					try {
						if (null != stat)
							stat.close();
					} catch (SQLException e) {}
				}
			}
		});
		return ee[0];
	}

	public int func(Class<?> classOfT, String funcName, String fieldName) {
		Entity<?> entity = getEntity(classOfT);
		EntityField ef = entity.getField(fieldName);
		if (null == ef)
			return func(entity.getTableName(), funcName, fieldName);
		return func(entity.getTableName(), funcName, ef.getColumnName());
	}

	public int func(String tableName, String funcName, String fieldName) {
		Sql sql = maker.func(tableName, funcName, fieldName);
		execute(sql);
		return sql.getInt();
	}

}
