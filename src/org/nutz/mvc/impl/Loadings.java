package org.nutz.mvc.impl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.nutz.ioc.annotation.InjectName;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;
import org.nutz.lang.segment.Segments;
import org.nutz.lang.util.Context;
import org.nutz.mvc.ActionFilter;
import org.nutz.mvc.ActionInfo;
import org.nutz.mvc.HttpAdaptor;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.ObjectInfo;
import org.nutz.mvc.annotation.AdaptBy;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.By;
import org.nutz.mvc.annotation.Chain;
import org.nutz.mvc.annotation.Encoding;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.Filters;
import org.nutz.mvc.annotation.Ok;

abstract class Loadings {

	static ActionInfo createInfo(Class<?> type) {
		ActionInfo ai = new ActionInfo();
		evalEncoding(ai, type.getAnnotation(Encoding.class));
		evalHttpAdaptor(ai, type.getAnnotation(AdaptBy.class));
		evalActionFilters(ai, type.getAnnotation(Filters.class));
		evalOk(ai, type.getAnnotation(Ok.class));
		evalFail(ai, type.getAnnotation(Fail.class));
		evalAt(ai, type.getAnnotation(At.class), type.getSimpleName());
		evalActionChainMaker(ai, type.getAnnotation(Chain.class));
		evalModule(ai, type);
		return ai;
	}

	static ActionInfo createInfo(Method method) {
		ActionInfo ai = new ActionInfo();
		evalEncoding(ai, method.getAnnotation(Encoding.class));
		evalHttpAdaptor(ai, method.getAnnotation(AdaptBy.class));
		evalActionFilters(ai, method.getAnnotation(Filters.class));
		evalOk(ai, method.getAnnotation(Ok.class));
		evalFail(ai, method.getAnnotation(Fail.class));
		evalAt(ai, method.getAnnotation(At.class), method.getName());
		evalActionChainMaker(ai, method.getAnnotation(Chain.class));
		ai.setMethod(method);
		return ai;
	}

	private static void evalActionChainMaker(ActionInfo ai, Chain cb) {
		if (null != cb) {
			ai.setChainName(cb.value());
		}
	}

	private static void evalAt(ActionInfo ai, At at, String def) {
		if (null != at) {
			if (null == at.value() || at.value().length == 0) {
				ai.setPaths(Lang.array("/" + def.toLowerCase()));
			} else {
				ai.setPaths(at.value());
			}
			if (!Strings.isBlank(at.key()))
				ai.setPathKey(at.key());
		}
	}

	private static void evalFail(ActionInfo ai, Fail fail) {
		if (null != fail) {
			ai.setFailView(fail.value());
		}
	}

	private static void evalOk(ActionInfo ai, Ok ok) {
		if (null != ok) {
			ai.setOkView(ok.value());
		}
	}

	private static void evalModule(ActionInfo ai, Class<?> type) {
		ai.setModuleType(type);
		InjectName in = type.getAnnotation(InjectName.class);
		if (null != in)
			ai.setInjectName(Strings.isBlank(in.value()) ? Strings.lowerFirst(type.getSimpleName())
														: Strings.trim(in.value()));
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	private static void evalActionFilters(ActionInfo ai, Filters filters) {
		if (null != filters) {
			List<ObjectInfo<? extends ActionFilter>> list = new ArrayList<ObjectInfo<? extends ActionFilter>>(filters.value().length);
			for (By by : filters.value()) {
				list.add(new ObjectInfo(by.type(), by.args()));
			}
			ai.setFilterInfos(list.toArray(new ObjectInfo[list.size()]));
		}
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	private static void evalHttpAdaptor(ActionInfo ai, AdaptBy ab) {
		if (null != ab) {
			ai.setAdaptorInfo((ObjectInfo<? extends HttpAdaptor>) new ObjectInfo(	ab.type(),
																					ab.args()));
		}
	}

	private static void evalEncoding(ActionInfo ai, Encoding encoding) {
		if (null == encoding) {
			ai.setInputEncoding(org.nutz.lang.Encoding.UTF8);
			ai.setOutputEncoding(org.nutz.lang.Encoding.UTF8);
		} else {
			ai.setInputEncoding(Strings.sNull(encoding.input(), org.nutz.lang.Encoding.UTF8));
			ai.setOutputEncoding(Strings.sNull(encoding.output(), org.nutz.lang.Encoding.UTF8));
		}
	}

	public static <T> T evalObj(NutConfig config, Class<T> type, String[] args) {
		// 用上下文替换参数
		Context context = config.getLoadingContext();
		for (int i = 0; i < args.length; i++) {
			args[i] = Segments.replace(args[i], context);
		}
		// 判断是否是 Ioc 注入

		if (args.length == 1 && args[0].startsWith("ioc:")) {
			String name = Strings.trim(args[0].substring(4));
			return config.getIoc().get(type, name);
		}
		return Mirror.me(type).born((Object[]) args);
	}

}
