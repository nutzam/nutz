package org.nutz.mvc.impl.processor;

import java.lang.reflect.Method;

import org.nutz.ioc.Ioc;
import org.nutz.ioc.Ioc2;
import org.nutz.ioc.IocContext;
import org.nutz.ioc.impl.ComboContext;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.mvc.ActionContext;
import org.nutz.mvc.ActionInfo;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.ioc.RequestIocContext;
import org.nutz.mvc.ioc.SessionIocContext;

/**
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * @author wendal(wendal1985@gmail.com)
 *
 */
public class ModuleProcessor extends AbstractProcessor {
	
	private String injectName;
	
	private Class<?> moduleType;
	private Method method;
	
	@Override
	public void init(NutConfig config, ActionInfo ai) throws Throwable {
		method = ai.getMethod();
		moduleType = ai.getModuleType();
		if (!Strings.isBlank(ai.getInjectName()))
			injectName = ai.getInjectName();
	}

	public void process(ActionContext ac) throws Throwable {
		RequestIocContext reqContext = null;
		try {
			if (null == injectName) {
				ac.setModule(moduleType.newInstance());
			} else {
				Ioc ioc = ac.getIoc();
				if (null == ioc)
					throw Lang.makeThrow(	"Moudle with @InjectName('%s') but you not declare a Ioc for this app",
							injectName);
				Object obj;
				/*
				 * 如果 Ioc 容器实现了高级接口，那么会为当前请求设置上下文对象
				 */
				if (ioc instanceof Ioc2) {
					reqContext = new RequestIocContext(ac.getRequest());
					SessionIocContext sessionContext = new SessionIocContext(ac.getRequest().getSession());
					IocContext myContext = new ComboContext(reqContext, sessionContext);
					obj = ((Ioc2) ioc).get(moduleType, injectName, myContext);
				}
				/*
				 * 否则，则仅仅简单的从容器获取
				 */
				else
					obj = ioc.get(moduleType, injectName);
				ac.setModule(obj);

			}
			ac.setMethod(method);
			doNext(ac);
		} finally {
			if (reqContext != null)
				try {
					reqContext.depose();
				} catch (Throwable e) {
					e.printStackTrace();
				}
		}
	}
	
}
