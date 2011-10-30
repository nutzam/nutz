package org.nutz.castor;

import java.lang.reflect.Array;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;

import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;

/**
 * 抽象转换器，所有的转换器必须继承自它
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * @param <FROM>
 *            从什么类型
 * @param <TO>
 *            转到什么类型
 */
public abstract class Castor<FROM, TO> {

	protected Castor() {
		fromClass = Mirror.getTypeParams(getClass())[0];
		toClass = Mirror.getTypeParams(getClass())[1];
	}

	protected Type fromClass;
	protected Type toClass;

	public Class<?> getFromClass() {

		return (Class<?>) fromClass;
	}

	public Class<?> getToClass() {
		return (Class<?>) toClass;
	}

	public abstract TO cast(FROM src, Type toType, String... args)
			throws FailToCastObjectException;

	@SuppressWarnings({"unchecked"})
	protected static Collection<?> createCollection(Object src, Type toType)
			throws FailToCastObjectException {
	    Class<?> type = Lang.getTypeClass(toType);
		Collection<?> coll = null;
		try {
			coll = (Collection<Object>) type.newInstance();
		}
		catch (Exception e) {
			if (Modifier.isAbstract(type.getModifiers())
				&& type.isAssignableFrom(ArrayList.class)) {
				coll = new ArrayList<Object>(Array.getLength(src));
			}
			if (null == coll)
				throw new FailToCastObjectException(String.format(	"Castors don't know how to implement '%s'",
				                                    type.getName()),
													e);
		}
		return coll;
	}
}
