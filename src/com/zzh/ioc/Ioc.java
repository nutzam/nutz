package com.zzh.ioc;

public interface Ioc {

	<T> T getObject(Class<T> classOfT, String name) throws FailToMakeObjectException,
			ObjectNotFoundException;

	String[] keys();

	void depose();

}
