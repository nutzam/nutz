package org.nutz.plugin;

public interface PluginManager<T> {

	T get() throws NoPluginCanWorkException;
	
}
