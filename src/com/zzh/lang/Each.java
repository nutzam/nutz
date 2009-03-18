package com.zzh.lang;

public interface Each<T> {
	void invoke(int i, T obj, int length) throws ExitLoop, LoopException;
}
