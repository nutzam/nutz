package org.nutz.ioc;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.nutz.castor.Castors;
import org.nutz.castor.FailToCastObjectException;
import org.nutz.ioc.meta.IocEventSet;
import org.nutz.ioc.meta.IocField;
import org.nutz.ioc.meta.IocObject;
import org.nutz.ioc.meta.IocValue;
import org.nutz.lang.Each;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.meta.Pair;

public abstract class Iocs {

	private static final String OBJFIELDS = "^(type|scope|singleton|fields|args|events)$";

	public static boolean isIocObject(Map<String, ?> map) {
		for (Entry<String, ?> en : map.entrySet())
			if (!en.getKey().matches(OBJFIELDS))
				return false;
		return true;
	}

	private static ObjectLoadException E(Throwable e, String fmt, Object... args) {
		return new ObjectLoadException(String.format(fmt, args), e);
	}

	@SuppressWarnings("unchecked")
	public static IocObject map2iobj(Map<String, Object> map) throws ObjectLoadException {
		final IocObject iobj = new IocObject();
		if (!isIocObject(map)) {
			for (Entry<String, Object> en : map.entrySet()) {
				IocField ifld = new IocField();
				ifld.setName(en.getKey());
				ifld.setValue(object2value(en.getValue()));
				iobj.addField(ifld);
			}
		} else {
			Object v = map.get("type");
			// type
			try {
				String typeName = (String) v;
				if (!Strings.isBlank(typeName)) {
					iobj.setType(Class.forName(typeName));
				}
			} catch (Exception e) {
				throw E(e, "Wrong type name: '%s'", v);
			}
			// singleton
			try {
				v = map.get("singleton");
				if (null != v)
					iobj.setSingleton(Castors.me().castTo(v, boolean.class));
			} catch (FailToCastObjectException e) {
				throw E(e, "Wrong singleton: '%s'", v);
			}
			// scope
			v = map.get("scope");
			if (null != v)
				iobj.setScope(v.toString());
			// events
			try {
				v = map.get("events");
				if (null != v) {
					IocEventSet ies = Lang.map2Object((Map<?, ?>) v, IocEventSet.class);
					iobj.setEvents(ies);
				}
			} catch (Exception e) {
				throw E(e, "Wrong events: '%s'", v);
			}
			// args
			try {
				v = map.get("args");
				if (null != v) {
					Lang.each(v, new Each<Object>() {
						public void invoke(int i, Object ele, int length) {
							iobj.addArg(object2value(ele));
						}
					});
				}
			} catch (Exception e) {
				throw E(e, "Wrong args: '%s'", v);
			}
			// fields
			try {
				v = map.get("fields");
				if (null != v) {
					Map<String, Object> fields = (Map<String, Object>) v;
					for (Entry<String, Object> en : fields.entrySet()) {
						IocField ifld = new IocField();
						ifld.setName(en.getKey());
						ifld.setValue(object2value(en.getValue()));
						iobj.addField(ifld);
					}
				}
			} catch (Exception e) {
				throw E(e, "Wrong args: '%s'", v);
			}
		}
		return iobj;
	}

	@SuppressWarnings("unchecked")
	static IocValue object2value(Object obj) {
		IocValue iv = new IocValue();
		// Null
		if (null == obj) {
			iv.setType("null");
			return iv;
		}
		// IocValue
		else if (obj instanceof IocValue) {
			return (IocValue) obj;
		}
		// Map
		else if (obj instanceof Map<?, ?>) {
			Map<String, Object> map = (Map<String, Object>) obj;
			if (map.size() == 1) {
				Entry<String, ?> en = map.entrySet().iterator().next();
				String key = en.getKey();
				// Refer | Java | Env | File
				if (key.matches("^(refer|java|env|file)$")) {
					iv.setType(key);
					iv.setValue(en.getValue());
					return iv;
				}
			}
			// Inner
			if (isIocObject(map)) {
				iv.setType("inner");
				try {
					iv.setValue(map2iobj(map));
				} catch (ObjectLoadException e) {
					throw Lang.wrapThrow(e);
				}
				return iv;
			}
			// Normal map
			Map<String, IocValue> newmap = new HashMap<String, IocValue>();
			for (Entry<String, Object> en : map.entrySet()) {
				IocValue v = object2value(en.getValue());
				newmap.put(en.getKey(), v);
			}
			iv.setType("normal");
			iv.setValue(newmap);
			return iv;
		}
		// Array
		else if (obj.getClass().isArray()) {
			Object[] array = (Object[]) obj;
			IocValue[] ivs = new IocValue[array.length];
			for (int i = 0; i < ivs.length; i++) {
				ivs[i] = object2value(ivs[i]);
			}
			iv.setType("normal");
			iv.setValue(ivs);
		}
		// Collection
		else if (obj instanceof Collection<?>) {
			try {
				Collection<IocValue> values = (Collection<IocValue>) obj.getClass().newInstance();
				Iterator<?> it = ((Collection<?>) obj).iterator();
				while (it.hasNext()) {
					Object o = it.next();
					IocValue v = object2value(o);
					values.add(v);
				}
				iv.setType("normal");
				iv.setValue(values);
				return iv;
			} catch (Exception e) {
				throw Lang.wrapThrow(e);
			}
		}
		// Normal
		iv.setType("normal");
		iv.setValue(obj);
		return iv;
	}

	public static Pair<Class<?>> parseName(String name) {
		String _name = null;
		Class<?> type = null;
		int pos = name.indexOf(':');
		if (pos < 0) {
			_name = Strings.trim(name);
		} else {
			_name = Strings.trim(name.substring(0, pos));
			try {
				String typeName = Strings.trim(name.substring(pos + 1));
				type = Class.forName(typeName);
			} catch (ClassNotFoundException e) {
				throw Lang.wrapThrow(e);
			}
		}
		return new Pair<Class<?>>(_name, type);
	}

	/**
	 * 查看一下 me 中有没有缺少的属性，没有的话，从 it 补充
	 */
	public static IocObject mergeWith(IocObject me, IocObject it) {
		// merge type
		if (me.getType() == null)
			me.setType(it.getType());

		// don't need merge signleon

		// merge events
		if (me.getEvents() == null) {
			me.setEvents(it.getEvents());
		}

		// merge scope
		if (Strings.isBlank(me.getScope())) {
			me.setScope(it.getScope());
		}

		// merge arguments
		if (!me.hasArgs())
			me.copyArgys(it.getArgs());

		// merge fields
		for (IocField fld : it.getFields())
			if (!me.hasField(fld.getName()))
				me.addField(fld);

		return me;
	}
}
