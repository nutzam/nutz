package org.nutz.ioc.impl;

import java.util.HashMap;
import java.util.Map;

import org.nutz.aop.ClassAgent;
import org.nutz.ioc.meta.Fld;
import org.nutz.ioc.meta.Lifecycle;
import org.nutz.ioc.meta.Obj;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;

/**
 * @author zozohtnt
 * @author Wendal(wendal1985@gmail.com)
 *
 */
public class Utils {

	@SuppressWarnings("unchecked")
	static Obj merge(Obj me, Obj parent) {
		if (Strings.isBlank(me.getType()))
			me.setType(parent.getType());
		if (me.getLifecycle() == null)
			me.setLifecycle(parent.getLifecycle());
		else
			me.setLifecycle(marge(me.getLifecycle(), parent.getLifecycle()));
		if (null == me.getArgs() || me.getArgs().length == 0)
			if (null != parent.getArgs() && parent.getArgs().length > 0)
				me.setArgs(parent.getArgs());
		if (null == me.getFields())
			me.setFields(parent.getFields());
		else if (null != parent.getFields()) {
			Map<String, Fld> myFields = (Map<String, Fld>) Lang.array2map(HashMap.class, me
					.getFields(), "name");
			Map<String, Fld> parentFields = (Map<String, Fld>) Lang.array2map(HashMap.class, parent
					.getFields(), "name");
			for (String key : parentFields.keySet()) {
				if (!myFields.containsKey(key)) {
					myFields.put(key, parentFields.get(key));
				}
			}
			if (me.getFields().length != myFields.size()) {
				me.setFields(myFields.values().toArray(new Fld[myFields.size()]));
			}
		}
		return me;
	}

	private static Lifecycle marge(Lifecycle me, Lifecycle parent) {
		if (Strings.isBlank(me.getCreate()))
			me.setCreate(parent.getCreate());
		if (Strings.isBlank(me.getDepose()))
			me.setDepose(parent.getDepose());
		if (Strings.isBlank(me.getFetch()))
			me.setFetch(parent.getFetch());
		return me;
	}
	
	public static final String DEFAULT_CLASSAGENT = "org.nutz.aop.javassist.JavassistClassAgent";
	
	public static final ClassAgent newDefaultClassAgent(){
		try {
			return (ClassAgent) Class.forName(DEFAULT_CLASSAGENT).newInstance();
		} catch (Throwable e) {
			Lang.wrapThrow(e);
		}
		return null;
	}
}
