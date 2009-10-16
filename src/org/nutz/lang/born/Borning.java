package org.nutz.lang.born;

public interface Borning<T> {

	T born();
	
	T born(Object[] args);

}
