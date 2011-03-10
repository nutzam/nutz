package org.nutz.el.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.nutz.el.*;
import org.nutz.el.val.*;
import org.nutz.lang.Mirror;

public class NutElValueMaker implements ElValueMaker {

	public ElValue make(Object obj) {
		// ElValue
		if (obj instanceof ElValue)
			return (ElValue) obj;

		// null
		if (null == obj)
			return new NullElValue();

		// Map
		if (obj instanceof Map<?, ?>)
			return new MapElValue((Map<?, ?>) obj);

		// List
		if (obj instanceof List<?>)
			return new ListElValue((List<?>) obj);

		// 集合
		if (obj instanceof Collection<?>)
			return new CollectionElValue((List<?>) obj);

		// 数组
		if (obj.getClass().isArray()) {
			return new ArrayElValue(obj);
		}

		/*
		 * 常用类型
		 */
		Mirror<?> mirror = Mirror.me(obj);

		// 字符串
		if (mirror.isStringLike())
			return new StringElValue(obj.toString());

		// 长整
		if (mirror.isLong())
			return new LongElValue((Long) obj);

		// 整数
		if (mirror.isIntLike())
			return new IntegerElValue((Integer) obj);

		// 布尔
		if (mirror.isBoolean())
			return (Boolean) obj ? El.TRUE : El.FALSE;

		// 浮点
		if (mirror.isFloat())
			return new FloatElValue((Float) obj);

		return new PojoElValue<Object>(obj);
	}

}
