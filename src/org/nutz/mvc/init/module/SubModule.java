package org.nutz.mvc.init.module;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.nutz.ioc.Ioc;
import org.nutz.ioc.annotation.InjectName;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.util.Context;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.ActionInvoker;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.init.PathNode;
import org.nutz.mvc.invoker.ActionInvokerImpl2;

/**
 * 子模块
 * 
 * @author juqkai(juqkai@gmail.com)
 * 
 */
public class SubModule extends ModuleTree<Class<?>> {
	private static final Log log = Logs.getLog(SubModule.class);
	
	private At at;
	private String moduleName;
	private Object moduleObj;
	public SubModule(Context context, Class<?> module, ModuleTree<?> father,
			Ioc ioc) {
		super(context, module, father, ioc);
		init();
	}
	private void init(){
		evalModule();
		this.at = module.getAnnotation(At.class);
	}

	public void scan() {
		for (Method method : module.getMethods()) {
			if (!Modifier.isPublic(method.getModifiers())
					|| !method.isAnnotationPresent(At.class))
				continue;
			addChild(new MethodModule(context, method, this, ioc));
		}
	}

	protected String[] getPath() {
		// get base url
		String[] bases;
		if (null == at)
			bases = Lang.array("");
		else if (null == at.value() || at.value().length == 0)
			bases = Lang.array("/" + module.getSimpleName().toLowerCase());
		else {
			bases = at.value();
			for (int i = 0; i < bases.length; i++)
				if (bases[i] == null || "/".equals(bases[i]))
					bases[i] = "";
		}
		return bases;
	}

	public void parse(PathNode<ActionInvoker> root) throws Throwable {
		for (ModuleTree<?> mt : child) {
			mt.parse(root);
		}
	}

	/**
	 * 解析注入名称
	 */
	private void evalModule() {
		InjectName name = module.getAnnotation(InjectName.class);
		if (null != name)
			if (Strings.isBlank(name.value()))
				this.moduleName = Strings.lowerFirst(module.getSimpleName());
			else
				this.moduleName = name.value();
		else
			try {
				moduleObj = module.newInstance();
			} catch (Exception e) {
				if (log.isWarnEnabled())
					log.warn(getExceptionMessage(e), e);
				throw Lang
						.makeThrow(
								"Class '%s' should has a accessible default constructor : '%s'",
								module.getName(), e.getMessage());
			}
	}

	protected void initInvoker(ActionInvokerImpl2 invoker) throws Throwable {
		invoker.module = moduleObj;
		invoker.moduleName = moduleName;
		invoker.moduleType = module;
		father.initInvoker(invoker);
	}

}
