package org.nutz.ioc.impl;

import java.lang.reflect.Modifier;
import java.util.Map;

import org.nutz.aop.ClassAgent;
import org.nutz.lang.Lang;

public class AopSetting {

	private Map<String, Object[]> byname;

	private Map<String, Object[]> bytype;

	private AopMatcher[] matchName;

	private AopMatcher[] matchType;

	public void init() {
		matchName = evalMatchers(byname);
		matchType = evalMatchers(bytype);
	}

	private AopMatcher[] evalMatchers(Map<String, Object[]> map) {
		AopMatcher[] matchers;
		if (null != map) {
			matchers = new AopMatcher[map.size()];
			int i = 0;
			for (String key : map.keySet())
				matchers[i++] = new AopMatcher(key, Lang.array2array(map.get(key), AopMethod.class));
		} else {
			matchers = new AopMatcher[0];
		}
		return matchers;
	}

	public ClassAgent evalClassAgent(Class<?> type, String name) {
		if (Modifier.isFinal(type.getModifiers()))
			return null;
		if (name.startsWith("$"))
			return null;
		if (null == type)
			throw Lang.makeThrow("eval object [%s] failed, type is null!", name);
		if (null == name)
			throw Lang.makeThrow("eval object [%s] failed, name is null!", type.getName());

		for (AopMatcher am : matchType) {
			ClassAgent ca = am.matchType(type);
			if (null != ca)
				return ca;
		}

		for (AopMatcher am : matchName) {
			ClassAgent ca = am.matchName(name);
			if (null != ca)
				return ca;
		}
		return null;

	}
}
