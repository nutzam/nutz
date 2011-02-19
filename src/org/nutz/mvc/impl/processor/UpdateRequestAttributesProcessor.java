package org.nutz.mvc.impl.processor;

import org.nutz.mvc.ActionContext;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.impl.Processor;

/**
 * 更新 Request 中的属性，增加诸如 '${base}', '${msg}' 等属性，以便 JSP 网页访问
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class UpdateRequestAttributesProcessor implements Processor {

	public boolean process(ActionContext ac) {
		Mvcs.updateRequestAttributes(ac.getRequest());
		return true;
	}

}
