package org.nutz.mvc.impl.processor;

import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.ActionContext;
import org.nutz.mvc.ActionInfo;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.Processor;

/**
 * 这是NutShiroProcessor的影子处理器,如果存在该插件,就加载之
 * @author wendal(wendal1985@gmail.com)
 *
 */
public class NutShiroShadowProcessor extends AbstractProcessor {
    
    Processor proxy;
    static Class<?> clazz;
    protected static boolean disable = false;
    private static final Log log = Logs.get();
    
    public static void disable() {
        disable = true;
    }
    
    static {
        try {
            clazz = Class.forName("org.nutz.integration.shiro.NutShiroProcessor");
            log.info("nutz-integration-shiro found");
        }
        catch (ClassNotFoundException e) {
        }
    }

    public NutShiroShadowProcessor() throws InstantiationException, IllegalAccessException {
        if (clazz != null)
            proxy = (Processor) clazz.newInstance();
    }
    
    public void init(NutConfig config, ActionInfo ai) throws Throwable {
        if (proxy != null)
            proxy.init(config, ai);
    }

    public void process(ActionContext ac) throws Throwable {
        if (!disable && proxy != null)
            proxy.process(ac);
        else
            doNext(ac);
    }

}
