package org.nutz.lang.tmpl;

import java.util.HashMap;
import java.util.Map;

import org.nutz.lang.Strings;
import org.nutz.lang.meta.Pair;

public class StrMappingConvertor implements StrEleConvertor {

    private Map<String, String> mapping;

    StrMappingConvertor(String input) {
        mapping = new HashMap<String, String>();
        String[] ss = Strings.split(input, false, ',');
        for (String s : ss) {
            Pair<String> p = Pair.create(s);
            mapping.put(p.getName(), p.getValue());
        }
    }

    @Override
    public String process(String str) {
        return Strings.sNull(mapping.get(str), str);
    }

}
