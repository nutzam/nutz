package org.nutz.mvc.init;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

import org.nutz.ioc.Ioc;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.util.Context;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.ActionInvoker;
import org.nutz.mvc.ActionInvoking;
import org.nutz.mvc.UrlMap;
import org.nutz.mvc.ViewMaker;
import org.nutz.mvc.annotation.AdaptBy;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Encoding;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.Filters;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.invoker.ActionInvokerImpl;

public class UrlMapImpl implements UrlMap {

	private static final Log log = Logs.getLog(UrlMapImpl.class);

	private Ioc ioc;

	private PathNode<ActionInvoker> root;

	private Context context;

	public UrlMapImpl(Ioc ioc, Context context, Class<?> mainModule) {
		this.ioc = ioc;
		this.root = new PathNode<ActionInvoker>();
		this.context = context;
		this.ok = mainModule.getAnnotation(Ok.class);
		this.fail = mainModule.getAnnotation(Fail.class);
		this.adaptBy = mainModule.getAnnotation(AdaptBy.class);
		this.filters = mainModule.getAnnotation(Filters.class);
		this.encoding = mainModule.getAnnotation(Encoding.class);
	}

	private Ok ok;
	private Fail fail;
	private AdaptBy adaptBy;
	private Filters filters;
	private Encoding encoding;

	public boolean add(List<ViewMaker> makers, Class<?> moduleType) {
		// View: OK
		Ok myOk = moduleType.getAnnotation(Ok.class);
		if (null == myOk)
			myOk = ok;
		// View: Defeat
		Fail myFail = moduleType.getAnnotation(Fail.class);
		if (null == myFail)
			myFail = fail;
		// get default HttpAdaptor
		AdaptBy myAb = moduleType.getAnnotation(AdaptBy.class);
		if (null == myAb)
			myAb = adaptBy;
		// get default ActionFilter
		Filters myFlts = moduleType.getAnnotation(Filters.class);
		if (null == myFlts)
			myFlts = filters;
		// get encoding
		Encoding myEncoding = moduleType.getAnnotation(Encoding.class);
		if (null == myEncoding)
			myEncoding = encoding;

		// get base url
		At baseAt = moduleType.getAnnotation(At.class);
		String[] bases;
		if (null == baseAt)
			bases = Lang.array("");
		else if (baseAt.value().length == 0)
			bases = Lang.array("/" + moduleType.getSimpleName().toLowerCase());
		else
			bases = baseAt.value();

		// looping methods
		boolean isModule = false;
		for (Method method : moduleType.getMethods()) {
			// Is it public?
			if (!Modifier.isPublic(method.getModifiers()))
				continue;
			// get Url
			At ats = method.getAnnotation(At.class);
			if (null == ats)
				continue;

			isModule = true;
			// Create invoker
			ActionInvokerImpl invoker = new ActionInvokerImpl(	context,
																ioc,
																makers,
																moduleType,
																method,
																myOk,
																myFail,
																myAb,
																myFlts,
																myEncoding);

			if (log.isDebugEnabled())
				log.debugf("  %20s() @(%s)", method.getName(), Lang.concat(ats.value()));

			// Mapping invoker
			for (String base : bases) {
				String[] paths = ats.value();
				if ((paths.length == 1 && Strings.isBlank(paths[0])) || paths.length == 0) {
					String path = base + "/" + method.getName().toLowerCase();
					root.add(path, invoker);
				} else {
					for (String at : paths)
						root.add(base + at, invoker);
				}
			}
		}
		return isModule;
	}

	public ActionInvoking get(String path) {
		PathInfo<ActionInvoker> info = root.get(path);
		String[] args = Strings.splitIgnoreBlank(info.getRemain(), "[/]");
		return new ActionInvoking(info.getObj(), args);
	}

}
