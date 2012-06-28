package org.nutz.mvc.adaptor.extractor;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.nutz.mvc.adaptor.ParamExtractor;

/**
 * 默认提取器
 * 
 * @author juqkai(juqkai@gmail.com)
 * 
 */
public class BaseParamExtractor implements ParamExtractor {
    private HttpServletRequest req;

    public BaseParamExtractor(HttpServletRequest req) {
        this.req = req;
    }

    public String[] extractor(String name) {
        return req.getParameterValues(name);
    }

    @SuppressWarnings("unchecked")
    public Set<String> keys() {
        return req.getParameterMap().keySet();
    }

}
