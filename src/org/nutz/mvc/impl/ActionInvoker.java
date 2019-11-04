package org.nutz.mvc.impl;

import java.util.ArrayList;
import java.util.List;

import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.ActionChain;
import org.nutz.mvc.ActionContext;
import org.nutz.mvc.ActionInfo;
import org.nutz.mvc.RequestMatcher;
import org.nutz.mvc.impl.reqmatcher.ApiVersionRequestMatcher;
import org.nutz.mvc.impl.reqmatcher.DefaultRequestMatcher;

/**
 * 根据 HTTP 请求的方法 (GET|POST|PUT|DELETE) 来调用响应的动作链
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * @author wendal(wendal1985@gmail.com)
 */
public class ActionInvoker {

    private static final Log log = Logs.get();

    protected List<RequestMatcher> matchers;

    protected DefaultRequestMatcher dft = new DefaultRequestMatcher();

    public ActionInvoker() {
        matchers = new ArrayList<RequestMatcher>(2);
        matchers.add(new ApiVersionRequestMatcher());
        matchers.add(dft);
    }

    public void add(String path, ActionInfo ai, ActionChain chain) {
        for (RequestMatcher matcher : matchers) {
            matcher.add(path, ai, chain);
        }
    }

    public ActionChain getActionChain(ActionContext ac) {
        for (RequestMatcher matcher : matchers) {
            ActionChain chain = matcher.match(ac);
            if (chain != null) {
                ac.set("nutz.mvc.current.chain", chain);
                return chain;
            }
        }
        if (log.isDebugEnabled())
            log.debugf("Not chain for req (path=%s, method=%s)", ac.getPath(), ac.getRequest().getMethod());
        return null;
    }

    public boolean invoke(ActionContext ac) {
        ActionChain chain = (ActionChain) ac.remove("nutz.mvc.current.chain");
        chain.doChain(ac);
        return ac.getBoolean(ActionContext.AC_DONE, true);
    }

    // ---------------------------------------------------------------------------
    // 为了兼容老的ActionInvoker
    public void addChain(String httpMethod, ActionChain chain) {
        dft.setDefaultChain(chain);
    }

    public void setDefaultChain(ActionChain defaultChain) {
        dft.setDefaultChain(defaultChain);
    }

    // ---------------------------------------------------------------------------
    // 预留2个方法吧
    public void setMatchers(List<RequestMatcher> matchers) {
        this.matchers = matchers;
    }

    public List<RequestMatcher> getMatchers() {
        return matchers;
    }
}
