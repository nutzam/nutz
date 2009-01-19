package com.zzh.dao;

import java.sql.SQLException;

public class DaoException extends RuntimeException {

	private static final long serialVersionUID = 394487130263116657L;

	public <T> DaoException(SQL<T> sql, SQLException exception) {
		super(String.format("%s\n--SQL::%s", exception.toString(), sql.toString()));
	}

}
