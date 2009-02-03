package com.zzh.castor.castor;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;

import com.zzh.castor.Castor;
import com.zzh.castor.FailToCastObjectException;

@SuppressWarnings("unchecked")
public class Collection2Array extends Castor<Collection, Object> {

	public Collection2Array() {
		this.fromClass = Collection.class;
		this.toClass = Array.class;
	}

	@Override
	protected Object cast(Collection src, Class<?> toType) throws FailToCastObjectException {
		Collection coll = (Collection) src;
		Class<?> eleType = toType.getComponentType();
		Object ary = Array.newInstance(eleType, coll.size());
		int index = 0;
		for (Iterator it = coll.iterator(); it.hasNext();) {
			Array.set(ary, index++, getCastors().castTo(it.next(), eleType));
		}
		return ary;
	}

}
