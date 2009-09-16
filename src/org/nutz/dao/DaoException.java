package org.nutz.dao;

import org.nutz.dao.sql.Sql;

@SuppressWarnings("serial")
public class DaoException extends RuntimeException {

	public DaoException(String message) {
		super(message);
	}

	public <T> DaoException(Sql sql, Exception exception) {
		super(String.format("%s\n--SQL::%s", exception.toString(), sql.toString()));
	}

	public static <T> DaoException create(T obj, String fieldName, String name, Exception e) {
		if (e instanceof DaoException)
			return (DaoException) e;
		return new DaoException(String.format("Fail to %s [%s]->[%s], because: '%s'", name,
				obj == null ? "NULL object" : obj.getClass().getName(), fieldName, null == e ? ""
						: e.getMessage()));
	}

}
