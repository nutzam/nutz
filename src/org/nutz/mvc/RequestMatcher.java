package org.nutz.mvc;

public interface RequestMatcher {

    void add(String path, ActionInfo ai, ActionChain chain);
    
    ActionChain match(ActionContext ctx);
}
