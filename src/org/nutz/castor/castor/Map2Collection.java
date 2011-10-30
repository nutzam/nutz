package org.nutz.castor.castor;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

import org.nutz.castor.Castor;
import org.nutz.castor.FailToCastObjectException;

@SuppressWarnings({"unchecked", "rawtypes"})
public class Map2Collection extends Castor<Map, Collection> {

	@Override
	public Collection cast(Map src, Type toType, String... args)
			throws FailToCastObjectException {
		Collection coll = createCollection(src, toType);
		coll.add(src);
		return coll;
	}

}
