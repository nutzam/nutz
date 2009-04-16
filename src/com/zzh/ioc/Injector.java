package com.zzh.ioc;

import java.lang.reflect.Field;

import com.zzh.castor.Castors;
import com.zzh.lang.Invoking;
import com.zzh.lang.Lang;

interface Injector {

	void inject(Object obj);

	/*----------------------------------------------------------------*/
	static class InvokingInjector implements Injector {

		private Invoking invoking;

		InvokingInjector(Invoking invoking) {
			this.invoking = invoking;
		}

		@Override
		public void inject(Object obj) {
			invoking.invoke(obj);
		}
	}

	/*----------------------------------------------------------------*/
	static class FieldInjector implements Injector {

		private Field field;
		private Object value;

		FieldInjector(Field field, Object value) {
			this.value = Castors.me().castTo(value, field.getType());
			field.setAccessible(true);
			this.field = field;
		}

		@Override
		public void inject(Object obj) {
			try {
				field.set(obj, value);
			} catch (Exception e) {
				throw Lang.wrapThrow(e);
			}
		}

	}
}
