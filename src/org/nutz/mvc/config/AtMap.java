package org.nutz.mvc.config;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.nutz.lang.Strings;
import org.nutz.lang.meta.Pair;

public class AtMap {

    private Map<String, String> ats;

    private Map<String, Method> methods;

    public AtMap() {
        ats = new HashMap<String, String>();
        methods = new HashMap<String, Method>();
    }

    public void add(String key, String actionPath) {
        if (actionPath.endsWith("/*"))
            actionPath = actionPath.substring(0, actionPath.length() - 2);
        ats.put(Strings.trim(key), Strings.trim(actionPath));
    }

    public void addMethod(String url, Method method) {
        methods.put(url, method);
    }

    public Set<String> keys() {
        return ats.keySet();
    }

    public Map<String, Method> getMethodMapping() {
        return methods;
    }

    public int size() {
        return ats.size();
    }

    public String get(String key) {
        return ats.get(key);
    }

    public AtMap clear() {
        ats.clear();
        return this;
    }

    public List<Pair<String>> getAll() {
        return getList((String[]) null);
    }

    public List<Pair<String>> getList(String... prefixes) {
        List<Pair<String>> list = new ArrayList<Pair<String>>(ats.size());
        Set<Entry<String, String>> ens = ats.entrySet();
        for (Entry<String, String> en : ens) {
            String key = en.getKey();
            if (null == prefixes || prefixes.length == 0)
                list.add(new Pair<String>(key, en.getValue()));
            else {
                for (String prefix : prefixes)
                    if (key.startsWith(prefix)) {
                        list.add(new Pair<String>(key, en.getValue()));
                        break;
                    }
            }
        }
        return list;
    }

}
