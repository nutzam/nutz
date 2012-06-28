package org.nutz.plugin;

import java.util.ArrayList;
import java.util.List;

import org.nutz.ioc.Ioc;
import org.nutz.ioc.IocException;

/**
 * 从 Ioc 容器中选取可用插件
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class IocPluginManager<T> implements PluginManager<T> {

    private Ioc ioc;
    private String[] names;

    public IocPluginManager(Ioc ioc, String... names) {
        this.ioc = ioc;
        this.names = names;
    }

    @SuppressWarnings("unchecked")
    public T get() throws NoPluginCanWorkException {
        for (String name : names) {
            try {
                Plugin plugin = ioc.get(Plugin.class, name);
                if (plugin.canWork())
                    return (T) plugin;
            }
            catch (IocException e) {}
        }
        throw new NoPluginCanWorkException();
    }

    @SuppressWarnings("unchecked")
    public List<T> gets() {
        List<T> aList = new ArrayList<T>(names.length);
        for (String name : names) {
            try {
                Plugin plugin = ioc.get(Plugin.class, name);
                if (plugin.canWork())
                    aList.add((T)plugin);
            }
            catch (IocException e) {}
        }
        return aList;
    }
}
