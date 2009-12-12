package org.nutz.plugin;

import static org.junit.Assert.*;

import org.junit.Test;
import org.nutz.log.LogAdapter;

public class PlugsTest {

	@Test
	public void testGetPlugins() {
		Object [] objs = PluginManager.get(LogAdapter.class);
		assertNotNull(objs);
		assertTrue(objs.length > 0);
	}

}
