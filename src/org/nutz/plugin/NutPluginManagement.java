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
 * 
 * @author Wendal(wendal1985@gmail.com)
 *
 */
public final class NutPluginManagement {
	
	private Map<Class<?>,List<NutPlugin>> plugins = new HashMap<Class<?>,List<NutPlugin>>();

	public static final String DEFAULT_PLUGIN_FILENAME = "nutz-default-plugin.json";
	public static final String EXT_PLUGIN_FILENAME = "nutz-ext-plugin.json";
	
	private NutPluginConfig nutPluginConfig;
	
	private static NutPluginManagement management = new NutPluginManagement();
	
	private NutPluginManagement() {
		loadPluginInfo(EXT_PLUGIN_FILENAME);
		loadPluginInfo(DEFAULT_PLUGIN_FILENAME);
	}
	
	public static NutPluginManagement me() {
		return management;
	}

	public <T> T [] getPlugins(Class<T> interfaceClass){
		List<NutPlugin> ps = plugins.get(interfaceClass);
		if(ps != null)
			return Lang.array2array(ps.toArray(), interfaceClass);
		return null;
	}
	
	public void reset(){
		for (List<NutPlugin> pluginList : plugins.values()) {
			for (NutPlugin nutPlugin : pluginList) {
				try{
					nutPlugin.depose(nutPluginConfig);
				}catch (Throwable e) {
				}
			}
		}
		plugins.clear();
		loadPluginInfo(EXT_PLUGIN_FILENAME);
		loadPluginInfo(DEFAULT_PLUGIN_FILENAME);
	}

	private void loadPluginInfo(String fileName) {
		try {
			InputStream is = Files.findFileAsStream(fileName);
			if(is == null)
				return;
			Object obj = Json.fromJson(new InputStreamReader(is));
			if(obj instanceof List<?>){
				List<?> classNameList = (List<?>)obj;
				for (Object object : classNameList) {
					loadPlugin(String.valueOf(object));
				}
			}
		} catch (Throwable e) {
			// Do noting
		}
	}

	private void loadPlugin(String pluginClassName){
		try{
			Class<?> pluginClass = Class.forName(pluginClassName);
			NutPlugin nutPlugin = (NutPlugin) pluginClass.newInstance();
			if(nutPlugin.canWork(nutPluginConfig)){
				Class<?> workForClass = nutPlugin.workFor();
				if(workForClass == null)
					return;
				nutPlugin.init(nutPluginConfig);
				List<NutPlugin> list = plugins.get(workForClass);
				if(list == null){
					list = new ArrayList<NutPlugin>();
					plugins.put(workForClass, list);
				}
				list.add(nutPlugin);
			}
		}catch (Throwable e) {
			// Do nothing
		}
	}
}
