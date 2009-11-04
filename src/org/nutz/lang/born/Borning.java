package org.nutz.lang.born;

public interface Borning<T> {

	@Deprecated
	T born();
	
	T born(Object[] args);

}
