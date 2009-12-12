package org.nutz.plugin;

public interface NutPlugin {

	boolean canWork(NutPluginConfig config);
	
	Class<?> workFor();
	
	void init(NutPluginConfig config) throws Throwable;
	
	void depose(NutPluginConfig config) throws Throwable;
}
