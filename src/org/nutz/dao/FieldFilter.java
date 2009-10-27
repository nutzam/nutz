package org.nutz.dao;

import java.util.HashMap;
import java.util.Map;

import org.nutz.lang.Lang;
import org.nutz.trans.Atom;

public class FieldFilter {

	private static ThreadLocal<FieldFilter> FF = new ThreadLocal<FieldFilter>();

	public static FieldFilter create(Class<?> type, boolean ignoreNull) {
		return create(type, null, null, ignoreNull);
	}

	public static FieldFilter create(Class<?> type, String actived) {
		return create(type, actived, null, false);
	}

	public static FieldFilter create(Class<?> type, String actived, boolean ignoreNull) {
		return create(type, actived, null, ignoreNull);
	}

	public static FieldFilter create(Class<?> type, String actived, String locked) {
		return FieldFilter.create(type, actived, locked, false);
	}

	public static FieldFilter create(	Class<?> type,
										String actived,
										String locked,
										boolean ignoreNull) {
		return create(type, FieldMatcher.make(actived, locked, ignoreNull));
	}

	public static FieldFilter create(Class<?> type, FieldMatcher mathcer) {
		FieldFilter ff = new FieldFilter();
		ff.set(type, mathcer);
		return ff;
	}

	private FieldFilter() {
		map = new HashMap<Class<?>, FieldMatcher>();
	}

	private Map<Class<?>, FieldMatcher> map;

	public FieldFilter set(Class<?> type, boolean ignoreNull) {
		map.put(type, FieldMatcher.make(null, null, ignoreNull));
		return this;
	}

	public FieldFilter set(Class<?> type, String actived) {
		map.put(type, FieldMatcher.make(actived, null, false));
		return this;
	}

	public FieldFilter set(Class<?> type, String actived, boolean ignoreNull) {
		map.put(type, FieldMatcher.make(actived, null, ignoreNull));
		return this;
	}

	public FieldFilter set(Class<?> type, String actived, String locked) {
		map.put(type, FieldMatcher.make(actived, locked, false));
		return this;
	}

	public FieldFilter set(Class<?> type, String actived, String locked, boolean ignoreNull) {
		map.put(type, FieldMatcher.make(actived, locked, ignoreNull));
		return this;
	}

	public FieldFilter set(Class<?> type, FieldMatcher matcher) {
		map.put(type, matcher);
		return this;
	}

	public FieldFilter remove(Class<?> type) {
		map.remove(type);
		return this;
	}

	/**
	 * 根据 POJO 的类型，从 ThreadLocal 中获取字段过滤器
	 * 
	 * @param type
	 *            POJO 的类型
	 * @return 字段过滤器
	 */
	public static FieldMatcher get(Class<?> type) {
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
