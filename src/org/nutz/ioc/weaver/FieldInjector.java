package org.nutz.ioc.weaver;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.nutz.castor.Castors;
import org.nutz.ioc.IocMaking;
import org.nutz.ioc.ValueProxy;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;

public class FieldInjector {

	public static FieldInjector create(Mirror<?> mirror, String fieldName, ValueProxy vp) {
		FieldInjector fi = new FieldInjector();
		fi.valueProxy = vp;

		Method[] sss = mirror.findSetters(fieldName);
		if (sss.length == 1)
			fi.doSet = new DoSetBySetter(sss[0]);
		else
			try {
				Field field = mirror.getField(fieldName);
				try {
					Method setter = mirror.getSetter(field);
					fi.doSet = new DoSetBySetter(setter);
				} catch (NoSuchMethodException e) {
					fi.doSet = new DoSetByField(field);
				}
			} catch (NoSuchFieldException e) {
				throw Lang.wrapThrow(e);
			}
		return fi;
	}

	private ValueProxy valueProxy;
	private DoSet doSet;

	private FieldInjector() {}

	void inject(IocMaking ing, Object obj) {
		Object value = valueProxy.get(ing);
		doSet.set(obj, value);
	}

	/* ================================================================= */
	static interface DoSet {
		void set(Object obj, Object value);
	}

	/* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
	static class DoSetByField implements DoSet {

		private Field field;

		DoSetByField(Field field) {
			this.field = field;
			this.field.setAccessible(true);
		}

		public void set(Object obj, Object value) {
			Object v = null;
			try {
				v = Castors.me().castTo(value, field.getType());
				field.set(obj, v);
			} catch (Exception e) {
				throw Lang.makeThrow("Fail to set '%s' to field %s.'%s' because: %s", v, field
						.getDeclaringClass().getName(), field.getName(), e.getMessage());
			}
		}
	}

	/* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
	static class DoSetBySetter implements DoSet {

		private Method setter;
		private Class<?> valueType;

		public DoSetBySetter(Method setter) {
			this.setter = setter;
			valueType = setter.getParameterTypes()[0];
		}

		public void set(Object obj, Object value) {
			Object v = null;
			try {
				v = Castors.me().castTo(value, valueType);
				setter.invoke(obj, v);
			} catch (Exception e) {
				throw Lang.makeThrow("Fail to set '%s' by setter %s.'%s()' because: %s", v, setter
						.getDeclaringClass().getName(), setter.getName(), e.getMessage());
			}
		}

	}

	/* ================================================================= */
}
