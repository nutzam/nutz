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

	public SimplePluginManager(String... classNames) throws InstantiationException,
			IllegalAccessException {
		for (String className : classNames)
			loadPlugin(className);
	}

	public SimplePluginManager(Class<? extends T>... classNames) throws InstantiationException,
			IllegalAccessException {
		for (Class<? extends T> pluginClass : classNames)
			loadPlugin(pluginClass);
	}

	@SuppressWarnings("unchecked")
	public T get() {
		for (Plugin plugin : list)
			if (plugin.canWork())
				return (T) plugin;
		return null;
	}

	protected void loadPlugin(Class<? extends T> pluginClass) throws InstantiationException,
			IllegalAccessException {
		if (pluginClass != null)
			list.add((Plugin) pluginClass.newInstance());
	}

	@SuppressWarnings("unchecked")
	private void loadPlugin(String pluginClassName) throws InstantiationException,
			IllegalAccessException {
		try {
			if (pluginClassName != null)
				loadPlugin((Class<? extends T>) Class.forName(pluginClassName));
		} catch (ClassNotFoundException e) {}
	}
}
