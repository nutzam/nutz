package org.nutz.mvc.impl.reqmatcher;

import java.util.HashMap;
import java.util.Map;

import org.nutz.mvc.ActionChain;
import org.nutz.mvc.ActionContext;
import org.nutz.mvc.ActionInfo;
import org.nutz.mvc.RequestMatcher;
import org.nutz.mvc.annotation.ApiVersion;

public class ApiVersionRequestMatcher implements RequestMatcher {
    
    protected int index = -1;
    protected Map<String, DefaultRequestMatcher> matchers;
    protected boolean keepPathArg;

    public void add(String path, ActionInfo ai, ActionChain chain) {
        ApiVersion panno = ai.getModuleType().getAnnotation(ApiVersion.class);
        if (panno != null) {
            ApiVersion anno = ai.getMethod().getAnnotation(ApiVersion.class);
            if (anno == null)
                anno = panno;
            String[] namedPathArgs = ai.getNamedPathArgs();
            if (namedPathArgs != null) {
                for (int i = 0; i < namedPathArgs.length; i++) {
                    if ("version".equals(namedPathArgs[i])) {
                        this.index = i;
                        if (matchers == null)
                            matchers = new HashMap<String, DefaultRequestMatcher>();
                        DefaultRequestMatcher matcher = matchers.get(anno.value());
                        if (matcher == null) {
                            matcher = new DefaultRequestMatcher();
                            matchers.put(anno.value(), matcher);
                        }
                        matcher.add(path, ai, chain);
                        if (anno.keepPathArg())
                            keepPathArg = true;
                        return; // 直接搞定
                    }
                }
            }
        }
    }

    public ActionChain match(ActionContext ctx) {
        if (matchers == null || ctx.getPathArgs().size() <= index)
            return null;
        String version = ctx.getPathArgs().get(index);
        if (version == null)
            return null;
        DefaultRequestMatcher matcher = matchers.get(version);
        if (matcher != null) {
            ActionChain chain = matcher.match(ctx);
            if (chain != null) {
                if (!keepPathArg)
                    ctx.getPathArgs().remove(index);
                return chain;
            }
        }
        return null;
    }

}
