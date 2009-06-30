package org.nutz.trans;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

public abstract class Transaction {

	public abstract int getId();
	
	protected abstract void commit() throws SQLException;

	protected abstract void rollback();

	public abstract Connection getConnection(DataSource dataSource) throws SQLException;

}
