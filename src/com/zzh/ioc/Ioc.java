package com.zzh.ioc;

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

}
