package org.nutz.lang.util;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nutz.castor.Castors;
import org.nutz.lang.Mirror;

public abstract class AbstractContext implements Context {

    public AbstractContext() {
        super();
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public Object get(String name, Object dft) {
        Object obj = get(name);
        if (null == obj) {
            return dft;
        }
        return obj;
    }

    @Override
    public <T> T getAs(Class<T> type, String name) {
        return Castors.me().castTo(get(name), type);
    }

    @Override
    public <T> T getAs(Class<T> type, String name, T dft) {
        Object obj = get(name);
        if (null == obj) {
            return dft;
        }
        return Castors.me().castTo(obj, type);
    }

    @Override
    public int getInt(String name) {
        return getInt(name, -1);
    }

    @Override
    public String getString(String name) {
        return getString(name, null);
    }

    @Override
    public boolean getBoolean(String name) {
        return getBoolean(name, false);
    }

    @Override
    public float getFloat(String name) {
        return getFloat(name, 0.0f);
    }

    @Override
    public double getDouble(String name) {
        return getDouble(name, 0.0);
    }

    @Override
    public double getDouble(String name, double dft) {
        Object obj = get(name);
        if (null == obj) {
            return dft;
        }
        return Double.parseDouble(obj.toString());
    }

    @Override
    public int getInt(String name, int dft) {
        Object obj = get(name);
        if (null == obj) {
            return dft;
        }
        return Integer.parseInt(obj.toString());
    }

    @Override
    public String getString(String name, String dft) {
        Object obj = get(name);
        if (null == obj) {
            return dft;
        }
        return obj.toString();
    }

    @Override
    public boolean getBoolean(String name, boolean dft) {
        Object obj = get(name);
        if (null == obj) {
            return dft;
        }
        return Boolean.parseBoolean(obj.toString());
    }

    @Override
    public float getFloat(String name, float dft) {
        Object obj = get(name);
        if (null == obj) {
            return dft;
        }
        return Float.parseFloat(obj.toString());
    }

    @Override
    public Context putAll(Object obj) {
        return putAll(null, obj);
    }

    @Override
    public Context putAll(String prefix, Object obj) {
        if (null != obj) {
            // Context
            if (obj instanceof Context) {
                Context ctx = (Context) obj;
                for (String key : ctx.keys()) {
                    if (null == prefix) {
                        this.set(key, ctx.get(key));
                    } else {
                        this.set(prefix + key, ctx.get(key));
                    }
                }
            }
            // Map
            else if (obj instanceof Map<?, ?>) {
                for (Map.Entry<?, ?> en : ((Map<?, ?>) obj).entrySet()) {
                    Object oKey = en.getKey();
                    if (null == oKey) {
                        continue;
                    }
                    String key = oKey.toString();
                    if (null != prefix) {
                        key = prefix + key;
                    }
                    this.set(key.toString(), en.getValue());
                }
            }
            // 普通 Java 对象
            else {
                Mirror<?> mirror = Mirror.me(obj);
                // 需要被忽略的 Java 对象
                if (mirror.getType().isArray()
                    || mirror.isNumber()
                    || mirror.isBoolean()
                    || mirror.isChar()
                    || mirror.isStringLike()
                    || mirror.isDateTimeLike()
                    || Collection.class.isAssignableFrom(mirror.getType())) {}
                // 普通 Java 对象，应该取其每个字段
                else {
                    for (Field field : mirror.getFields()) {
                        String key = field.getName();
                        if (null != prefix) {
                            key = prefix + key;
                        }
                        this.set(key, mirror.getValue(obj, field));
                    }
                }
            }
        }
        return this;
    }

    @Override
    public Map<String, Object> getInnerMap() {
        Map<String, Object> map = new HashMap<String, Object>();
        for (String key : this.keys()) {
            map.put(key, this.get(key));
        }
        return map;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> getMap(String name) {
        return getAs(Map.class, name);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Object> getList(String name) {
        return getAs(List.class, name);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> List<T> getList(Class<T> classOfT, String name) {
        return (List<T>) getList(name);
    }

    @Override
    public abstract AbstractContext clone();

}