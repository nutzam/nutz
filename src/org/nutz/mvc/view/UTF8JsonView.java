package org.nutz.mvc.view;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.json.JsonFormat;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.View;

/**
 * 将数据采用json方式输出的试图实现
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * @author mawn(ming300@gmail.com)
 * @author wendal(wendal1985@gmail.com)
 */
public class UTF8JsonView implements View {

    public static String CT = "application/json";
    public static String JSONP_CT = "application/javascript";

    protected JsonFormat format;

    protected Object data;

    protected boolean jsonp;

    protected String jsonpParam;

    public UTF8JsonView setData(Object data) {

        this.data = data;
        return this;
    }

    public UTF8JsonView setJsonp(boolean jsonp) {
        this.jsonp = jsonp;
        return this;
    }

    public UTF8JsonView setJsonpParam(String jsonpParam) {
        this.jsonpParam = jsonpParam;
        return this;
    }

    public UTF8JsonView(JsonFormat format) {
        this.format = format;
    }
    
    public UTF8JsonView() {
    	this.format = new JsonFormat(false);
	}

    public void render(HttpServletRequest req, HttpServletResponse resp, Object obj)
            throws IOException {

        if (resp.getContentType() == null)
            if (jsonp)
                resp.setContentType(JSONP_CT);
            else
                resp.setContentType(CT);
        Writer writer = resp.getWriter();
        if (jsonp)
            writer.write(req.getParameter(jsonpParam == null ? "callback" : jsonpParam) + "(");
        Mvcs.write(resp, writer, null == obj ? data : obj, format);
        if (jsonp)
            writer.write(");");
    }

    public static final View NICE = new UTF8JsonView(JsonFormat.nice());
    public static final View COMPACT = new UTF8JsonView(JsonFormat.compact());
    public static final View FULL = new UTF8JsonView(JsonFormat.full());
    public static final View FORLOOK = new UTF8JsonView(JsonFormat.forLook());
    public static final View JSONP = new UTF8JsonView(JsonFormat.compact()).setJsonp(true);
}
