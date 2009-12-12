package org.nutz.plugin;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nutz.json.Json;
import org.nutz.lang.Files;
import org.nutz.log.LogFactory;

/**
 * 
 * @author Wendal(wendal1985@gmail.com)
 *
 */
public final class NutPluginManagement {
	
	private static Map<Class<?>,List<NutPlugin>> plugins = new HashMap<Class<?>,List<NutPlugin>>();

	public static final String DEFAULT_PLUGIN_FILENAME = "nutz-default-plugin.json";
	public static final String EXT_PLUGIN_FILENAME = "nutz-ext-plugin.json";
	
	private static NutPluginConfig nutPluginConfig;

	public static Object [] getPlugins(Class<?> interfaceClass){
		List<NutPlugin> ps = plugins.get(interfaceClass);
		if(ps != null)
			return ps.toArray();
		return null;
	}
	
	public static void reset(){
		try{
			for (List<NutPlugin> pluginList : plugins.values()) {
				for (NutPlugin nutPlugin : pluginList) {
					nutPlugin.depose(nutPluginConfig);
				}
			}
		}catch (Throwable e) {
		}finally{
			plugins.clear();
			loadPluginInfo(EXT_PLUGIN_FILENAME);
			loadPluginInfo(DEFAULT_PLUGIN_FILENAME);
		}
	}

	static{
		loadPluginInfo(EXT_PLUGIN_FILENAME);
		loadPluginInfo(DEFAULT_PLUGIN_FILENAME);
	}

	private static void loadPluginInfo(String fileName) {
		try {
			InputStream is = Files.findFileAsStream(fileName);
			if(is == null)
				return;
			Object obj = Json.fromJson(new InputStreamReader(is));
			if(obj instanceof List){
				List<?> classNameList = (List<?>)obj;
				for (Object object : classNameList) {
					loadPlugin(String.valueOf(object));
				}
			}
		} catch (Throwable e) {
			// Do noting
		}
	}

	private static void loadPlugin(String pluginClassName){
		try{
			Class<?> pluginClass = Class.forName(pluginClassName);
			NutPlugin nutPlugin = (NutPlugin) pluginClass.newInstance();
			if(nutPlugin.canWork(nutPluginConfig)){
				nutPlugin.init(nutPluginConfig);
				Class<?> workForClass = nutPlugin.workFor();
				List<NutPlugin> list = plugins.get(workForClass);
				if(list == null){
					list = new ArrayList<NutPlugin>();
					plugins.put(workForClass, list);
				}
				list.add(nutPlugin);
			}
		}catch (Throwable e) {
			LogFactory.getLog(NutPluginManagement.class).warnf("Fail to load plugin: %s",pluginClassName);
		}
	}
}
