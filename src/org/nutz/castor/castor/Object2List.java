package org.nutz.castor.castor;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.nutz.castor.Castor;
import org.nutz.castor.FailToCastObjectException;
import org.nutz.lang.Lang;

@SuppressWarnings({"rawtypes"})
public class Object2List extends Castor<Object, List> {

	@Override
	@SuppressWarnings("unchecked")
	public List cast(Object src, Type toType, String... args) throws FailToCastObjectException {
		try {
			List<Object> list = (List<Object>) (toType == List.class ? new ArrayList<Object>(1)
																	: Lang.getTypeClass(toType).newInstance());
			list.add(src);
			return list;
		}
		catch (Exception e) {
			throw Lang.wrapThrow(e);
		}
	}

}
