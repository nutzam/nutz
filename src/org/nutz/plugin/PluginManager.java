package org.nutz.plugin;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nutz.json.Json;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;

/**
 * TODO zzh: 仍然觉得这个实现思路有点问题，还需要仔细想想...
 * <p>
 * 记录一些想法：
 * <ul>
 * <li>但是我们不应该规定插件的配置方式，使用者 new 这个类时来决定
 * <li>插件的配置方式，可以是一个 Ioc 容器，一个数组，一段字符串，都可以
 * <li>我觉得没有必要用单例。比如 Nutz.Log 部分 new 了一个 PluginManager，其他的地方 也可 new 一个
 * PluginManger，它们管理的插件不一样。
 * <li>复杂的插件体系，人家早用 OSGI 了，用这个，无非就是想部署时才决定用什么实现
 * </ul>
 * 
 * 
 * @author Wendal(wendal1985@gmail.com)
 * @author zozoh(zozohtnt@gmail.com)
 */
public final class PluginManager {

	private Map<Class<?>, List<Plugin>> plugins = new HashMap<Class<?>, List<Plugin>>();

	public static final String DEFAULT_PLUGIN_FILENAME = "nutz-default-plugin.json";
	public static final String EXT_PLUGIN_FILENAME = "nutz-plugin.json";

	private static PluginManager management = new PluginManager();

	private PluginManager() {
		loadPluginInfo(EXT_PLUGIN_FILENAME);
		loadPluginInfo(DEFAULT_PLUGIN_FILENAME);
	}

	public static <T> T[] get(Class<T> interfaceClass) {
		return me().getPlugins(interfaceClass);
	}

	private static PluginManager me() {
		return management;
	}

	public <T> T[] getPlugins(Class<T> interfaceClass) {
		List<Plugin> ps = plugins.get(interfaceClass);
		if (ps != null)
			return Lang.array2array(ps.toArray(), interfaceClass);
		return null;
	}

	public void reset() {
		for (List<Plugin> pluginList : plugins.values()) {
			for (Plugin nutPlugin : pluginList) {
				try {
					nutPlugin.depose();
				} catch (Throwable e) {}
			}
		}
		plugins.clear();
		loadPluginInfo(EXT_PLUGIN_FILENAME);
		loadPluginInfo(DEFAULT_PLUGIN_FILENAME);
	}

	private void loadPluginInfo(String fileName) {
		try {
			InputStream is = Files.findFileAsStream(fileName);
			if (is == null)
				return;
			Object obj = Json.fromJson(new InputStreamReader(is));
			if (obj instanceof List<?>) {
				List<?> classNameList = (List<?>) obj;
				for (Object object : classNameList) {
					loadPlugin(String.valueOf(object));
				}
			}
		} catch (Throwable e) {
			// Do noting
		}
	}

	private void loadPlugin(String pluginClassName) {
		try {
			Class<?> pluginClass = Class.forName(pluginClassName);
			Plugin nutPlugin = (Plugin) pluginClass.newInstance();
			if (nutPlugin.canWork()) {
				Class<?> workForClass = nutPlugin.getWorkType();
				if (workForClass == null)
					return;
				nutPlugin.init();
				List<Plugin> list = plugins.get(workForClass);
				if (list == null) {
					list = new ArrayList<Plugin>();
					plugins.put(workForClass, list);
				}
				list.add(nutPlugin);
			}
		} catch (Throwable e) {
			// Do nothing
		}
	}
}
