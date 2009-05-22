package com.zzh.ioc;

import java.util.Map;

public interface Ioc {

	<T> T get(Class<T> classOfT, String name) throws FailToMakeObjectException,
			ObjectNotFoundException;

	String[] keys();

	/**
	 * Just clear all cache, if some object need to be deposed, depose it.
	 */
	void clear();

	/**
	 * Depose all resources, when the method is invoked, current ioc will not
	 * availiable
	 */
	void depose();

	ObjectMaker findMaker(Map<String, Object> map);

	Ioc add(ObjectMaker maker);

	boolean isSingleton(Class<?> classOfT, String name);

}
