package org.nutz.plugin;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Wendal(wendal1985@gmail.com)
 * @author zozoh(zozohtnt@gmail.com)
 */
public class SimplePluginManager<T> implements PluginManager<T> {

	private List<Plugin> list = new ArrayList<Plugin>();

	public SimplePluginManager(String... classNames) throws PluginException {
		for (String className : classNames)
			loadPlugin(className);
	}

	public SimplePluginManager(Class<? extends T>... classNames) throws PluginException {
		for (Class<? extends T> pluginClass : classNames)
			loadPlugin(pluginClass);
	}

	@SuppressWarnings("unchecked")
	public T get() throws NoPluginCanWorkException {
		for (Plugin plugin : list)
			if (plugin.canWork())
				return (T) plugin;
		throw new NoPluginCanWorkException();
	}

	protected void loadPlugin(Class<? extends T> pluginClass) throws PluginException {
		if (pluginClass != null)
			try {
				list.add((Plugin) pluginClass.newInstance());
			} catch (Exception e) {
				throw new PluginException(pluginClass.getName(), e);
			}
	}

	@SuppressWarnings("unchecked")
	private void loadPlugin(String pluginClassName) throws PluginException {
		try {
			if (pluginClassName != null)
				loadPlugin((Class<? extends T>) Class.forName(pluginClassName));
		} catch (ClassNotFoundException e) {}
	}
}
