package org.nutz.mvc.testapp;

import java.io.File;
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
import org.nutz.lang.Files;

/**
 * 需要Jetty 6.1.26 的jar包
 * @author wendal
 *
 */
public abstract class BaseWebappTest {
	
	protected Server server;
	
	protected Response resp;

	@Before
	public void startServer() throws Throwable{
		String WEBAPPDIR = "org/nutz/mvc/testapp/ROOT";
		File root = Files.findFile(WEBAPPDIR);
		server = new Server(8888);
		String warUrlString = root.toURI().toURL().toExternalForm();
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
		resp = Http.get("http://localhost:8888"+getContextPath()+path);
		return resp;
	}
	
	public Response post(String path,Map<String, Object> params){
		resp = Sender.create(Request.create("http://localhost:8888"+getContextPath()+path, METHOD.POST, params, null)).send();
		return resp;
	}
}
