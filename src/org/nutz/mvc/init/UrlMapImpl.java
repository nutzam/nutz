package org.nutz.mvc.init;

import java.lang.reflect.Method;

import java.util.List;

import org.nutz.ioc.Ioc;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.ActionInvoker;
import org.nutz.mvc.ActionInvoking;
import org.nutz.mvc.UrlMap;
import org.nutz.mvc.ViewMaker;
import org.nutz.mvc.annotation.*;
import org.nutz.mvc.invoker.ActionInvokerImpl;

public class UrlMapImpl implements UrlMap {

	private static final Log log = Logs.getLog(UrlMapImpl.class);

	private Ioc ioc;

	private PathNode<ActionInvoker> root;

	public UrlMapImpl(Ioc ioc) {
		this.ioc = ioc;
		root = new PathNode<ActionInvoker>();
	}

	private Ok ok;
	private Fail fail;
	private AdaptBy adaptBy;
	private Filters filters;
	private Encoding encoding;

	void setEncoding(Encoding encoding) {
		this.encoding = encoding;
	}

	void setOk(Ok ok) {
		this.ok = ok;
	}

	void setFail(Fail fail) {
		this.fail = fail;
	}

	void setAdaptBy(AdaptBy adaptBy) {
		this.adaptBy = adaptBy;
	}

	void setFilters(Filters filters) {
		this.filters = filters;
	}

	public void add(List<ViewMaker> makers, Class<?> moduleType) {
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
		for (Method method : moduleType.getMethods()) {
			// get Url
			At ats = method.getAnnotation(At.class);
			if (null == ats)
				continue;
			// Create invoker
			ActionInvokerImpl invoker = new ActionInvokerImpl(ioc, makers, moduleType, method,
					myOk, myFail, myAb, myFlts, myEncoding);

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
	}

	public ActionInvoking get(String path) {
		PathInfo<ActionInvoker> info = root.get(path);
		String[] args = Strings.splitIgnoreBlank(info.getRemain(), "[/]");
		return new ActionInvoking(info.getObj(), args);
	}

}
