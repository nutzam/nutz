package org.nutz.mvc.impl;

import java.util.ArrayList;
import java.util.List;

import org.nutz.lang.Lang;
import org.nutz.mvc.ActionChain;
import org.nutz.mvc.ActionChainMaker;
import org.nutz.mvc.ActionInfo;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.Processor;
import org.nutz.mvc.impl.chainconfig.ActionChainMakerConfiguretion;
import org.nutz.mvc.impl.chainconfig.JsonActionChainMakerConfiguretion;

public class NutActionChainMaker implements ActionChainMaker {
	
	ActionChainMakerConfiguretion co;
	
	public NutActionChainMaker(String...args) {
		co = new JsonActionChainMakerConfiguretion(args);
	}

	public ActionChain eval(NutConfig config, ActionInfo ai) {
		
		try {
			List<Processor> list = new ArrayList<Processor>();
			for (String name : co.getProcessors(ai.getChainName())) {
				Processor processor = getProcessorByName(config, name);
				processor.init(config, ai);
				list.add(processor);
			}

			Processor errorProcessor = getProcessorByName(config, co.getErrorProcessor(ai.getChainName()));
			errorProcessor.init(config, ai);
			/*
			 * 返回动作链实例
			 */
			return new NutActionChain(list, errorProcessor);
		} catch (Throwable e) {
			throw Lang.wrapThrow(e);
		}
	}

	protected static Processor getProcessorByName(NutConfig config,String name) throws Exception {
		if (name.startsWith("ioc:") && name.length() > 4)
			return config.getIoc().get(Processor.class, name.substring(4));
		else
			return (Processor) Lang.loadClass(name).newInstance();
	}
}
