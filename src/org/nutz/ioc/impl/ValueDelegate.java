package org.nutz.ioc.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.nutz.ioc.Ioc;
import org.nutz.ioc.ValueMaker;
import org.nutz.ioc.meta.Map2Obj;
import org.nutz.ioc.meta.Obj;
import org.nutz.ioc.meta.Val;
import org.nutz.lang.Lang;

// ValueDelegate
public abstract class ValueDelegate {

	public abstract Object get();

	public abstract boolean isDynamic();

	/*--------------------------------------------------*/

	@SuppressWarnings("unchecked")
	static <T> ValueDelegate eval(NutIoc ioc, String objectName, Class<?> valueType, Val val) {
		if (null == val || val.isNull()) {
			return new NullValueDelegate();
		} else if (val.isBoolean()) {
			return new BooleanValueDelegate((Boolean) val.getValue());
		} else if (val.isNormal()) {
			return new NormalValueDelegate(val.getValue());
		} else if (val.isArray()) {
			Class<?> elementType = null;
			if (null != valueType && valueType.isArray())
				elementType = valueType.getComponentType();
			return new ArrayValueDelegate(ioc, objectName, elementType, (Object[]) val.getValue());
		} else if (val.isMap()) {
			return new MapValueDelegate(ioc, objectName, (Map<String, Object>) val.getValue());
		} else if (val.isInner()) {
			Obj inner = (Obj) val.getValue();
			// set the outer name to inner, then the field of inner
			// can use {refer:'@name'} to get outer name.
			inner.setName(objectName);
			if (inner.isSingleton())
				return new SingletonInnerValueDelegate(ioc, valueType, inner);
			else
				return new ThrowableInnerValueDelegate(ioc, valueType, inner);
		} else if (val.isRefer()) {
			String value = (String) val.getValue();
			if ("@ioc".equalsIgnoreCase(value))
				return new NormalValueDelegate(ioc);
			else if ("@name".equalsIgnoreCase(value))
				return new NormalValueDelegate(objectName);
			return new ReferValueDelegate(ioc, valueType, (String) val.getValue());
		} else if (val.isSpecial()) {
			ValueMaker vm = ioc.findValueMaker(val);
			if (null != vm)
				return new NormalValueDelegate(vm.make(val));
		}
		throw Lang.makeThrow("Don't know how to deal with val type [%s]", val.getType());
	}

	/*--------------------------------------------------*/
	private static class ReferValueDelegate extends ValueDelegate {

		private Ioc ioc;
		private String name;
		private Class<?> type;
		private Object obj;
		private boolean singleton;

		ReferValueDelegate(Ioc ioc, Class<?> type, String name) {
			this.ioc = ioc;
			this.name = name;
			singleton = ioc.isSingleton(type, name);
			if (singleton)
				obj = ioc.get(type, name);
		}

		@Override
		public Object get() {
			if (null == obj)
				return ioc.get(type, name);
			return obj;
		}

		@Override
		public boolean isDynamic() {
			return !singleton;
		}

	}

	/*--------------------------------------------------*/
	private static class ThrowableInnerValueDelegate extends ValueDelegate {

		private ObjectCreator<?> creator;

		@SuppressWarnings("unchecked")
		<T> ThrowableInnerValueDelegate(NutIoc ioc, Class<?> type, Obj obj) {
			creator = new ObjectCreator(ioc, type, obj);
		}

		@Override
		public Object get() {
			return creator.make().getObject();
		}

		@Override
		public boolean isDynamic() {
			return true;
		}

	}

	/*--------------------------------------------------*/
	private static class SingletonInnerValueDelegate extends ValueDelegate {

		private ObjectHolder<?> oh;

		@SuppressWarnings("unchecked")
		SingletonInnerValueDelegate(NutIoc ioc, Class<?> type, Obj obj) {
			oh = new ObjectCreator(ioc, type, obj).make();
		}

		@Override
		public Object get() {
			return oh.getObject();
		}

		@Override
		public boolean isDynamic() {
			return false;
		}

	}

	/*--------------------------------------------------*/
	private static class MapValueDelegate extends ValueDelegate {

		private boolean dynamic;
		private Map<String, ValueDelegate> vds;
		private Map<String, Object> map;

		<T> MapValueDelegate(NutIoc ioc, String objectName, Map<String, Object> map) {
			vds = new TreeMap<String, ValueDelegate>();
			for (String key : map.keySet()) {
				Object ele = map.get(key);
				Val val;
				if (ele instanceof Val) {
					val = (Val) ele;
				} else {
					val = Map2Obj.object2val(ele);
				}
				ValueDelegate vd = ValueDelegate.eval(ioc, objectName, null, val);
				vds.put(key, vd);
				dynamic |= vd.isDynamic();
			}
			if (!dynamic)
				map = makeMap();
		}

		private Map<String, Object> makeMap() {
			Map<String, Object> map = new TreeMap<String, Object>();
			for (String key : vds.keySet()) {
				ValueDelegate vd = vds.get(key);
				map.put(key, vd.get());
			}
			return map;
		}

		@Override
		public Object get() {
			if (null == map)
				return makeMap();
			return map;
		}

		@Override
		public boolean isDynamic() {
			return dynamic;
		}

	}

	/*--------------------------------------------------*/
	private static class ArrayValueDelegate extends ValueDelegate {

		private boolean dynamic;
		private List<ValueDelegate> vds;
		private Object[] array;

		<T> ArrayValueDelegate(NutIoc ioc, String objectName, Class<?> elementType, Object[] array) {
			vds = new LinkedList<ValueDelegate>();
			if (null != array)
				for (Object ele : array) {
					Val val;
					if (ele instanceof Val) {
						val = (Val) ele;
					} else {
						val = Map2Obj.object2val(ele);
					}
					ValueDelegate vd = ValueDelegate.eval(ioc, objectName, elementType, val);
					vds.add(vd);
					dynamic |= vd.isDynamic();
				}
			if (!dynamic)
				array = makeArray();
		}

		private Object[] makeArray() {
			Object[] objs = new Object[vds.size()];
			int i = 0;
			for (ValueDelegate vd : vds)
				objs[i++] = vd.get();
			return objs;
		}

		@Override
		public Object get() {
			if (null == array)
				return makeArray();
			return array;
		}

		@Override
		public boolean isDynamic() {
			return dynamic;
		}

	}

	/*--------------------------------------------------*/
	private static class NullValueDelegate extends ValueDelegate {

		@Override
		public Object get() {
			return null;
		}

		@Override
		public boolean isDynamic() {
			return false;
		}

	}

	/*--------------------------------------------------*/
	private static class BooleanValueDelegate extends ValueDelegate {

		private Boolean value;

		BooleanValueDelegate(Boolean bool) {
			this.value = bool;
		}

		@Override
		public Object get() {
			return value;
		}

		@Override
		public boolean isDynamic() {
			return false;
		}

	}

	/*--------------------------------------------------*/
	private static class NormalValueDelegate extends ValueDelegate {

		private Object value;

		NormalValueDelegate(Object obj) {
			this.value = obj;
		}

		@Override
		public Object get() {
			return value;
		}

		@Override
		public boolean isDynamic() {
			return false;
		}

	}
}
