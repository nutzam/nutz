package org.nutz.ioc.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.util.MultiLineProperties;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.resource.NutResource;
import org.nutz.resource.Scans;

/**
 * 代理Properties文件,以便直接在Ioc配置文件中使用
 * 
 * @author wendal(wendal1985@gmail.com)
 * @author zozoh(zozohtnt@gmail.com)
 * 
 * @since 1.b.37
 */
public class PropertiesProxy {

    private static final Log log = Logs.get();

    // 是否为UTF8格式的Properties文件
    private final boolean utf8;
    // 是否忽略无法加载的文件
    private boolean ignoreResourceNotFound = false;

    private MultiLineProperties mp = new MultiLineProperties();

    public PropertiesProxy() {
        this(true);
    }

    public PropertiesProxy(boolean utf8) {
        this.utf8 = utf8;
    }

    public PropertiesProxy(String... paths) {
        this(true);
        this.setPaths(paths);
    }

    public PropertiesProxy(InputStream in) {
        this(true);
        try {
            mp = new MultiLineProperties();
            mp.load(new InputStreamReader(in));
        }
        catch (IOException e) {
            throw Lang.wrapThrow(e);
        }
    }

    /**
     * 加载指定文件/文件夹的Properties文件,合并成一个Properties对象
     * <p>
     * <b style=color:red>如果有重复的key,请务必注意加载的顺序!!<b/>
     * 
     * @param paths
     *            需要加载的Properties文件路径
     */
    public void setPaths(String... paths) {
        mp = new MultiLineProperties();

        try {
            List<NutResource> list = getResources(paths);
            if (utf8)
                for (NutResource nr : list)
                    mp.load(nr.getReader(), false);
            else {
                Properties p = new Properties();
                for (NutResource nr : list) {
                    p.load(nr.getInputStream());
                }
                mp.putAll(p);
            }
        }
        catch (IOException e) {
            throw Lang.wrapThrow(e);
        }
    }

    /**
     * 加载指定文件/文件夹的Properties文件
     * 
     * @param paths
     *            需要加载的Properties文件路径
     * @return 加载到的Properties文件Resource列表
     */
    private List<NutResource> getResources(String... paths) {
        List<NutResource> list = new ArrayList<NutResource>();
        for (String path : paths) {
            try {
                List<NutResource> resources = Scans.me().loadResource("^.+[.]properties$", path);
                list.addAll(resources);
            }
            catch (Exception e) {
                if (ignoreResourceNotFound) {
                    if (log.isWarnEnabled()) {
                        log.warn("Could not load resource from " + path + ": " + e.getMessage());
                    }
                } else {
                    throw Lang.wrapThrow(e);
                }
            }
        }
        return list;
    }

    public void setIgnoreResourceNotFound(boolean ignoreResourceNotFound) {
        this.ignoreResourceNotFound = ignoreResourceNotFound;
    }

    public void put(String key, String value) {
        mp.put(key, value);
    }

    public String get(String key) {
        return mp.get(key);
    }

    public String get(String key, String defaultValue) {
        return Strings.sNull(mp.get(key), defaultValue);
    }

    public int getInt(String key) {
        return getInt(key, -1);
    }

    public int getInt(String key, int defaultValue) {
        try {
            return Integer.parseInt(get(key));
        }
        catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public long getLong(String key) {
        return getLong(key, -1);
    }

    public long getLong(String key, long dfval) {
        try {
            return Long.parseLong(get(key));
        }
        catch (NumberFormatException e) {
            return dfval;
        }
    }

    public String getTrim(String key) {
        return Strings.trim(get(key));
    }

    public String getTrim(String key, String defaultValue) {
        return Strings.trim(get(key, defaultValue));
    }

    public List<String> getKeys() {
        return mp.keys();
    }

    public Collection<String> getValues() {
        return mp.values();
    }

    public Properties toProperties() {
        Properties p = new Properties();
        for (String key : mp.keySet()) {
            p.put(key, mp.get(key));
        }
        return p;
    }
}
