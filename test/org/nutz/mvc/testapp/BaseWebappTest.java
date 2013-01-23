package org.nutz.mvc.testapp;

import static org.junit.Assert.*;

import java.net.URL;
import java.util.Map;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.After;
import org.junit.Before;
import org.nutz.http.Http;
import org.nutz.http.Request;
import org.nutz.http.Request.METHOD;
import org.nutz.http.Response;
import org.nutz.http.Sender;

/**
 * 需要Jetty 7.3.1 的jar包
 * 
 * @author wendal
 * 
 */
public abstract class BaseWebappTest {

    protected Server server;

    protected Response resp;

    private boolean isRunInMaven = false;

    private String serverURL = "http://localhost:8888";

    {
        for (StackTraceElement ste : Thread.currentThread().getStackTrace()) {
            if (ste.getClassName().startsWith("org.apache.maven.surefire")) {
                isRunInMaven = true;
                serverURL = "http://nutztest.herokuapp.com";
                break;
            }
        }
    }

    @Before
    public void startServer() throws Throwable {
        if (!isRunInMaven) {
            try {
                URL url = getClass().getClassLoader().getResource("org/nutz/mvc/testapp/Root/FLAG");
                String path = url.toExternalForm();
                System.err.println(url);
                server = new Server(8888);
                String warUrlString = path.substring(0, path.length() - 4);
                server.setHandler(new WebAppContext(warUrlString, getContextPath()));
                server.start();
            }
            catch (Throwable e) {
                if (server != null)
                    server.stop();
                throw e;
            }
        }
    }

    @After
    public void shutdownServer() throws Throwable {
        if (!isRunInMaven) {
            if (server != null)
                server.stop();
        }
    }

    public String getContextPath() {
        return "/nutztest";
    }

    public String getBaseURL() {
        return serverURL + getContextPath();
    }

    public Response get(String path) {
        resp = Http.get(getBaseURL() + path);
        assertNotNull(resp);
        return resp;
    }

    public Response post(String path, Map<String, Object> params) {
        resp = Sender.create(Request.create(getBaseURL() + path, METHOD.POST, params, null)).send();
        assertNotNull(resp);
        return resp;
    }

    public Response post(String path, String data) {
        Request req = Request.create(getBaseURL() + path, METHOD.POST);
        req.setData(data);
        resp = Sender.create(req).send();
        assertNotNull(resp);
        return resp;
    }

    public Response post(String path, byte[] bytes) {
        Request req = Request.create(getBaseURL() + path, METHOD.POST);
        req.setData(bytes);
        resp = Sender.create(req).send();
        assertNotNull(resp);
        return resp;
    }
}
