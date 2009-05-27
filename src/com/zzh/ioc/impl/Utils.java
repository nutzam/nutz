package com.zzh.ioc.impl;

import java.util.HashMap;
import java.util.Map;

import com.zzh.ioc.meta.Fld;
import com.zzh.ioc.meta.Obj;
import com.zzh.lang.Lang;
import com.zzh.lang.Strings;

public class Utils {

	@SuppressWarnings("unchecked")
	static Obj merge(Obj me, Obj parent) {
		if (Strings.isBlank(me.getType()))
			me.setType(parent.getType());
		if (Strings.isBlank(me.getDeposer()))
			me.setDeposer(parent.getDeposer());
		if (Strings.isBlank(me.getDeposeby()))
			me.setDeposeby(parent.getDeposeby());
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
}
