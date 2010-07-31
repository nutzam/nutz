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
		Ok myOk = moduleType.getAnnotation(Ok.class);
		if (null == myOk)
			myOk = ok;
		Fail myFail = moduleType.getAnnotation(Fail.class);
		if (null == myFail)
			myFail = fail;
		AdaptBy myAb = moduleType.getAnnotation(AdaptBy.class);
		if (null == myAb)
			myAb = adaptBy;
		Filters myFlts = moduleType.getAnnotation(Filters.class);
		if (null == myFlts)
			myFlts = filters;
		Encoding myEncoding = moduleType.getAnnotation(Encoding.class);
		if (null == myEncoding)
			myEncoding = encoding;

		// get base url
		At baseAt = moduleType.getAnnotation(At.class);
		String[] bases;
		if (null == baseAt)
			bases = Lang.array("");
		else if (null == baseAt.value() || baseAt.value().length == 0)
			bases = Lang.array("/" + moduleType.getSimpleName().toLowerCase());
		else
			bases = baseAt.value();

		/*
		 * looping all methods in the class, if has one @At, this class will be
		 * take as 'Module'
		 */
		boolean isModule = false;
		for (Method method : moduleType.getMethods()) {
			/*
			 * Make sure the public method, which with @At can be take as the
			 * enter method
			 */
			if (!Modifier.isPublic(method.getModifiers()) || !method.isAnnotationPresent(At.class))
				continue;
			/*
			 * Then, check the @At
			 */
			At ats = method.getAnnotation(At.class);
			isModule = true;

			// Create invoker
			ActionInvoker invoker = new ActionInvokerImpl(	context,
															ioc,
															makers,
															moduleType,
															method,
															myOk,
															myFail,
															myAb,
															myFlts,
															myEncoding);

			// Mapping invoker
			for (String base : bases) {
				String[] paths = ats.value();
				// The @At without value
				if ((paths.length == 1 && Strings.isBlank(paths[0])) || paths.length == 0) {
					// Get the action path
					String actionPath = base + "/" + method.getName().toLowerCase();
					root.add(actionPath, invoker);

					// Print log
					if (log.isDebugEnabled())
						log.debugf("  %20s() @(%s)", method.getName(), actionPath);
				}
				// More than one value in @At
				else {
					for (String at : paths) {
						// Get Action
						String actionPath = base + at;
						root.add(actionPath, invoker);

						// Print log
						if (log.isDebugEnabled())
							log.debugf("  %20s() @(%s)", method.getName(), actionPath);
					}
				}
			}
		}
		return isModule;
	}

	public ActionInvoking get(String path) {
		PathInfo<ActionInvoker> info = root.get(path);
		String[] args = Strings.splitIgnoreBlank(info.getRemain(), "[/]");
		return new ActionInvoking(info, args);
	}

}
