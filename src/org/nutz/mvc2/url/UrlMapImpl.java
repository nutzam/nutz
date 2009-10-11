package org.nutz.mvc2.url;

import java.lang.reflect.Method;
import java.util.List;

import org.nutz.ioc.Ioc;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.mvc2.ActionInvoker;
import org.nutz.mvc2.UrlMap;
import org.nutz.mvc2.ViewMaker;
import org.nutz.mvc2.annotation.*;
import org.nutz.mvc2.invoker.ActionInvokerImpl;

public class UrlMapImpl implements UrlMap {

	private Ioc ioc;

	private PathNode<ActionInvoker> root;

	public UrlMapImpl(Ioc ioc) {
		this.ioc = ioc;
		root = new PathNode<ActionInvoker>();
	}

	private static Ok OK;
	private static Fail FAIL;
	private static AdaptBy AB;

	public void add(List<ViewMaker> makers, Class<?> module) {
		// create object
		IocBy ib = module.getAnnotation(IocBy.class);
		Object obj;
		if (null == ib) {
			try {
				obj = module.newInstance();
			} catch (Exception e) {
				throw Lang.makeThrow("Class '%s' should has a accessible default constructor : '%s'", module.getName(),
						e.getMessage());
			}
		} else {
			obj = ioc.get(module, ib.value());
		}
		// Declare View and HttpAdaptor
		Ok myOk;
		Fail myFail;
		AdaptBy myAb;
		// Check default module
		if (null == OK && null == FAIL && null == AB && null != module.getAnnotation(DefaultModule.class)) {
			// View: OK
			OK = myOk = module.getAnnotation(Ok.class);
			// View: Defeat
			FAIL = myFail = module.getAnnotation(Fail.class);
			// get default HttpAdaptor
			AB = myAb = module.getAnnotation(AdaptBy.class);
		} else {
			// View: OK
			myOk = module.getAnnotation(Ok.class);
			if (null == myOk)
				myOk = OK;
			// View: Defeat
			myFail = module.getAnnotation(Fail.class);
			if (null == myFail)
				myFail = FAIL;
			// get default HttpAdaptor
			myAb = module.getAnnotation(AdaptBy.class);
			if (null == myAb)
				myAb = AB;
		}
		// get base url
		Url baseUrl = module.getAnnotation(Url.class);
		String basePath;
		if (null == baseUrl)
			basePath = "";
		else if (Strings.isBlank(baseUrl.value()))
			basePath = "/" + module.getSimpleName().toLowerCase();
		else
			basePath = baseUrl.value();
		// looping methods
		for (Method method : module.getMethods()) {
			// get Url
			Url url = method.getAnnotation(Url.class);
			if (null == url)
				continue;
			String path;
			if (Strings.isBlank(url.value()))
				path = basePath + "/" + method.getName().toLowerCase();
			else
				path = basePath + url.value();
			// Store invoker
			ActionInvoker invoker = new ActionInvokerImpl(makers, obj, method, myOk, myFail, myAb);
			root.add(path, invoker);
		}
	}

	public ActionInvoker get(String path) {
		return root.get(path);
	}

}
