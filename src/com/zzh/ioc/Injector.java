package com.zzh.ioc;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.zzh.castor.Castors;
import com.zzh.lang.Lang;

interface Injector {

	void inject(Object obj);

	/*----------------------------------------------------------------*/
	static class SetterInjector implements Injector {

		private Method setter;
		private Value value;
		private Class<?> paramType;

		SetterInjector(Method setter, Value value) {
			this.setter = setter;
			this.value = value;
			this.paramType = setter.getParameterTypes()[0];
		}

		@Override
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
		private Value value;

		FieldInjector(Field field, Value value) {
			this.value = value;
			field.setAccessible(true);
			this.field = field;
		}

		@Override
		public void inject(Object obj) {
			try {
				Object v = value.get();
				if (null != v && v.getClass() != field.getType())
					v = Castors.me().castTo(v, field.getType());
				field.set(obj, v);
			} catch (Exception e) {
				throw Lang.wrapThrow(e);
			}
		}

	}
}
