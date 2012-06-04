package org.nutz.mvc.impl;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.nutz.ioc.annotation.InjectName;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;
import org.nutz.lang.segment.Segments;
import org.nutz.lang.util.Context;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.ActionFilter;
import org.nutz.mvc.ActionInfo;
import org.nutz.mvc.HttpAdaptor;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.ObjectInfo;
import org.nutz.mvc.annotation.AdaptBy;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.By;
import org.nutz.mvc.annotation.Chain;
import org.nutz.mvc.annotation.DELETE;
import org.nutz.mvc.annotation.Encoding;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.Filters;
import org.nutz.mvc.annotation.GET;
import org.nutz.mvc.annotation.Modules;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.POST;
import org.nutz.mvc.annotation.PUT;
import org.nutz.mvc.annotation.PathMap;
import org.nutz.resource.Scans;

public class LoadingsImpl{

	private static final Log log = Logs.get();

	public static ActionInfo createInfo(Class<?> type) {
		ActionInfo ai = new ActionInfo();
		evalEncoding(ai, type.getAnnotation(Encoding.class));
		evalHttpAdaptor(ai, type.getAnnotation(AdaptBy.class));
		evalActionFilters(ai, type.getAnnotation(Filters.class));
		evalPathMap(ai, type.getAnnotation(PathMap.class));
		evalOk(ai, type.getAnnotation(Ok.class), type);
		evalFail(ai, type.getAnnotation(Fail.class));
		evalAt(ai, type.getAnnotation(At.class), type);
		evalActionChainMaker(ai, type.getAnnotation(Chain.class));
		evalModule(ai, type);
		return ai;
	}

	public static ActionInfo createInfo(Method method,Class<?> module) {
		ActionInfo ai = new ActionInfo();
		evalEncoding(ai, method.getAnnotation(Encoding.class));
		evalHttpAdaptor(ai, method.getAnnotation(AdaptBy.class));
		evalActionFilters(ai, method.getAnnotation(Filters.class));
		evalOk(ai, method.getAnnotation(Ok.class), method.getName(),module);
		evalFail(ai, method.getAnnotation(Fail.class));
		evalAt(ai, method.getAnnotation(At.class), method.getName(),module);
		evalActionChainMaker(ai, method.getAnnotation(Chain.class));
		evalHttpMethod(ai, method);
		ai.setMethod(method);
		return ai;
	}

	public static Set<Class<?>> scanModules(Class<?> mainModule) {
		Modules ann = mainModule.getAnnotation(Modules.class);
		boolean scan = null == ann ? false : ann.scanPackage();
		// 准备扫描列表
		List<Class<?>> list = new LinkedList<Class<?>>();
		list.add(mainModule);
		if (null != ann) {
			for (Class<?> module : ann.value()) {
				list.add(module);
			}
		}
		// 扫描包
		Set<Class<?>> modules = new HashSet<Class<?>>();
		if (null != ann && ann.packages() != null && ann.packages().length > 0) {
			for (String packageName : ann.packages())
				scanModuleInPackage(modules, packageName);
		}
		for (Class<?> type : list) {
			// mawm 为了兼容maven,根据这个type来加载该type所在jar的加载
			try {
				URL location = type.getProtectionDomain().getCodeSource().getLocation();
				if (log.isDebugEnabled())
					log.debugf("module class location '%s'", location);
			} catch (NullPointerException e) {
				//Android上无法拿到getProtectionDomain,just pass
			}
			Scans.me().registerLocation(type);
		}
		// 执行扫描
		for (Class<?> type : list) {
			// 扫描子包
			if (scan) {
				scanModuleInPackage(modules, type.getPackage().getName());
			}
			// 仅仅加载自己
			else {
				if (isModule(type)) {
					if (log.isDebugEnabled())
						log.debugf(" > add '%s'", type.getName());
					modules.add(type);
				} else if (log.isTraceEnabled()) {
					log.tracef(" > ignore '%s'", type.getName());
				}
			}
		}
		return modules;
	}

	protected static void scanModuleInPackage(Set<Class<?>> modules, String packageName) {
		if (log.isDebugEnabled())
			log.debugf(" > scan '%s'", packageName);

		List<Class<?>> subs = Scans.me().scanPackage(packageName);
		checkModule(modules, subs);
	}

	/**
	 * @param modules
	 * @param subs
	 */
	private static void checkModule(Set<Class<?>> modules, List<Class<?>> subs) {
		for (Class<?> sub : subs) {
			if (isModule(sub)) {
				if (log.isDebugEnabled())
					log.debugf("   >> add '%s'", sub.getName());
				modules.add(sub);
			} else if (log.isTraceEnabled()) {
				log.tracef("   >> ignore '%s'", sub.getName());
			}
		}
	}

	public static void evalHttpMethod(ActionInfo ai, Method method) {
		if (method.getAnnotation(GET.class) != null)
			ai.getHttpMethods().add("GET");
		if (method.getAnnotation(POST.class) != null)
			ai.getHttpMethods().add("POST");
		if (method.getAnnotation(PUT.class) != null)
			ai.getHttpMethods().add("PUT");
		if (method.getAnnotation(DELETE.class) != null)
			ai.getHttpMethods().add("DELETE");
	}

	public static void evalActionChainMaker(ActionInfo ai, Chain cb) {
		if (null != cb) {
			ai.setChainName(cb.value());
		}
	}

	public static void evalAt(ActionInfo ai, At at, Class<?> clazz) {
		String def = clazz.getSimpleName();
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

	public static void evalAt(ActionInfo ai, At at, String def ,Class<?> clazz) {
		/**
		 * Controller内的为module.ctnName.methodName MainModule为 methodName
		 */
		if (null != at) {
			if (null == at.value() || at.value().length == 0) {
				ai.setPaths(Lang.array("/" + def.toLowerCase()));
			} else {
				ai.setPaths(at.value());
			}
			if (!Strings.isBlank(at.key()))
				ai.setPathKey(at.key());
		} else { // create by wangc
			
			if (isController(clazz)) {// 只处理符合规范的控制器
				At cat = clazz.getAnnotation(At.class);
				if(cat == null){
					String ctnName = getControllerName(clazz);
					String moduleName = getContrllerModule(clazz);
					if (Strings.isEmpty(moduleName))
						ai.setPaths(Lang.array("/" + ctnName +"/" + Strings.lowerFirst(def)));
					else
						ai.setPaths(Lang.array("/"
								+ moduleName.replaceAll("\\.", "/") + "/" + ctnName +"/" + Strings.lowerFirst(def)));
				}else{
					ai.setPaths(Lang.array("/" + Strings.lowerFirst(def)));
				}
			} else {
				if (clazz.getAnnotation(Modules.class) != null) {
					ai.setPaths(Lang.array("/" + Strings.lowerFirst(def)));
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private static void evalPathMap(ActionInfo ai, PathMap pathMap) {
		if (pathMap != null) {
			ai.setPathMap(Json.fromJson(Map.class, pathMap.value()));
		}
	}

	public static void evalFail(ActionInfo ai, Fail fail) {
		if (null != fail) {
			ai.setFailView(fail.value());
		}
	}

	public static void evalOk(ActionInfo ai, Ok ok, Class<?> clazz) {
		if (null != ok) {
			ai.setOkView(ok.value());
		} else { // create by wangc
			if (isController(clazz)) {
				String moduleName = getContrllerModule(clazz);
				String ctnName = getControllerName(clazz);
				if (Strings.isEmpty(moduleName))
					ai.setOkView("jsp:views." + ctnName + ".default");
				else
					ai.setOkView("jsp:views." + moduleName + "." + ctnName
							+ ".default");
			}
		}
	}

	public static void evalOk(ActionInfo ai, Ok ok, String def ,Class<?> clazz) {
		if (null != ok) {
			ai.setOkView(ok.value());
		} else {// create by wangc
			if(isController(clazz)){  
				String moduleName = getContrllerModule(clazz);
				String ctnName = getControllerName(clazz);
				if(Strings.isEmpty(moduleName)){
					ai.setOkView("jsp:views."+ ctnName+"."+Strings.lowerFirst(def));
				}else{
					ai.setOkView("jsp:views."+moduleName+"."+ctnName+"."+Strings.lowerFirst(def));
				}
			}else{
				if(clazz.getAnnotation(Modules.class) != null){
					ai.setOkView("jsp:views."+Strings.lowerFirst(def));
				}
			}
		}
	}

	public static void evalModule(ActionInfo ai, Class<?> type) {
		ai.setModuleType(type);
		String beanName = null;
		// 按照5.10.3章节的说明，优先使用IocBean.name的注解声明bean的名字 Modify By QinerG@gmai.com
		InjectName innm = type.getAnnotation(InjectName.class);
		IocBean iocBean = type.getAnnotation(IocBean.class);
		if (innm == null && iocBean == null) // TODO 再考虑考虑
			if(isController(type)){  // create by wangc
				beanName = type.getName();
			}else
				return ;
		if (iocBean != null) {
			beanName = iocBean.name();
		}
		if (Strings.isBlank(beanName)) {
			if (innm != null && !Strings.isBlank(innm.value())) {
				beanName = innm.value();
			} else {
				beanName = Strings.lowerFirst(type.getSimpleName());
			}
		}
		ai.setInjectName(beanName);
	}

	@SuppressWarnings({"unchecked", "rawtypes" })
	public static void evalActionFilters(ActionInfo ai, Filters filters) {
		if (null != filters) {
			List<ObjectInfo<? extends ActionFilter>> list = new ArrayList<ObjectInfo<? extends ActionFilter>>(filters.value().length);
			for (By by : filters.value()) {
				list.add(new ObjectInfo(by.type(), by.args()));
			}
			ai.setFilterInfos(list.toArray(new ObjectInfo[list.size()]));
		}
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public static void evalHttpAdaptor(ActionInfo ai, AdaptBy ab) {
		if (null != ab) {
			ai.setAdaptorInfo((ObjectInfo<? extends HttpAdaptor>) new ObjectInfo(	ab.type(),
																					ab.args()));
		}
	}

	public static void evalEncoding(ActionInfo ai, Encoding encoding) {
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

	public static boolean isModule(Class<?> classZ) {
		int classModify = classZ.getModifiers();
		if (!Modifier.isPublic(classModify) || Modifier.isAbstract(classModify)
				|| Modifier.isInterface(classModify))
			return false;
		if (classZ.getSimpleName().endsWith("Controller")) {
			// 任何以Controller结尾的都为模块类
			return true;
		}
		for (Method method : classZ.getMethods())
			if (method.isAnnotationPresent(At.class))
				return true;
		return false;
	}

	public static boolean isController(Class<?> clazz) {
		return clazz.getAnnotation(Modules.class) == null
				&& clazz.getSimpleName().endsWith("Controller")
				&& clazz.getSimpleName().length() > "Controller".length();
	}

	public static String getControllerName(Class<?> clazz) {
		String name = clazz.getSimpleName();
		if(name.endsWith("Controller") && "Controller".length() < name.length())
			return Strings.lowerFirst(name.substring(0, name.length()
				- "Controller".length()));
		else{
			return Strings.lowerFirst(name);
		}
	}

	public static String getContrllerModule(Class<?> clazz) {
		String fullName = clazz.getName();
		String pkgName = clazz.getPackage().getName();
		int start = fullName.indexOf("controllers.");
		if (start == 0) {
			if ("controllers".equals(pkgName)) {
				return "";
			} else {
				return pkgName.substring("controllers.".length());
			}
		} else if (start > 0) {
			if (start + "controllers".length() == pkgName.length()) {
				return "";
			} else {
				return pkgName.substring(start + "controllers.".length());
			}
		} else {// 不符合规范 ,不提供闭包支持
			return "";
		}
	}
}
