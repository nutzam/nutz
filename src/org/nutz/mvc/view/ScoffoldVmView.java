package org.nutz.mvc.view;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.lang.Encoding;
import org.nutz.lang.Strings;
import org.nutz.lang.util.Context;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.View;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class ScoffoldVmView implements View{

	
	private String viewName = null;
	
	public ScoffoldVmView(String viewName){
		this.viewName = viewName;
	}
	@Override
	public void render(HttpServletRequest req, HttpServletResponse resp,
			Object obj) throws Throwable {
		req.setAttribute("scoffold_viewName", viewName);
		req.setAttribute("obj", obj);
		new JspView("views.scoffold").render(req, resp, obj);
	}
}
