package com.zzh.castor.castor;

import java.util.Collection;
import java.util.Map;

import com.zzh.castor.Castor;
import com.zzh.castor.FailToCastObjectException;
import com.zzh.lang.Lang;

@SuppressWarnings("unchecked")
public class Collection2Map extends Castor<Collection, Map> {

	@Override
	protected Map cast(Collection src, Class<?> toType, String... args)
			throws FailToCastObjectException {
		if (null == args || args.length == 0)
			throw Lang
					.makeThrow(
							FailToCastObjectException.class,
							"For the elements in Collection %s, castors don't know which one is the key field.",
							src.getClass().getName());
		return Lang.collection2map((Class<Map<Object, Object>>) toType, src, args[0]);
	}

}
