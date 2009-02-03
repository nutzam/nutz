package com.zzh.dao;

import com.zzh.castor.Castors;

public class SqlNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -3449985653479894065L;

	public SqlNotFoundException(String key, String[] paths) {
		super(String.format("fail to find SQL '%s' in file paths: %s", key, Castors.me()
				.castToString(paths)));
	}

}
