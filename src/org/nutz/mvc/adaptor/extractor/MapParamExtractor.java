package org.nutz.mvc.adaptor.extractor;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.nutz.mvc.adaptor.ParamExtractor;

/**
 * refer为map时的提取器
 * 
 * @author juqkai(juqkai@gmail.com)
 * 
 */
public class MapParamExtractor implements ParamExtractor {
    private HttpServletRequest req;
    private Map<String, Object> map;

    public MapParamExtractor(HttpServletRequest req, Map<String, Object> refer) {
        this.req = req;
        this.map = refer;
    }

    public String[] extractor(String name) {
        if (null != map && map.containsKey(name)) {
            Object obj = map.get(name);
            if (obj instanceof String[])
                return (String[]) obj;
            if (obj == null)
                return null;
            return new String[]{obj.toString()};
        }
        return req.getParameterValues(name);
    }

    @SuppressWarnings("unchecked")
    public Set<String> keys() {
        Set<String> ss = new HashSet<String>();
        ss.addAll(map.keySet());
        ss.addAll(req.getParameterMap().keySet());
        return ss;
    }

}
