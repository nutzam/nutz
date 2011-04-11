package org.nutz.mvc.testapp;

import java.net.URL;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.webapp.WebAppContext;
import org.nutz.http.Http;
import org.nutz.http.Request;
import org.nutz.http.Request.METHOD;
import org.nutz.http.Response;
import org.nutz.http.Sender;

/**
 * 需要Jetty 6.1.26 的jar包
 * @author wendal
 *
 */
public abstract class BaseWebappTest {
	
	protected Server server;

	@Before
	public void startServer() throws Throwable{
		String WEBAPPDIR = "org/nutz/mvc/testapp/ROOT";
		server = new Server(8888);
		URL warUrl = BaseWebappTest.class.getClassLoader().getResource(WEBAPPDIR);
		String warUrlString = warUrl.toExternalForm();
		server.setHandler(new WebAppContext(warUrlString, getContextPath()));
		server.start();
	}
	
	@After
	public void shutdownServer() throws Throwable{
		server.stop();
	}
	
	public String getContextPath(){
		return "/nutztest";
	}
	
	public Response get(String path){
		return Http.get("http://localhost:8888"+getContextPath()+path);
	}
	
	public Response post(String path,Map<String, Object> params){
		return Sender.create(Request.create("http://localhost:8888"+getContextPath()+path, METHOD.POST, params, null)).send();
	}
}
