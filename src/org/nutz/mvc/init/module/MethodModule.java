package org.nutz.mvc.init.module;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.nutz.ioc.Ioc;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;
import org.nutz.lang.segment.Segments;
import org.nutz.lang.util.Context;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.ActionFilter;
import org.nutz.mvc.ActionInvoker;
import org.nutz.mvc.HttpAdaptor;
import org.nutz.mvc.View;
import org.nutz.mvc.ViewMaker;
import org.nutz.mvc.adaptor.PairAdaptor;
import org.nutz.mvc.annotation.AdaptBy;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.By;
import org.nutz.mvc.annotation.Encoding;
import org.nutz.mvc.annotation.Filters;
import org.nutz.mvc.init.PathNode;
import org.nutz.mvc.invoker.ActionInvokerImpl2;
import org.nutz.mvc.view.VoidView;

/**
 * 入口函数
 * @author juqkai(juqkai@gmail.com)
 *
 */
public class MethodModule extends ModuleTree<Method> {
	private At at;
	public MethodModule(Context context, Method module, ModuleTree<?> father,
			Ioc ioc) {
		super(context, module, father, ioc);
		init();
	}
	private void init(){
		this.at = module.getAnnotation(At.class);
	}

	private static final Log log = Logs.getLog(MethodModule.class);
	
	public String inputCharset;
	public String outputCharset;


	public void scan() {}

	public void parse(PathNode<ActionInvoker> root) throws Throwable {
		ActionInvoker ai = fetchActionInvoker();
		for(String path : getPath()){
			root.add(path, ai);
		}
	}

	private ActionInvoker fetchActionInvoker() throws Throwable {
		ActionInvokerImpl2 invoker = new ActionInvokerImpl2(module);
		initInvoker(invoker);
		return invoker;
	}
	
	protected void initInvoker(ActionInvokerImpl2 invoker) throws Throwable{
		invoker.filters = evalFilters();
		invoker.adaptor = evalHttpAdaptor();
		invoker.ok = evalView("@Ok", fetchOk());
		invoker.fail = evalView("@Fail", fetchFail());
		evalEncoding();
		invoker.inputCharset = inputCharset;
		invoker.outputCharset = outputCharset;
		
		father.initInvoker(invoker);
	}

	/**
	 * 取得视图
	 * @param <T>
	 * @param viewType 视图注解类型名称
	 * @param ann 视图注解类型
	 * @return
	 * @throws Throwable
	 */
	private <T extends Annotation> View evalView(String viewType, T ann) throws Throwable {
		if (ann == null)
			return new VoidView();

		String str = (String) Mirror.me(ann.getClass()).invoke(ann, "value");
		str = Segments.replace(str, context);
		int pos = str.indexOf(':');
		String type, value;
		if (pos > 0) {
			type = Strings.trim(str.substring(0, pos).toLowerCase());
			value = Strings.trim(pos >= (str.length() - 1) ? null : str
					.substring(pos + 1));
		} else {
			type = str;
			value = null;
		}
		for (ViewMaker maker : fetchMakers()) {
			View view = maker.make(ioc, type, value);
			if (null != view)
				return view;
		}
		throw Lang.makeThrow("Can not eval %s(\"%s\") View for %s", viewType,
				str, this.module);
	}
	/**
	 * 获取编码
	 */
	private void evalEncoding() {
		Encoding encoding = fetchEncoding();
		if (null == encoding) {
			inputCharset = org.nutz.lang.Encoding.UTF8;
			outputCharset = org.nutz.lang.Encoding.UTF8;
		} else {
			inputCharset = encoding.input();
			outputCharset = encoding.output();
		}
	}

	/**
	 * filter
	 * @return
	 */
	private ActionFilter[] evalFilters() {
		Filters flts = fetchFilters();
		ActionFilter[] filters = null;
		if (null != flts) {
			filters = new ActionFilter[flts.value().length];
			for (int i = 0; i < filters.length; i++) {
				By by = flts.value()[i];
				filters[i] = evalObject(by.type(), by.args());
			}
		}
		return filters;
	}

	/**
	 * 取得适配器
	 * @return
	 */
	private HttpAdaptor evalHttpAdaptor() {
		AdaptBy ab = fetchAdaptBy();
		HttpAdaptor adaptor;
		try {
			if (null != ab) {
				adaptor = evalObject(ab.type(), ab.args());
			} else {
				adaptor = new PairAdaptor();
			}
		} catch (Exception e) {
			if (log.isWarnEnabled())
				log.warn(getExceptionMessage(e), e);
			throw Lang.wrapThrow(e);
		}
		adaptor.init(module);
		return adaptor;
	}

	private <T> T evalObject(Class<T> type, String[] args) {
		// Replace the vars
		if (null != args && args.length > 0)
			for (int i = 0; i < args.length; i++) {
				args[i] = Segments.replace(args[i], context);
			}
		/*
		 * 如果参数的形式为: {"ioc:xxx"}，则用 ioc.get(type,"xxx") 获取这个对象
		 */
		if (null != ioc && null != args && args.length == 1
				&& !Strings.isBlank(args[0])) {
			int pos = args[0].indexOf(':');
			if (pos == 3 && pos < (args[0].length() - 1)
					&& "ioc".equalsIgnoreCase(args[0].substring(0, pos))) {
				String name = args[0].substring(pos + 1);
				return ioc.get(type, name);
			}
		}
		return Mirror.me(type).born((Object[]) args);
	}

	protected String[] getPath() {
		List<String> lpaths = new ArrayList<String>();
		String[] bases = father.getPath();
		// Mapping invoker
		String actionPath = null;
		for (String base : bases) {
			String[] paths = at.value();
			// The @At without value
			if ((paths.length == 1 && Strings.isBlank(paths[0])) || paths.length == 0) {
				// Get the action path
				actionPath = base + "/" + module.getName().toLowerCase();
				lpaths.add(actionPath);
				// Print log
				if (log.isDebugEnabled())
					log.debug(String.format("  %20s() @(%s)", module.getName(), actionPath));
			}
			// Have value in @At
			else {
				for (String at : paths) {
					// Get Action
					actionPath = base + at;
					lpaths.add(actionPath);
					// Print log
					if (log.isDebugEnabled())
						log.debug(String.format("  %20s() @(%s)", module.getName(), actionPath));
				}
			}
		}
		return lpaths.toArray(new String[0]);
	}

}
