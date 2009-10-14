package org.nutz.ioc.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.nutz.castor.Castors;
import org.nutz.lang.Lang;

interface Injector {

	void inject(Object obj);

	/*----------------------------------------------------------------*/
	static class SetterInjector implements Injector {

		private Method setter;
		private ValueDelegate value;
		private Class<?> paramType;

		public SetterInjector(Method setter, ValueDelegate value) {
			this.setter = setter;
			this.value = value;
			this.paramType = setter.getParameterTypes()[0];
		}

		public void inject(Object obj) {
			try {
				Object v = value.get();
				if (null != v && v.getClass() != paramType)
					v = Castors.me().castTo(v, paramType);
				setter.invoke(obj, v);
			} catch (Exception e) {
				throw Lang.wrapThrow(e);
			}
		}
	}

	/*----------------------------------------------------------------*/
	static class FieldInjector implements Injector {

		private Field field;
		private ValueDelegate value;

		public FieldInjector(Field field, ValueDelegate value) {
			this.value = value;
			field.setAccessible(true);
			this.field = field;
		}

		public void inject(Object obj) {
			try {
				Object v = null == value ? null : value.get();
				if (null != v && v.getClass() != field.getType())
					v = Castors.me().castTo(v, field.getType());
				field.set(obj, v);
			} catch (Exception e) {
				throw Lang.wrapThrow(e);
			}
		}

	}
}
