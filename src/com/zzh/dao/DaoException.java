package com.zzh.dao;

public class DaoException extends RuntimeException {

	private static final long serialVersionUID = 394487130263116657L;

	public DaoException(String message) {
		super(message);
	}

	public <T> DaoException(Sql<T> sql, Exception exception) {
		super(String.format("%s\n--SQL::%s", exception.toString(), sql.toString()));
	}

	public static <T> DaoException create(T obj, String fieldName, String name, Exception e) {
		return new DaoException(String.format("Fail to %s [%s]->[%s], because: '%s'", name,
				obj == null ? "NULL object" : obj.getClass().getName(), fieldName, null == e ? ""
						: e.getMessage()));
	}

}
