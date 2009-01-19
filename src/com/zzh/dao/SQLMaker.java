package com.zzh.dao;

import com.zzh.dao.FailToMakeSQLException;

public interface SQLMaker{

	ExecutableSQL<?> makeInsertSQL(Class<?> klass)
			throws FailToMakeSQLException;

	ExecutableSQL<?> makeUpdateSQL(Class<?> klass)
			throws FailToMakeSQLException;

	ExecutableSQL<?> makeDeleteByIdSQL(Class<?> klass, long id)
			throws FailToMakeSQLException;

	ExecutableSQL<?> makeDeleteByNameSQL(Class<?> klass, String name)
			throws FailToMakeSQLException;

	ExecutableSQL<?> makeClearSQL(Class<?> klass) throws FailToMakeSQLException;

	<T> QuerySQL<T> makeQuerySQL(Class<T> klass) throws FailToMakeSQLException;

	<T> FetchSQL<T> makeFetchByIdSQL(Class<T> klass, long id)
			throws FailToMakeSQLException;

	<T> FetchSQL<T> makeFetchByNameSQL(Class<T> klass, String name)
			throws FailToMakeSQLException;

	<T> FetchSQL<Integer> makeFetchMaxIdSQL(Class<T> klass)
			throws FailToMakeSQLException;

	<T> FetchSQL<Integer> makeCountSQL(Class<T> klass)
			throws FailToMakeSQLException;

}
