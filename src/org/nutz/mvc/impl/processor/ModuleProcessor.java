package org.nutz.mvc.impl.processor;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.nutz.ioc.Ioc;
import org.nutz.ioc.Ioc2;
import org.nutz.ioc.IocContext;
import org.nutz.ioc.impl.ComboContext;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.ActionContext;
import org.nutz.mvc.ActionInfo;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.NutSessionListener;
import org.nutz.mvc.ioc.RequestIocContext;
import org.nutz.mvc.ioc.SessionIocContext;

/**
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * @author wendal(wendal1985@gmail.com)
 * 
 */
public class ModuleProcessor extends AbstractProcessor {

    private static final Log log = Logs.get();

    private String injectName;

    private Class<?> moduleType;
    private Method method;
    private Object moduleObj;

    private static Map<String, Object> modulesMap = new HashMap<String, Object>();

    @Override
    public void init(NutConfig config, ActionInfo ai) throws Throwable {
        method = ai.getMethod();
        moduleType = ai.getModuleType();
        // 直接指定了对象
        if (ai.getModuleObj() != null) {
        	moduleObj = ai.getModuleObj();
        }
        // 不使用 Ioc 容器管理模块
        else if (Strings.isBlank(ai.getInjectName())) {
            // change in 1.b.49
            // 同一个类的入口方法,共用同一个实例
            synchronized (modulesMap) {
                String className = moduleType.getName();
                moduleObj = modulesMap.get(className);
                if (moduleObj == null) {
                    if (log.isInfoEnabled())
                        log.info("Create Module obj without Ioc --> "
                                 + moduleType);
                    moduleObj = Mirror.me(moduleType).born();
                    modulesMap.put(className, moduleObj);
                }
            }
        }
        // 使用 Ioc 容器管理模块
        else {
            Ioc ioc = config.getIoc();
            if (null == ioc)
                throw Lang.makeThrow("Moudle with @InjectName('%s') or @IocBean('%s') but you not declare a Ioc for this app!! Miss @IocBy at MainMdoule??",
                                     injectName,
                                     injectName);
            injectName = ai.getInjectName();
            if (!ioc.has(injectName)) {
                log.warnf("Moudle with @InjectName('%s') or @IocBean('%s') but no such ioc bean found!! Pls check your ioc configure!!",
                          injectName,
                          injectName);
            }
        }
    }

    public void process(ActionContext ac) throws Throwable {
        RequestIocContext reqContext = null;
        try {
            if (null != moduleObj) {
                ac.setModule(moduleObj);
            } else {
                Ioc ioc = ac.getIoc();
                Object obj;
                /*
                 * 如果 Ioc 容器实现了高级接口，那么会为当前请求设置上下文对象
                 */
                if (NutSessionListener.isSessionScopeEnable
                    && ioc instanceof Ioc2) {
                    reqContext = new RequestIocContext(ac.getRequest());
                    HttpSession sess = Mvcs.getHttpSession(false);
                    IocContext myContext = null;
                    // 如果容器可以创建 Session ...
                    if (null != sess) {
                        SessionIocContext sessionContext = new SessionIocContext(sess);
                        myContext = new ComboContext(reqContext, sessionContext);
                    }
                    // 如果容器禁止了 Session ...
                    else {
                        myContext = reqContext;
                    }
                    Mvcs.setIocContext(myContext);
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
            // if (log.isDebugEnabled()) //打印实际执行的Method信息
            // log.debugf("Handle URL[%s] by Method[%s]",ac.getPath(),method);
            doNext(ac);
        }
        finally {
            if (reqContext != null)
                try {
                    reqContext.depose();
                }
                catch (Throwable e) {
                    if (log.isDebugEnabled())
                        log.debug("ReqContext depose fail?!", e);
                }
        }
    }

}
