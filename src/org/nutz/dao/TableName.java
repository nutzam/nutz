package org.nutz.dao;

import java.util.Iterator;

import org.nutz.lang.Lang;
import org.nutz.lang.segment.Segment;
import org.nutz.lang.segment.Segments;

public class TableName {

	private static ThreadLocal<Object> object = new ThreadLocal<Object>();

	public static void run(Object refer, Runnable atom) {
		Object old = get();
		set(refer);
		try {
			atom.run();
		} catch (Exception e) {
			throw Lang.wrapThrow(e);
		} finally {
			set(old);
		}
	}

	/**
	 * @return current reference object
	 */
	public static Object get() {
		return object.get();
	}

	/**
	 * @param obj
	 * @return the reference object set by current thread last time
	 */
	public static Object set(Object obj) {
		Object re = get();
		object.set(obj);
		return re;
	}

	public static void clear() {
		set(null);
	}

	public static String render(Segment segment) {
		Object obj = get();
		if (null == obj)
			return segment.toString();
		Segment seg = segment.born();
		if (obj instanceof CharSequence || obj instanceof Number || obj.getClass().isPrimitive()) {
			for (Iterator<String> it = seg.keys().iterator(); it.hasNext();) {
				seg.set(it.next(), obj);
			}
		} else {
			Segments.fillByKeys(seg, obj);
		}
		return seg.toString();
	}
}
