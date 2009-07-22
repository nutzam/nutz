package org.nutz.ioc;

import org.nutz.ioc.meta.Val;

public interface Ioc {

	<T> T get(Class<T> classOfT, String name) throws FailToMakeObjectException,
			ObjectNotFoundException;
	
	boolean hasName(String name);

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

	Ioc add(ValueMaker maker);

	ValueMaker findValueMaker(Val val);

	boolean isSingleton(Class<?> classOfT, String name);

}
