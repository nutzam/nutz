package org.nutz.mvc.impl;

import java.util.List;

import org.nutz.mvc.Processor;

public interface ActionChainMakerConfiguretion {

	public List<Class<? extends Processor>> getProcessors(String key);
	
	public Class<? extends Processor> getErrorProcessor(String key);
}
