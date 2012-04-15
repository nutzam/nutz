package org.nutz.dao.impl;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.nutz.dao.Dao;
import org.nutz.lang.Lang;

public final class DaoX {

	public static Dao dao;
	public static final ThreadLocal<Connection> conns = new ThreadLocal<Connection>();
	static {
		dao = new NutDao(new SDataSource());
	}

	static class SDataSource implements DataSource {

		public Connection getConnection() throws SQLException {
			return conns.get();
		}

		public PrintWriter getLogWriter() throws SQLException {
			throw Lang.noImplement();
		}

		public void setLogWriter(PrintWriter out) throws SQLException {
			throw Lang.noImplement();
		}

		public void setLoginTimeout(int seconds) throws SQLException {
			throw Lang.noImplement();
		}

		public int getLoginTimeout() throws SQLException {
			throw Lang.noImplement();
		}

		public <T> T unwrap(Class<T> iface) throws SQLException {
			throw Lang.noImplement();
		}

		public boolean isWrapperFor(Class<?> iface) throws SQLException {
			throw Lang.noImplement();
		}

		public Connection getConnection(String username, String password)
				throws SQLException {
			throw Lang.noImplement();
		}

	}

}
