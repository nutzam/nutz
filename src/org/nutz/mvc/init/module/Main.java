package org.nutz.mvc.init.module;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.nutz.ioc.Ioc;
import org.nutz.lang.util.Context;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.ActionInvoker;
import org.nutz.mvc.ViewMaker;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Modules;
import org.nutz.mvc.annotation.Views;
import org.nutz.mvc.init.PathNode;
import org.nutz.mvc.invoker.ActionInvokerImpl2;
import org.nutz.mvc.view.DefaultViewMaker;
import org.nutz.resource.Scans;

/**
 * 主模块
 * @author juqkai(juqkai@gmail.com)
 *
 */
public class Main extends ModuleTree<Class<?>>{
	public Main(Context context, Class<?> module, ModuleTree<?> father, Ioc ioc) {
		super(context, module, father, ioc);
	}
	private static final Log log = Logs.getLog(Main.class);

	public void parse(PathNode<ActionInvoker> root) throws Throwable {
		for(ModuleTree<?> mt : child){
			mt.parse(root);
		}
	}

	public void scan() {
		// Then try to load sub-modules
		AnnotatedElement ae = (AnnotatedElement) module;
		Modules modules = ae.getAnnotation(Modules.class);
		Class<?>[] moduleRefers;
		if (null == modules || null == modules.value() || modules.value().length == 0)
			moduleRefers = new Class<?>[]{(Class<?>)module};
		else
			moduleRefers = modules.value();

		// 扫描所有的
		boolean isNeedScanSubPackages = null == modules ? false : modules.scanPackage();
		for (Class<?> module : moduleRefers) {
			// 扫描这个类同包，以及所有子包的类
			if (isNeedScanSubPackages) {
				if (log.isDebugEnabled())
					log.debugf(" > scan '%s'", module.getPackage().getName());
				List<Class<?>> subs = Scans.me().scanPackage(module);
				for (Class<?> sub : subs) {
					if (isModule(sub)) {
						if (log.isDebugEnabled())
							log.debugf("   >> add '%s'", sub.getName());
						addChild(new SubModule(context, sub, this, ioc));
//						moduleSet.add(sub);
					} else if (log.isTraceEnabled()) {
						log.tracef("   >> ignore '%s'", sub.getName());
					}
				}
			}
			// 仅仅加载自己
			else {
				if (isModule(module)) {
					if (log.isDebugEnabled())
						log.debugf(" > add '%s'", module.getName());
//					moduleSet.add(module);
					addChild(new SubModule(context, module, this, ioc));
				} else if (log.isTraceEnabled()) {
					log.tracef(" > ignore '%s'", module.getName());
				}
			}
		}
	}
	private static boolean isModule(Class<?> classZ) {
		for (Method method : classZ.getMethods())
			if (method.isAnnotationPresent(At.class))
				return true;
		return false;
	}
	
	public List<ViewMaker> fetchMakers() throws Throwable{
		Views vms = module.getAnnotation(Views.class);
		// Prepare view makers
		List<ViewMaker> makers = new ArrayList<ViewMaker>();
		if (null != vms)
			for (Class<? extends ViewMaker> type : vms.value())
				makers.add(type.newInstance());
		makers.add(new DefaultViewMaker());// 优先使用用户自定义
		return makers;
	}
	protected void initInvoker(ActionInvokerImpl2 invoker) throws Throwable{}
	protected String[] getPath(){
		return new String[]{};
	}
}
