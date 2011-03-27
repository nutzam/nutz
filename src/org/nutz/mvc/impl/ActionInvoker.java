package org.nutz.mvc.impl;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.mvc.ActionChain;
import org.nutz.mvc.ActionContext;

/**
 * 根据 HTTP 请求的方法 (GET|POST|PUT|DELETE) 来调用响应的动作链
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class ActionInvoker {

	private ActionChain defaultChain;

	private Map<String, ActionChain> chainMap;

	public ActionInvoker() {
		chainMap = new HashMap<String, ActionChain>();
	}

	/**
	 * 增加 ActionChain
	 * 
	 * @param httpMethod
	 *            HTTP 的请求方法 (GET|POST|PUT|DELETE),如果为空，则会抛错
	 * @param chain
	 *            动作链
	 */
	public void addChain(String httpMethod, ActionChain chain) {
		if (Strings.isBlank(httpMethod))
			throw Lang.makeThrow("chain need a valid HTTP Method, but is is '%s'", httpMethod);
		chainMap.put(httpMethod.toUpperCase(), chain);
	}

	public void setDefaultChain(ActionChain defaultChain) {
		this.defaultChain = defaultChain;
	}

	/**
	 * 根据动作链上下文对象，调用一个相应的动作链
	 * 
	 * @param ac
	 *            动作链上下文
	 * @return true- 成功的找到一个动作链并执行。 false- 没有找到动作链
	 */
	public boolean invoke(ActionContext ac) {
		HttpServletRequest req = ac.getRequest();
		String httpMethod = Strings.sNull(req.getMethod(), "GET").toUpperCase();
		ActionChain chain = chainMap.get(httpMethod);
		// 找到了特殊HTTP方法的处理动作链
		if (null != chain) {
			chain.doChain(ac);
			return true;
		}
		// 这个 URL 所有的HTTP方法用统一的动作链处理
		else if (null != defaultChain) {
			defaultChain.doChain(ac);
			return true;
		}
		// 否则将认为不能处理
		return false;
	}

	public String toString() {
		return String.format(	"%s&%s",
								defaultChain == null ? "----" : "DEFT",
								Json.toJson(chainMap.keySet(), JsonFormat.compact()));
	}

}
