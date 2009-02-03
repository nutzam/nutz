package com.zzh.castor.castor;

import java.util.Collection;

import com.zzh.castor.Castor;
import com.zzh.castor.FailToCastObjectException;

@SuppressWarnings("unchecked")
public class Collection2Collection extends Castor<Collection, Collection> {

	@Override
	protected Collection cast(Collection src, Class<?> toType) throws FailToCastObjectException {
		Collection coll = createCollection(src, toType);
		coll.addAll(src);
		return coll;
	}

}
