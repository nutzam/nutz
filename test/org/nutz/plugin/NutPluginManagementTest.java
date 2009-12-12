package org.nutz.plugin;

import static org.junit.Assert.*;

import org.junit.Test;
import org.nutz.log.LogAdapter;

public class NutPluginManagementTest {

	@Test
	public void testGetPlugins() {
		Object [] objs = NutPluginManagement.getPlugins(LogAdapter.class);
		assertNotNull(objs);
		assertTrue(objs.length > 0);
	}

}
