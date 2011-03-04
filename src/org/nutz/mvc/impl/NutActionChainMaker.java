package org.nutz.mvc.impl;

import java.util.ArrayList;
import java.util.List;

import org.nutz.lang.Lang;
import org.nutz.mvc.ActionChain;
import org.nutz.mvc.ActionChainMaker;
import org.nutz.mvc.ActionInfo;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.Processor;

public class NutActionChainMaker implements ActionChainMaker {
	
	ActionChainMakerConfiguretion co;
	
	public NutActionChainMaker(String...args) {
		//TODO 根据参数加载一种配置文件,以配置动作链
		/*	<code>
		 * {
		 *     default : {
		 *     		ps : ['asfd','asdf','qwt'],
		 *          error : ''
		 *     },
		 *     abc : {
		 *     		ps : ['xxxx','asdfa']
		 *     },
		 *     ccc : {
		 *     		error : abc
		 *     }
		 * }
		 * </code>
		 */
		co = DefaultActionChainMakerConfiguretion.me();
	}

	public ActionChain eval(NutConfig config, ActionInfo ai) {
		
		try {
			List<Processor> list = new ArrayList<Processor>(7);
		
			for (Class<? extends Processor> className : co.getProcessors(ai.getChainName())) {
				Processor processor = className.newInstance();
				processor.init(config, ai);
				list.add(processor);
			}

			Processor errorProcessor = co.getErrorProcessor(ai.getChainName()).newInstance();
			errorProcessor.init(config, ai);
			/*
			 * 返回动作链实例
			 */
			return new NutActionChain(list, errorProcessor);
		} catch (Throwable e) {
			throw Lang.wrapThrow(e);
		}
	}

}
