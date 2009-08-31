package org.nutz.ioc.aop;

import org.nutz.lang.Strings;

interface ObjectMatcher {

	boolean match(Class<?> type, String name);

	/*-----------------------------------------------------------*/
	static class ByName implements ObjectMatcher {

		private String regex;

		ByName(String regex) {
			this.regex = regex;
		}

		public boolean match(Class<?> type, String name) {
			if (Strings.isBlank(name))
				return false;
			return name.matches(regex);
		}

	}

	/*-----------------------------------------------------------*/
	static class ByType implements ObjectMatcher {
		private String regex;

		ByType(String regex) {
			this.regex = regex;
		}

		public boolean match(Class<?> type, String name) {
			if (null == type)
				return false;
			return type.getName().matches(regex);
		}
	}
}
