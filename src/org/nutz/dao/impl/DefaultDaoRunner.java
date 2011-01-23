package org.nutz.dao.impl;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.nutz.dao.ConnCallback;
import org.nutz.dao.ConnectionHolder;
import org.nutz.dao.DaoRunner;
import org.nutz.dao.Daos;
import org.nutz.log.Log;
import org.nutz.log.Logs;

public class DefaultDaoRunner implements DaoRunner {
	
	private static final Log log = Logs.getLog(DefaultDaoRunner.class);

	public void run(DataSource dataSource, ConnCallback callback) {
		ConnectionHolder ch = Daos.getConnection(dataSource);
		try {
			ch.invoke(callback);
		}
		catch (Throwable e) {
			try {
				ch.rollback();
			}
			catch (SQLException e1) {
				if (log.isWarnEnabled())
					log.warn("Rollback SQLException !!!", e1);
			}
			if (e instanceof RuntimeException)
				throw (RuntimeException) e;
			else
				throw new RuntimeException(e);
		}
		finally {
			Daos.releaseConnection(ch);
		}
	}

}
