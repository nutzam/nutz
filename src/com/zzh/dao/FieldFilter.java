package com.zzh.dao;

import java.util.HashMap;
import java.util.Map;

import com.zzh.lang.Lang;
import com.zzh.trans.Atom;

public class FieldFilter {

	private static ThreadLocal<FieldFilter> FF = new ThreadLocal<FieldFilter>();

	public static FieldFilter create(Class<?> type, String actived) {
		return create(type, actived, null);
	}

	public static FieldFilter create(Class<?> type, String actived, String locked) {
		FieldFilter ff = new FieldFilter();
		ff.add(type, actived, locked);
		return ff;
	}

	private FieldFilter() {
		map = new HashMap<Class<?>, FieldMatcher>();
	}

	private Map<Class<?>, FieldMatcher> map;

	public FieldFilter add(Class<?> type, String actived, String locked) {
		map.put(type, FieldMatcher.make(actived, locked));
		return this;
	}

	public FieldFilter add(Class<?> type, FieldMatcher fm) {
		map.put(type, fm);
		return this;
	}

	public FieldFilter remove(Class<?> type) {
		map.remove(type);
		return this;
	}

	static FieldMatcher get(Class<?> type) {
		FieldFilter ff = FF.get();
		if (null == ff)
			return null;
		return ff.map.get(type);
	}

	public void run(Atom atom) {
		FF.set(this);
		try {
			atom.run();
		} catch (Exception e) {
			throw Lang.wrapThrow(e);
		} finally {
			FF.remove();
		}
	}

}
