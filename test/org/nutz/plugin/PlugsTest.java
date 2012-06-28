package org.nutz.plugin;

import static junit.framework.TestCase.*;

import org.junit.Test;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.NutIoc;
import org.nutz.ioc.loader.json.JsonLoader;
import org.nutz.log.Log;
import org.nutz.log.LogAdapter;
import org.nutz.log.impl.SystemLogAdapter;

public class PlugsTest {

    @Test(expected = NoPluginCanWorkException.class)
    public void testNoPlugin() throws InstantiationException, IllegalAccessException {
        PluginManager<LogAdapter> pluginManager = new SimplePluginManager<LogAdapter>("nutz.noClass");
        pluginManager.get();
    }

    @SuppressWarnings("unchecked")
    @Test(expected = NoPluginCanWorkException.class)
    public void testNoPlugin2() throws InstantiationException, IllegalAccessException {
        PluginManager<LogAdapter> pluginManager = new SimplePluginManager<LogAdapter>((Class<LogAdapter>) null);
        pluginManager.get();
    }
    
    @Test
    public void test_get_plugin_from_ioc(){
        Ioc ioc = new NutIoc(new JsonLoader("org/nutz/plugin/plugin.js"));
        PluginManager<Log> manager = new IocPluginManager<Log>(ioc, "pluB","pluA","pluC");
        assertNotNull(manager.get());
        assertTrue(manager.get() instanceof SystemLogAdapter);
    }
}
