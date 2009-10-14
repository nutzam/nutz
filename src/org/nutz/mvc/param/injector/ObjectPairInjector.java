package org.nutz.mvc.param.injector;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.castor.Castors;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.mvc.annotation.Param;
import org.nutz.mvc.param.ParamInjector;

public class ObjectPairInjector implements ParamInjector {

	private static interface Injector {
		void inject(HttpServletRequest request, Object obj);
	}

	private static class FieldValueInjector implements Injector {

		private Field field;
		private String name;

		FieldValueInjector(Field field, String name) {
			this.field = field;
			this.field.setAccessible(true);
			this.name = name;
		}

		public void inject(HttpServletRequest request, Object obj) {
			try {
				field.set(obj, Castors.me().castTo(request.getParameter(name), field.getType()));
			} catch (Exception e) {
				throw Lang.wrapThrow(e);
			}
		}

	}

	private static class FieldSetterInjector implements Injector {

		private String name;
		private Method setter;

		FieldSetterInjector(Method setter, String name) {
			this.setter = setter;
			this.name = name;
		}

		public void inject(HttpServletRequest request, Object obj) {
			try {
				setter.invoke(obj, Castors.me().castTo(request.getParameter(name),
						setter.getParameterTypes()[0]));
			} catch (Exception e) {
				throw Lang.wrapThrow(e);
			}
		}

	}

	private Injector[] injectors;
	private Mirror<?> mirror;

	public ObjectPairInjector(Class<?> type) {
		mirror = Mirror.me(type);
		Field[] fields = mirror.getFields();
		injectors = new Injector[fields.length];
		for (int i = 0; i < fields.length; i++) {
			Field f = fields[i];
			Param param = f.getAnnotation(Param.class);
			try {
				Method setter = mirror.getSetter(f);
				injectors[i] = new FieldSetterInjector(setter, null == param ? f.getName() : param
						.value());
			} catch (NoSuchMethodException e) {
				injectors[i] = new FieldValueInjector(f, null == param ? f.getName() : param
						.value());
			}
		}
	}

	public Object get(HttpServletRequest request, HttpServletResponse response, Object refer) {
		Object obj = mirror.born();
		for (Injector inj : injectors)
			inj.inject(request, obj);
		return obj;
	}

}
