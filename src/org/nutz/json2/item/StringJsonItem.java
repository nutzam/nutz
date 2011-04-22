package org.nutz.json2.item;

import java.lang.reflect.Type;

import org.nutz.castor.Castors;
import org.nutz.lang.Mirror;

public class StringJsonItem extends SingleJsonItem{
	public Object parse(Type type) {
		Mirror<?> me = fetchMirror(type);
		if (null == me || me.is(String.class))
			return value;
		return Castors.me().castTo(value, me.getType());
	}
}
