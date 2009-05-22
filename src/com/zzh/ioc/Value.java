package com.zzh.ioc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.zzh.castor.Castors;
import com.zzh.lang.Lang;

abstract class Value {

	@SuppressWarnings("unchecked")
	static Value make(Ioc ioc, Class<?> classOfT, String name, Object value) throws Exception {
		if (null == value)
			return new NullValue();
		else if (value instanceof Mapping) {
			if (((Mapping) value).isSingleton()) {
				return new StaticMappingValue(ioc, name, (Mapping) value);
			} else {
				return new DynamicMappingValue(ioc, name, (Mapping) value);
			}
		} else if (value instanceof Map) {
			Map<String, Object> map = (Map<String, Object>) value;
			// for @refer
			if (map.containsKey("refer")) {
				Object rv = map.get("refer");
				if (null == rv)
					return new NullValue();
				if ("@ioc".equalsIgnoreCase(rv.toString()))
					return new StaticValue(ioc);
				else if ("@name".equalsIgnoreCase(rv.toString()))
					return new StaticValue(name);
				Value refer = make(ioc, null, name, rv);
				Object referValue = refer.get();
				if (null != referValue)
					return new ReferedValue(classOfT, ioc, referValue.toString());
				return new NullValue();
			}
			// for customized ObjectMaker
			ObjectMaker mk = ioc.findMaker(map);
			if (null != mk) {
				Object re = mk.make(map);
				if (re instanceof Value)
					return (Value) re;
				return new StaticValue(Castors.me().castTo(re, classOfT));
			} else { // Then the map must be a real map,
				return new MapValue(ioc, name, map);
			}
		} else if (value instanceof Collection) {
			return new CollectionValue(ioc, name, (Collection<?>) value);
		} else if (value.getClass().isArray()) {
			return new ArrayValue(ioc,name, (Object[]) value);
		} else if (value.getClass().isArray()) {

		}
		return new StaticValue(Castors.me().castTo(value, classOfT));
	}

	/*-------------------------------------------------------------------*/
	abstract Object get();

	abstract boolean isDynamic();

	/*-------------------------------------------------------------------*/
	static class ArrayValue extends Value {

		private boolean dynamic;
		private List<Value> pairs;
		private Object[] array;

		public ArrayValue(Ioc ioc, String name, Object[] objs) throws Exception {
			this.pairs = new LinkedList<Value>();
			for (Object obj : objs) {
				Value v = Value.make(ioc, null, name, obj);
				dynamic |= v.isDynamic();
				pairs.add(v);
			}
			if (!dynamic)
				array = makeArray();
		}

		@Override
		Object get() {
			if (null != array)
				return array;
			return makeArray();
		}

		Object[] getArray() {
			return (Object[]) get();
		}

		Object[] makeArray() {
			Object[] objs = new Object[pairs.size()];
			int i = 0;
			for (Value v : pairs) {
				objs[i++] = v.get();
			}
			return objs;
		}

		@Override
		boolean isDynamic() {
			return dynamic;
		}

	}

	/*-------------------------------------------------------------------*/
	static class NullValue extends Value {

		@Override
		Object get() {
			return null;
		}

		@Override
		boolean isDynamic() {
			return false;
		}

	}

	/*-------------------------------------------------------------------*/
	static class CollectionValue extends Value {

		private boolean dynamic;
		private List<Value> pairs;
		private List<Object> list;

		public CollectionValue(Ioc ioc, String name, Collection<?> coll) throws Exception {
			this.pairs = new LinkedList<Value>();
			for (Object o : coll) {
				Value v = Value.make(ioc, null, name, o);
				dynamic |= v.isDynamic();
				pairs.add(v);
			}
			if (!dynamic)
				list = makeList();
		}

		@Override
		Object get() {
			if (null != list)
				return list;
			return makeList();
		}

		private List<Object> makeList() {
			List<Object> list = new ArrayList<Object>(pairs.size());
			for (Iterator<Value> it = pairs.iterator(); it.hasNext();) {
				list.add(it.next().get());
			}
			return list;
		}

		@Override
		boolean isDynamic() {
			return dynamic;
		}

	}

	/*-------------------------------------------------------------------*/
	static class MapValue extends Value {

		private static class Pair {
			private String key;
			private Value value;
		}

		private List<Pair> dynamics;
		private List<Pair> pairs;
		private Map<String, Object> map;

		public MapValue(Ioc ioc, String name, Map<String, Object> map) throws Exception {
			this.dynamics = new LinkedList<Pair>();
			this.pairs = new LinkedList<Pair>();
			for (Iterator<String> it = map.keySet().iterator(); it.hasNext();) {
				Pair p = new Pair();
				p.key = it.next();
				p.value = Value.make(ioc, null, name, map.get(p.key));
				if (p.value.isDynamic())
					dynamics.add(p);
				pairs.add(p);
			}
			this.map = isDynamic() ? null : makeMap();
		}

		@Override
		Object get() {
			if (null != map)
				return map;
			return makeMap();
		}

		private Map<String, Object> makeMap() {
			Map<String, Object> map = new HashMap<String, Object>();
			for (Iterator<Pair> it = pairs.iterator(); it.hasNext();) {
				Pair p = it.next();
				map.put(p.key, p.value.get());
			}
			return map;
		}

		@Override
		boolean isDynamic() {
			return dynamics.size() > 0;
		}

	}

	/*-------------------------------------------------------------------*/
	static class ReferedValue extends Value {

		private Ioc ioc;
		private String name;
		private Class<?> classOfT;

		public ReferedValue(Class<?> classOfT, Ioc ioc, String name) throws Exception {
			this.classOfT = classOfT;
			this.ioc = ioc;
			this.name = name;
		}

		@Override
		Object get() {
			return ioc.get(classOfT, name);
		}

		@Override
		boolean isDynamic() {
			return !ioc.isSingleton(classOfT, name);
		}

	}

	/*-------------------------------------------------------------------*/
	static class StaticValue extends Value {

		private Object value;

		public StaticValue(Object value) {
			this.value = value;
		}

		@Override
		Object get() {
			return value;
		}

		@Override
		boolean isDynamic() {
			return false;
		}

	}

	/*-------------------------------------------------------------------*/
	static class DynamicMappingValue extends Value {

		private ObjectMapping<?> mapping;

		@SuppressWarnings("unchecked")
		public DynamicMappingValue(Ioc ioc, String name, Mapping mapping) {
			try {
				this.mapping = new ObjectMapping(ioc, null, name, mapping);
			} catch (Exception e) {
				throw Lang.makeThrow("Fail to make MappingValue because '%s'", e.getMessage());
			}
		}

		@Override
		Object get() {
			return mapping.make().getObject();
		}

		@Override
		boolean isDynamic() {
			return true;
		}

	}

	/*-------------------------------------------------------------------*/
	static class StaticMappingValue extends Value {

		private ObjectHolder<?> oh;

		@SuppressWarnings("unchecked")
		public StaticMappingValue(Ioc ioc, String name, Mapping mapping) {
			try {
				this.oh = new ObjectMapping(ioc, null, name, mapping).make();
			} catch (Exception e) {
				throw Lang.makeThrow("Fail to make MappingValue because '%s'", e.getMessage());
			}
		}

		@Override
		Object get() {
			return oh.getObject();
		}

		@Override
		boolean isDynamic() {
			return true;
		}

	}

}
