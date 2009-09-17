package org.nutz.dao.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.nutz.castor.Castors;
import org.nutz.dao.Condition;
import org.nutz.dao.entity.Entity;
import org.nutz.lang.Strings;

public class SqlImpl implements Sql {

	SqlImpl(SqlLiteral sql, StatementAdapter adapter) {
		this.sql = sql;
		this.adapter = adapter;
		this.context = new SqlContext();
		updateCount = -1;
	}

	private SqlLiteral sql;
	private SqlContext context;
	private SqlCallback callback;
	private Condition condition;
	private Object result;
	private StatementAdapter adapter;
	private Entity<?> entity;
	private int updateCount;

	public String toString() {
		mergeCondition();
		return sql.toString();
	}

	public void execute(Connection conn) throws SQLException {
		mergeCondition();
		updateCount = -1;
		String str = sql.toPrepareStatementString();
		// SELECT ...
		if (sql.isSELECT()) {
			if (null != callback) {
				PreparedStatement stat = conn.prepareStatement(str, ResultSet.TYPE_SCROLL_INSENSITIVE,
						ResultSet.CONCUR_READ_ONLY);
				adapter.process(stat, sql, entity);
				ResultSet rs = stat.executeQuery();
				setResult(callback.invoke(conn, rs, this));
				rs.close();
				stat.close();
			}
		}
		// DELETE | CREATE | DROP ...
		else if (sql.isDELETE() || sql.isCREATE() || sql.isDROP()) {
			Statement stat = conn.createStatement();
			stat.execute(sql.toString());
			stat.close();
			if (null != callback)
				callback.invoke(conn, null, this);
		}
		// UPDATE | INSERT ...
		else if (sql.isUPDATE() || sql.isINSERT()) {
			PreparedStatement stat = conn.prepareStatement(str, ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
			adapter.process(stat, sql, entity);
			stat.execute();
			updateCount = stat.getUpdateCount();
			stat.close();
			if (null != callback)
				callback.invoke(conn, null, this);
		}

	}

	private void mergeCondition() {
		if (null != condition) {
			String cnd = Strings.trim(condition.toString(entity));
			if (cnd != null) {
				String cndu = cnd.toUpperCase();
				if (!cndu.startsWith("WHERE") && !cndu.startsWith("ORDER BY"))
					cnd += " WHERE ";
				sql.getVars().set("condition", cnd);
			}
		}
	}

	public int getUpdateCount() {
		return updateCount;
	}

	public Sql setAdapter(StatementAdapter adapter) {
		this.adapter = adapter;
		return this;
	}

	public Sql setEntity(Entity<?> entity) {
		this.entity = entity;
		return this;
	}

	public VarSet params() {
		return sql.getParams();
	}

	public VarSet vars() {
		return sql.getVars();
	}

	public SqlContext getContext() {
		return context;
	}

	public SqlCallback getCallback() {
		return callback;
	}

	public Sql setCallback(SqlCallback callback) {
		this.callback = callback;
		return this;
	}

	public Condition getCondition() {
		return condition;
	}

	public Sql setCondition(Condition condition) {
		this.condition = condition;
		return this;
	}

	public Object getResult() {
		return result;
	}

	public Sql setResult(Object result) {
		this.result = result;
		return this;
	}

	public Entity<?> getEntity() {
		return entity;
	}

	public SqlLiteral getSqlLiteral() {
		return sql;
	}

	public Sql duplicate() {
		Sql newSql = SQLs.create(sql).setCallback(callback).setCondition(condition);
		newSql.getContext().setMatcher(context.getMatcher()).setPager(context.getPager());
		return newSql;
	}

	@Override
	public Object clone() {
		return duplicate();
	}

	public int getInt() {
		return Castors.me().castTo(result, int.class);
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> getList(Class<T> classOfT) {
		if (null == result)
			return null;
		if (result instanceof List) {
			if (((List<T>) result).isEmpty())
				return (List<T>) result;
			if (((List) result).get(0).getClass().isAssignableFrom(classOfT))
				return (List<T>) result;
			ArrayList list = new ArrayList(((List) result).size());
			int i = 0;
			Iterator it = ((List) result).iterator();
			while (it.hasNext()) {
				list.set(i++, Castors.me().castTo(it.next(), classOfT));
			}
			return list;
		}
		return Castors.me().cast(result, result.getClass(), List.class, classOfT.getName());
	}

	public <T> T getObject(Class<T> classOfT) {
		return Castors.me().castTo(result, classOfT);
	}

}
