package com.zzh.ioc;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import com.zzh.lang.Lang;
import com.zzh.lang.Mirror;
import com.zzh.lang.Strings;

public class JavaObjectMaker extends ObjectMaker {

	@Override
	protected boolean accept(Map<String, Object> properties) {
		return properties.containsKey("java");
	}

	@Override
	protected Value make(Map<String, Object> properties) {
		String callPath = properties.get("java").toString();
		int pos = callPath.lastIndexOf('.');
		String className = Strings.trim(callPath.substring(0, pos));
		String fieldName = Strings.trim(callPath.substring(pos + 1));
		try {
			Mirror<?> mirror = Mirror.me(Class.forName(className));
			try {
				return new JavaFieldValue(mirror, fieldName);
			} catch (NoSuchFieldException e) {
				return new JavaMethodValue(mirror, fieldName);
			}
		} catch (Exception e) {
			throw Lang.wrapThrow(e);
		}
	}

	static class JavaMethodValue extends Value {

		private Method method;

		public JavaMethodValue(Mirror<?> mirror, String fieldName) throws NoSuchMethodException {
			try {
				this.method = mirror.getType().getMethod(Mirror.getGetterName(fieldName));
			} catch (NoSuchMethodException e) {
				this.method = mirror.getType().getMethod(fieldName);
			}
		}

		@Override
		Object get() {
			try {
				return method.invoke(null);
			} catch (Exception e) {
				throw Lang.wrapThrow(e);
			}
		}

		@Override
		boolean isDynamic() {
			return false;
		}

	}

	static class JavaFieldValue extends Value {

		private Field field;

		public JavaFieldValue(Mirror<?> mirror, String fieldName) throws NoSuchFieldException {
			this.field = mirror.getField(fieldName);
		}

		@Override
		Object get() {
			try {
				return field.get(null);
			} catch (Exception e) {
				throw Lang.wrapThrow(e);
			}
		}

		@Override
		boolean isDynamic() {
			return false;
		}

	}

}
