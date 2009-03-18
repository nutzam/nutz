package com.zzh.dao;

import com.zzh.castor.Castors;

@SuppressWarnings("serial")
public class SqlNotFoundException extends RuntimeException {

	public SqlNotFoundException(String key, String[] paths) {
		super(String.format("fail to find SQL '%s' in file paths: %s", key, Castors.me()
				.castToString(paths)));
	}

}
