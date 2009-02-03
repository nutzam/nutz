package com.zzh.lang;

public interface Each<T> {
	void invoke(int index, T obj, int size) throws ExitLoop;
}
