package org.nutz.dao.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.nutz.dao.Condition;
import org.nutz.dao.entity.Entity;

abstract class AbstractSql implements Sql {

	AbstractSql(SqlLiteral sql) {
		this.sql = sql;
	}

	protected SqlLiteral sql;
	protected SqlContext context;
	protected SqlCallback callback;
	protected Condition condition;
	protected Object result;

	public void execute(Connection conn) throws SQLException {
		if (null != condition)
			sql.vars().set("condition", condition.toString((Entity<?>) context.getEntity()));
		PreparedStatement stat = conn.prepareStatement(sql.toPrepareStatementString());
		process(stat);
		if (null != callback)
			callback.invoke(conn);
	}

	public abstract void process(PreparedStatement stat) throws SQLException;

	public VarSet holders() {
		return sql.vars();
	}

	public VarSet vars() {
		return sql.holders();
	}

	public SqlContext getContext() {
		return context;
	}

	public Sql setContext(SqlContext context) {
		this.context = context;
		return this;
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

	public Sql clone() {
		Sql newSql = SQLs.create(sql);
		return newSql.setContext(context).setCallback(callback).setCondition(condition);
	}
}
