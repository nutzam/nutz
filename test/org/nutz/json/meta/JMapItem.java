package org.nutz.json.meta;

import java.util.List;
import java.util.Map;

public class JMapItem {

    private Map<String, Class<? extends JA>> map;

    private List<Class<? extends JA>> list;

    public Map<String, Class<? extends JA>> getMap() {
        return map;
    }

    public void setMap(Map<String, Class<? extends JA>> map) {
        this.map = map;
    }

    public List<Class<? extends JA>> getList() {
        return list;
    }

    public void setList(List<Class<? extends JA>> list) {
        this.list = list;
    }

}
