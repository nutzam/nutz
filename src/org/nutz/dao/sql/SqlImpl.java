package org.nutz.dao.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.nutz.castor.Castors;
import org.nutz.dao.Condition;
import org.nutz.dao.entity.Entity;
import org.nutz.lang.Strings;

class SqlImpl implements Sql {

	SqlImpl(SqlLiteral sql, FieldTypeAdapter adapter) {
		this.sql = sql;
		this.adapter = adapter;
		this.context = new SqlContext();
	}

	private SqlLiteral sql;
	private SqlContext context;
	private SqlCallback callback;
	private Condition condition;
	private Object result;
	private FieldTypeAdapter adapter;
	private Entity<?> entity;
	private int updateCount;

	public String toString() {
		mergeCondition();
		return sql.toString();
	}

	public void execute(Connection conn) throws SQLException {
		mergeCondition();
		PreparedStatement stat = conn.prepareStatement(sql.toPrepareStatementString());
		adapter.process(stat, sql, entity);
		stat.execute();
		if (null != callback) {
			callback.invoke(conn, stat, this);
			updateCount = stat.getUpdateCount();
			ResultSet rs = stat.getResultSet();
			if (null != rs && !rs.isClosed())
				rs.close();
		}
		if (!stat.isClosed())
			stat.close();
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

	public Sql setAdapter(FieldTypeAdapter adapter) {
		this.adapter = adapter;
		return this;
	}

	public Sql setEntity(Entity<?> entity) {
		this.entity = entity;
		return this;
	}

	public VarSet holders() {
		return sql.getHolders();
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

	@Override
	public int getInt() {
		return Castors.me().castTo(result, int.class);
	}

	@SuppressWarnings("unchecked")
	@Override
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

	@Override
	public <T> T getObject(Class<T> classOfT) {
		return Castors.me().castTo(result, classOfT);
	}

}
