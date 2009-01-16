package com.zzh.dao.callback;

public interface Callback<T> {

	T invoke(Object... args) throws Exception;

}
