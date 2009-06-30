package org.nutz.ioc;

public interface Callback<T> {

	void invoke(T obj);
	
}
