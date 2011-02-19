package org.nutz.mvc;

import java.lang.reflect.Method;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.ioc.Ioc;
import org.nutz.lang.util.SimpleContext;

public class ActionContext extends SimpleContext {

	private static final String PATH = "path";
	private static final String PATH_ARGS = "pathArgs";

	// private static final String INPUT_ENCODING = "encoding.input";
	// private static final String OUTPUT_ENCODING = "encoding.output";

	private static final String REQUEST = HttpServletRequest.class.getName();
	private static final String RESPONSE = HttpServletResponse.class.getName();
	private static final String SERVLET_CONTEXT = ServletContext.class.getName();

	private static final String MODULE = "module";
	private static final String METHOD = "method";
	private static final String METHOD_ARGS = "method.args";
	private static final String METHOD_RETURN = "method.return";

	// private static final String ADAPTOR = "adaptor";

	// private static final String VIEW_OK = "view.ok";
	// private static final String VIEW_FAIL = "view.fail";

	// private static final String FILTERS = "filters";

	private static final String ERROR = "error";

	public Ioc getIoc() {
		return Mvcs.getIoc(getServletContext());
	}

	public Throwable getError() {
		return this.getAs(Throwable.class, ERROR);
	}

	public ActionContext setError(Throwable error) {
		this.set(ERROR, error);
		return this;
	}

	public String getPath() {
		return this.getString(PATH);
	}

	public ActionContext setPath(String ph) {
		this.set(PATH, ph);
		return this;
	}

	@SuppressWarnings("unchecked")
	public List<String> getPathArgs() {
		return this.getAs(List.class, PATH_ARGS);
	}

	public ActionContext setPathArgs(List<String> args) {
		this.set(PATH_ARGS, args);
		return this;
	}

	// public String getOutputEncoding() {
	// return this.getString(OUTPUT_ENCODING);
	// }
	//
	// public ActionContext setOutputEncoding(String en) {
	// this.set(OUTPUT_ENCODING, en);
	// return this;
	// }
	//
	// public String getInputEncoding() {
	// return this.getString(INPUT_ENCODING);
	// }
	//
	// public ActionContext setInputEncoding(String en) {
	// this.set(INPUT_ENCODING, en);
	// return this;
	// }

	public Method getMethod() {
		return this.getAs(Method.class, METHOD);
	}

	public ActionContext setMethod(Method m) {
		this.set(METHOD, m);
		return this;
	}

	public Object getModule() {
		return this.get(MODULE);
	}

	public ActionContext setModule(Object obj) {
		this.set(MODULE, obj);
		return this;
	}

	public Object[] getMethodArgs() {
		return this.getAs(Object[].class, METHOD_ARGS);
	}

	public ActionContext setMethodArgs(Object[] args) {
		this.set(METHOD_ARGS, args);
		return this;
	}

	public Object getMethodReturn() {
		return this.get(METHOD_RETURN);
	}

	public ActionContext setMethodReturn(Object re) {
		this.set(METHOD_RETURN, re);
		return this;
	}

	// public HttpAdaptor getAdaptor() {
	// return this.getAs(HttpAdaptor.class, ADAPTOR);
	// }
	//
	// public ActionContext setAdaptor(HttpAdaptor ad) {
	// this.set(ADAPTOR, ad);
	// return this;
	// }
	//
	// public View getOkView() {
	// return this.getAs(View.class, VIEW_OK);
	// }
	//
	// public ActionContext setOkView(View ok) {
	// this.set(VIEW_OK, ok);
	// return this;
	// }
	//
	// public View getFailView() {
	// return this.getAs(View.class, VIEW_FAIL);
	// }
	//
	// public ActionContext setFailView(View fail) {
	// this.set(VIEW_FAIL, fail);
	// return this;
	// }
	//
	// public ActionFilter[] getFilters() {
	// return this.getAs(ActionFilter[].class, FILTERS);
	// }
	//
	// public ActionContext setFilters(ActionFilter[] fs) {
	// this.set(FILTERS, fs);
	// return this;
	// }

	public HttpServletRequest getRequest() {
		return this.getAs(HttpServletRequest.class, REQUEST);
	}

	public ActionContext setRequest(HttpServletRequest req) {
		this.set(REQUEST, req);
		return this;
	}

	public HttpServletResponse getResponse() {
		return this.getAs(HttpServletResponse.class, RESPONSE);
	}

	public ActionContext setResponse(HttpServletResponse resp) {
		this.set(RESPONSE, resp);
		return this;
	}

	public ServletContext getServletContext() {
		return this.getAs(ServletContext.class, SERVLET_CONTEXT);
	}

	public ActionContext setServletContext(ServletContext sc) {
		this.set(SERVLET_CONTEXT, sc);
		return this;
	}

	public ActionContext clone() {
		ActionContext re = new ActionContext();
		re.putAll(this);
		return re;
	}

}
