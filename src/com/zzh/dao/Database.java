package com.zzh.dao;

public interface Database {

	Pager createPager(int pageNumber, int pageSize);

	String name();

	/*----------------------------------------------------*/
	public static class Mysql implements Database {
		public Pager createPager(int pageNumber, int pageSize) {
			return Pager.create(Pager.MySQL, pageNumber, pageSize);
		}

		public String name() {
			return "mysql";
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
	}

	/*----------------------------------------------------*/
	public static class DB2 implements Database {
		public Pager createPager(int pageNumber, int pageSize) {
			return Pager.create(Pager.DB2, pageNumber, pageSize);
		}

		public String name() {
			return "db2";
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
	}

	/*----------------------------------------------------*/
	public static class SQLServer implements Database {
		public Pager createPager(int pageNumber, int pageSize) {
			return Pager.create(Pager.SQLServer, pageNumber, pageSize);
		}

		public String name() {
			return "sqlserver";
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
	}
	/*----------------------------------------------------*/
}
