package com.zzh.lang.born;

public interface BorningInvoker<T> {

	T born() throws Exception;

	void clearArgs();

	T born(Object[] args) throws Exception;
}
