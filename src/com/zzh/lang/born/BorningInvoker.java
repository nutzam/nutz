package com.zzh.lang.born;

public interface BorningInvoker<T>  extends Borning<T> {

	T born(Object[] args);
	
}
