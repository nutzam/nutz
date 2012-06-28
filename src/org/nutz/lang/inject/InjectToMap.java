package org.nutz.lang.inject;

import java.util.Map;

public class InjectToMap implements Injecting {

    private String key;

    public InjectToMap(String key) {
        this.key = key;
    }

    @SuppressWarnings("unchecked")
    public void inject(Object obj, Object value) {
        ((Map<String, Object>) obj).put(key, value);
    }

}
