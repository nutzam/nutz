package com.zzh.castor.castor;

import java.util.Collection;
import java.util.Map;

import com.zzh.castor.Castor;
import com.zzh.castor.FailToCastObjectException;

@SuppressWarnings("unchecked")
public class Map2Collection extends Castor<Map, Collection> {

	@Override
	protected Collection cast(Map src, Class<?> toType, String... args)
			throws FailToCastObjectException {
		Collection coll = createCollection(src, toType);
		coll.add(src);
		return coll;
	}

}
