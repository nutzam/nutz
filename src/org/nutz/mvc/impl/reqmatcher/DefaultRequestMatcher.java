package org.nutz.mvc.impl.reqmatcher;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.ActionChain;
import org.nutz.mvc.ActionContext;
import org.nutz.mvc.ActionInfo;
import org.nutz.mvc.RequestMatcher;

public class DefaultRequestMatcher implements RequestMatcher {
    
    private static final Log log = Logs.get();
    
    protected Map<String, ActionChain> chainMap;
    
    protected ActionChain defaultChain;

    public void add(String path, ActionInfo ai, ActionChain chain) {
        if (ai.isForSpecialHttpMethod()) {
            for (String httpMethod : ai.getHttpMethods()) {
                if (chainMap == null)
                    chainMap = new HashMap<String, ActionChain>();
                chainMap.put(httpMethod, chain);
            }
        }
        else {
            defaultChain = chain;
        }
    }

    public ActionChain match(ActionContext ac) {
        String httpMethod = "";
        if (chainMap != null && !chainMap.isEmpty()) {
            HttpServletRequest req = ac.getRequest();
            httpMethod = Strings.sNull(req.getMethod(), "GET").toUpperCase();
            ActionChain chain = chainMap.get(httpMethod);
            // 找到了特殊HTTP方法的处理动作链
            if (null != chain) {
                return chain;
            }
        }
        // 这个 URL 所有的HTTP方法用统一的动作链处理
        if (null != defaultChain) {
            return defaultChain;
        }
        if (chainMap != null && chainMap.size() != 0 && log.isDebugEnabled()) {
            log.debugf("Path=[%s] available methods%s but request [%s], using the wrong http method?", ac.getPath(), chainMap.keySet(), httpMethod);
        }
        // 否则将认为不能处理
        return null;
    }

    public void addChain(String httpMethod, ActionChain chain) {
        if (chainMap == null)
            chainMap = new HashMap<String, ActionChain>();
        chainMap.put(httpMethod, chain);
    }

    public void setDefaultChain(ActionChain defaultChain) {
        this.defaultChain = defaultChain;
    }
}
