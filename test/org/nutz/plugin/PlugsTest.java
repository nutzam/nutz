package org.nutz.plugin;

import org.junit.Test;
import org.nutz.log.LogAdapter;

public class PlugsTest {

	@Test
	public void testGetPlugins() {
		// 暂时看不出有什么测试的必要
	}

	@Test(expected=NoPluginCanWorkException.class)
	public void testNoPlugin() throws InstantiationException, IllegalAccessException{
		PluginManager<LogAdapter> pluginManager 
			= new SimplePluginManager<LogAdapter>("nutz.noClass");
		pluginManager.get();
	}
	
	@SuppressWarnings("unchecked")
	@Test(expected=NoPluginCanWorkException.class)
	public void testNoPlugin2() throws InstantiationException, IllegalAccessException{
		PluginManager<LogAdapter> pluginManager 
			= new SimplePluginManager<LogAdapter>((Class<LogAdapter>)null);
		pluginManager.get();
	}
}
