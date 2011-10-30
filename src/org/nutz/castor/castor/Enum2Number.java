package org.nutz.castor.castor;

import java.lang.reflect.Type;

import org.nutz.castor.Castor;
import org.nutz.castor.FailToCastObjectException;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;

@SuppressWarnings({"rawtypes"})
public class Enum2Number extends Castor<Enum, Number> {

	@Override
	public Number cast(Enum src, Type toType, String... args) throws FailToCastObjectException {
		Mirror<?> mirror = Mirror.me(Integer.class);
		Integer re = src.ordinal();
		if (mirror.canCastToDirectly(Lang.getTypeClass(toType)))
			return re;
		return (Number) Mirror.me(toType).born(re.toString());
	}

}
