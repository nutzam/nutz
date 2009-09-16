package org.nutz.dao.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.nutz.dao.Condition;
import org.nutz.dao.entity.Entity;

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

	public void execute(Connection conn) throws SQLException {
		if (null != condition)
			sql.vars().set("condition", condition.toString(entity));
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
		return sql.vars();
	}

	public VarSet vars() {
		return sql.holders();
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

	public Sql duplicate() {
		Sql newSql = SQLs.create(sql).setCallback(callback).setCondition(condition);
		newSql.getContext().setMatcher(context.getMatcher()).setPager(context.getPager());
		return newSql;
	}

	@Override
	public Object clone() {
		return duplicate();
	}

}
