package org.nutz.dao;

public interface Database {

	Pager createPager(int pageNumber, int pageSize);

	String name();

	Class<? extends Pager> getPagerType();

	/*----------------------------------------------------*/
	public static class Mysql implements Database {
		public Pager createPager(int pageNumber, int pageSize) {
			return Pager.create(Pager.MySQL, pageNumber, pageSize);
		}

		public String name() {
			return "mysql";
		}

		public Class<? extends Pager> getPagerType() {
			return Pager.MySQL;
		}
	}

	/*----------------------------------------------------*/
	public static class Postgresql implements Database {
		public Pager createPager(int pageNumber, int pageSize) {
			return Pager.create(Pager.Postgresql, pageNumber, pageSize);
		}

		public String name() {
			return "psql";
		}

		public Class<? extends Pager> getPagerType() {
			return Pager.Postgresql;
		}
	}

	/*----------------------------------------------------*/
	public static class DB2 implements Database {
		public Pager createPager(int pageNumber, int pageSize) {
			return Pager.create(Pager.DB2, pageNumber, pageSize);
		}

		public String name() {
			return "db2";
		}

		public Class<? extends Pager> getPagerType() {
			return Pager.DB2;
		}
	}

	/*----------------------------------------------------*/
	public static class Oracle implements Database {
		public Pager createPager(int pageNumber, int pageSize) {
			return Pager.create(Pager.Oracle, pageNumber, pageSize);
		}

		public String name() {
			return "oracle";
		}

		public Class<? extends Pager> getPagerType() {
			return Pager.Oracle;
		}
	}

	/*----------------------------------------------------*/
	public static class SQLServer implements Database {
		public Pager createPager(int pageNumber, int pageSize) {
			return Pager.create(Pager.SQLServer, pageNumber, pageSize);
		}

		public String name() {
			return "sqlserver";
		}

		public Class<? extends Pager> getPagerType() {
			return Pager.SQLServer;
		}
	}

	/*----------------------------------------------------*/
	public static class Unknwon implements Database {
		public Pager createPager(int pageNumber, int pageSize) {
			return Pager.create(null, pageNumber, pageSize);
		}

		public String name() {
			return "unknown";
		}

		public Class<? extends Pager> getPagerType() {
			return Pager.class;
		}
	}
	/*----------------------------------------------------*/
}
