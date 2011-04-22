package org.nutz.json2;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;

/**
 * @author juqkai(juqkai@gmail.com)
 *
 */
public abstract class JsonItem implements JsonParse {
	protected Mirror<?> fetchMirror(Type type){
		Class<?> clazz = Lang.getTypeClass(type);
		ParameterizedType pt = null;
		if (type instanceof ParameterizedType) {
			pt = (ParameterizedType) type;
			clazz = (Class<?>) pt.getRawType();
		}
		Mirror<?> me = Mirror.me(clazz);
		return me;
	}
	
	protected ParameterizedType fetchParameterizedType(Type type){
		if (type instanceof ParameterizedType) {
			return (ParameterizedType) type;
		}
		return null;
	}
}
