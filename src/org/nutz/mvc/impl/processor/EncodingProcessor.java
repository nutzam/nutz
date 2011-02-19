package org.nutz.mvc.impl.processor;

import org.nutz.mvc.ActionContext;
import org.nutz.mvc.impl.Processor;

public class EncodingProcessor implements Processor {

	private String input;

	private String output;

	public EncodingProcessor(String input, String output) {
		this.input = input;
		this.output = output;
	}

	public boolean process(ActionContext ac) throws Exception {
		ac.getRequest().setCharacterEncoding(input);
		ac.getResponse().setCharacterEncoding(output);
		return true;
	}

}
