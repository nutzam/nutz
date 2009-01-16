package com.zzh.dao.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import com.zzh.dao.Condition;
import com.zzh.dao.Dao;
import com.zzh.dao.ExecutableSQL;
import com.zzh.dao.FetchSQL;
import com.zzh.dao.QuerySQL;
import com.zzh.dao.SQL;
import com.zzh.dao.SQLMaker;
import com.zzh.dao.SQLManager;
import com.zzh.dao.SQLUtils;
import com.zzh.dao.callback.ConnCallback;
import com.zzh.dao.callback.QueryCallback;
import com.zzh.dao.entity.Entity;
import com.zzh.dao.entity.EntityHolder;
import com.zzh.lang.Lang;
import com.zzh.lang.meta.Pager;
import com.zzh.lang.types.Castors;
import com.zzh.trans.Trans;

public class NutDao implements Dao {

	private DataSource dataSource;
	private SQLMaker sqlMaker;
	private SQLManager sqlManager;
	private EntityHolder entityHolder;

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void setSqlManager(SQLManager sqlManager) {
		this.sqlManager = sqlManager;
	}

	public void setCastors(Castors castors) {
		this.entityHolder.changeCastors(castors);
	}

	@Override
	public SQLMaker maker() {
		return this.sqlMaker;
	}

	@Override
	public SQLManager sqls() {
		return this.sqlManager;
	}

	public NutDao() {
		this.entityHolder = new EntityHolder(null);
		this.sqlMaker = new NutSQLMaker(this.entityHolder);
	}

	public NutDao(DataSource dataSource) {
		this();
		this.setDataSource(dataSource);
	}

	public NutDao(DataSource dataSource, SQLManager sqlManager) {
		this();
		this.setDataSource(dataSource);
		this.setSqlManager(sqlManager);
	}

	public NutDao(DataSource dataSource, SQLManager sqlManager, Castors castors) {
		this(dataSource, sqlManager);
		this.entityHolder.changeCastors(castors);
	}

	@Override
	public <T> void clear(Class<T> classOfT, Condition condition) {
		SQL<?> sql = maker().makeClearSQL(classOfT).setCondition(condition, getEntity(classOfT),
				classOfT);
		execute(sql);
	}

	private int evalInt(FetchSQL<Integer> sql) {
		sql.setCallback(new QueryCallback<Integer>() {
			@Override
			public Integer invoke(ResultSet rs) throws SQLException {
				return rs.getInt(1);
			}
		});
		this.execute(sql);
		return ((Integer) sql.getResult()).intValue();
	}

	@Override
	public <T> int count(Class<T> classOfT, Condition condition) {
		FetchSQL<Integer> sql = maker().makeCountSQL(classOfT);
		sql.setCondition(condition, getEntity(classOfT), classOfT);
		return evalInt(sql);
	}

	@Override
	public <T> void delete(Class<T> classOfT, long id) {
		checkIdField(classOfT, this.entityHolder);
		SQL<?> sql = maker().makeDeleteByIdSQL(classOfT, id);
		execute(sql);
	}

	@Override
	public <T> void delete(Class<T> classOfT, String name) {
		checkNameField(classOfT, this.entityHolder);
		SQL<?> sql = maker().makeDeleteByNameSQL(classOfT, name);
		execute(sql);
	}

	@Override
	public void execute(final SQL<?>... sqls) {
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
		Connection conn = SQLUtils.getConnection(getDataSource());
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
			SQLUtils.releaseConnection(conn, getDataSource());
			conn = null;
		}

	}

	@Override
	public void executeBySqlKey(String... keys) {
		List<SQL<?>> sqlList = new ArrayList<SQL<?>>();
		for (String key : keys) {
			SQL<?> sql = sqls().createSQL(key);
			sqlList.add(sql);
		}
		SQL<?> sqls[] = new SQL<?>[sqlList.size()];
		this.execute(sqlList.toArray(sqls));
	}

	@Override
	public <T> T fetch(Class<T> classOfT, long id) {
		final Entity<T> en = checkIdField(classOfT, this.entityHolder);
		FetchSQL<T> sql = maker().makeFetchByIdSQL(classOfT, id);
		sql.setCallback(new QueryCallback<T>() {
			@Override
			public T invoke(ResultSet rs) throws SQLException {
				return en.getObject(rs);
			}
		});
		execute(sql);
		return sql.getResult();
	}

	private static <T> Entity<T> checkIdField(Class<T> classOfT, EntityHolder entityHolder) {
		final Entity<T> en = entityHolder.getEntity(classOfT);
		if (en.getIdField() == null) {
			throw new RuntimeException(String.format("Entity [%] need @Id field", classOfT
					.getName()));
		}
		return en;
	}

	private static <T> Entity<T> checkNameField(Class<T> classOfT, EntityHolder entityHolder) {
		final Entity<T> en = entityHolder.getEntity(classOfT);
		if (en.getNameField() == null) {
			throw new RuntimeException(String.format("Entity [%] need @Name field", classOfT
					.getName()));
		}
		return en;
	}

	@Override
	public <T> T fetch(Class<T> classOfT, String name) {
		final Entity<T> en = checkNameField(classOfT, this.entityHolder);
		FetchSQL<T> sql = maker().makeFetchByNameSQL(classOfT, name);
		sql.setCallback(new QueryCallback<T>() {
			@Override
			public T invoke(ResultSet rs) throws SQLException {
				return en.getObject(rs);
			}
		});
		execute(sql);
		return sql.getResult();
	}

	@Override
	public <T> Entity<T> getEntity(Class<T> classOfT) {
		return entityHolder.getEntity(classOfT);
	}

	@Override
	public <T> int getMaxId(Class<T> classOfT) {
		FetchSQL<Integer> sql = maker().makeFetchMaxIdSQL(classOfT);
		return evalInt(sql);
	}

	@Override
	public <T> T getObject(Class<T> classOfT, ResultSet rs) {
		return entityHolder.getEntity(classOfT).getObject(rs);
	}

	@Override
	public <T> T insert(T obj) {
		Entity<?> en = entityHolder.getEntity(obj.getClass());
		// prepare insert SQL
		ExecutableSQL<?> insertSql = maker().makeInsertSQL(obj.getClass());
		insertSql.setValue(obj);
		// Evaluate fetchId SQL
		FetchSQL<Integer> fetchIdSql = null;
		if (en.hasIdField()) {
			fetchIdSql = maker().makeFetchMaxIdSQL(obj.getClass());
			fetchIdSql.setCallback(new QueryCallback<Integer>() {
				@Override
				public Integer invoke(ResultSet rs) throws SQLException {
					return rs.getInt(1);
				}
			});
		}
		this.execute(insertSql, fetchIdSql);

		// Update Id field if need
		if (null != fetchIdSql) {
			try {
				en.getMirror().setValue(obj, en.getIdentifiedField().getField(),
						fetchIdSql.getResult());
			} catch (Exception e) {
				throw Lang.wrapThrow(e);
			}
		}
		return obj;
	}

	@Override
	public <T> T update(T obj) {
		SQL<?> sql = maker().makeUpdateSQL(obj.getClass());
		sql.setValue(obj);
		execute(sql);
		return obj;
	}

	@Override
	public <T> List<T> query(Class<T> classOfT, Condition condition, Pager pager) {
		final Entity<T> en = this.entityHolder.getEntity(classOfT);
		QuerySQL<T> sql = maker().makeQuerySQL(classOfT);
		sql.setPager(pager);
		sql.setCondition(condition, en, classOfT);
		sql.setCallback(new QueryCallback<T>() {
			@Override
			public T invoke(ResultSet rs) throws SQLException {
				return en.getObject(rs);
			}
		});
		execute(sql);
		return (List<T>) sql.getResult();
	}

}
