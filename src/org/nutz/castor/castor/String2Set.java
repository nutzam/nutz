package org.nutz.castor.castor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.nutz.castor.Castor;
import org.nutz.castor.FailToCastObjectException;
import org.nutz.json.Json;
import org.nutz.lang.Lang;

@SuppressWarnings("rawtypes")
public class String2Set extends Castor<String, Set> {

	@SuppressWarnings("unchecked")
	@Override
	public Set cast(String src, Class<?> toType, String... args) throws FailToCastObjectException {
		List list = Json.fromJson(List.class, Lang.inr(src));
		return new HashSet(list);
	}

}