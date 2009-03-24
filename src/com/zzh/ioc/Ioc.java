package com.zzh.ioc;

public interface Ioc {

	<T> T getObject(Class<T> classOfT, String name) throws FailToMakeObjectException,
			ObjectNotFoundException;

	void depose();

	Ioc addDeposer(Deposer deposer);
}
