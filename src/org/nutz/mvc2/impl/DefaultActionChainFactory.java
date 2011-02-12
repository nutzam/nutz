package org.nutz.mvc2.impl;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.mvc.init.NutConfig;
import org.nutz.mvc2.ActionChain;
import org.nutz.mvc2.ActionChainFactory;
import org.nutz.mvc2.ActionNode;

public class DefaultActionChainFactory implements ActionChainFactory {
	
	public ActionChain make(HttpServletRequest req, HttpServletResponse resp, ServletContext servletContext) {
		ActionChain chain = new ActionChainImpl(new ArrayList<ActionNode>(list));
		chain.put(ActionFilters.request, req);
		chain.put(ActionFilters.response, resp);
		chain.put(ActionFilters.servletContent, servletContext);
		return chain;
	}
	
	public void init(NutConfig config) {
		list.add(new UpdateRequestAttributesFilter());
		list.add(new UriMappingNode());
		list.add(new EncodingNode());
		list.add(new ViewNode());
		list.add(new OldActionFilterNode());
		list.add(new HttpAdaptorFilter());
		list.add(new ActionInvokeObjectNode());
		list.add(new MethodInvokeActionNode());
		
		for (ActionNode node : list) {
			try {
				node.init(config);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}
	
	public void destroy() {
		for (ActionNode node : list) {
			try {
				node.depose();
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
		list = null;
	}

	List<ActionNode> list = new ArrayList<ActionNode>();

}
