package org.nutz.lang.eject;

import java.util.Map;

public class EjectFromMap implements Ejecting {

    private String key;

    public EjectFromMap(String key) {
        this.key = key;
    }

    public Object eject(Object obj) {
        return null == obj ? null : ((Map<?, ?>) obj).get(key);
    }

}
