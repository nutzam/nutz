package org.nutz.mvc.impl;

import java.util.ArrayList;
import java.util.List;

import org.nutz.mvc.Processor;
import org.nutz.mvc.impl.processor.ActionFiltersProcessor;
import org.nutz.mvc.impl.processor.AdaptorProcessor;
import org.nutz.mvc.impl.processor.EncodingProcessor;
import org.nutz.mvc.impl.processor.FailProcessor;
import org.nutz.mvc.impl.processor.MethodInvokeProcessor;
import org.nutz.mvc.impl.processor.ModuleProcessorFactory;
import org.nutz.mvc.impl.processor.UpdateRequestAttributesProcessor;
import org.nutz.mvc.impl.processor.ViewProcessor;

public class DefaultActionChainMakerConfiguretion implements ActionChainMakerConfiguretion {

	protected List<Class<? extends Processor>> processors = new ArrayList<Class<? extends Processor>>();
	
	protected Class<? extends Processor> errorProcessor;
	
	private DefaultActionChainMakerConfiguretion() {
		
		processors.add(UpdateRequestAttributesProcessor.class);
		processors.add(EncodingProcessor.class);
		processors.add(ModuleProcessorFactory.class);
		processors.add(ActionFiltersProcessor.class);
		processors.add(AdaptorProcessor.class);
		processors.add(MethodInvokeProcessor.class);
		processors.add(ViewProcessor.class);
		
		errorProcessor = FailProcessor.class;
	}
	
	public List<Class<? extends Processor>> getProcessors(String key) {
		return processors;
	}
	
	public Class<? extends Processor> getErrorProcessor(String key) {
		return errorProcessor;
	}
	
	private static final ActionChainMakerConfiguretion me = new DefaultActionChainMakerConfiguretion();
	
	public static final ActionChainMakerConfiguretion me(){
		return me;
	}
}
