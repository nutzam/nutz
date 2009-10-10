package org.nutz.mvc2.url;

import java.lang.reflect.Method;

import org.nutz.ioc.Ioc;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.mvc2.ActionInvoker;
import org.nutz.mvc2.UrlMap;
import org.nutz.mvc2.annotation.*;
import org.nutz.mvc2.invoker.ActionInvokerImpl;

public class UrlMapImpl implements UrlMap {

	private Ioc ioc;

	private PathNode<ActionInvoker> root;

	public UrlMapImpl(Ioc ioc) {
		this.ioc = ioc;
		root = new PathNode<ActionInvoker>();
	}

	public void add(Class<?> module) {
		// create object
		IocBy ib = module.getAnnotation(IocBy.class);
		Object obj;
		if (null == ib) {
			try {
				obj = module.newInstance();
			} catch (Exception e) {
				throw Lang.makeThrow(
						"Class '%s' should has a accessible default constructor : '%s'", module
								.getName(), e.getMessage());
			}
		} else {
			obj = ioc.get(module, ib.value());
		}
		// get default views
		Views dftviews = module.getAnnotation(Views.class);
		// get base url
		Url base = module.getAnnotation(Url.class);
		String basePath;
		if (null == base)
			basePath = "";
		else if (Strings.isBlank(base.value()))
			basePath = "/" + module.getSimpleName().toLowerCase();
		else
			basePath = base.value();

		// looping methods
		for (Method method : module.getMethods()) {
			// Url
			Url url = method.getAnnotation(Url.class);
			if (null == url)
				continue;
			String path;
			if (Strings.isBlank(url.value()))
				path = basePath + "/" + method.getName().toLowerCase();
			else
				path = basePath + url.value();
			// Store invoker
			ActionInvoker invoker = new ActionInvokerImpl(obj, method, dftviews);
			root.add(path, invoker);
		}
	}

	public ActionInvoker get(String path) {
		return root.get(path);
	}

}
